/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package de.quantummaid.mapmaid.builder.recipes.manualregistry;

import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.ModifierFieldDetector.modifierBased;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer.methodNameDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields.serializationFields;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.types.resolver.ResolvedField.resolvedPublicFields;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManualRegistry implements Recipe {
    private static final FieldDetector FIELD_DETECTOR = modifierBased();

    private final List<Definition> definitions = new LinkedList<>();
    private final List<Class<?>> manuallyAddedCustomPrimitiveTypes = new LinkedList<>();
    private final List<Class<?>> manuallyAddedSerializedObjectTypes = new LinkedList<>();

    public static ManualRegistry manuallyRegisteredTypes() {
        return new ManualRegistry();
    }

    public <T> ManualRegistry withCustomPrimitive(final Class<T> type,
                                                  final Function<T, String> serializationMethod,
                                                  final Function<String, T> deserializationMethod) {
        return this.withCustomPrimitive(generalDefinition(
                fromClassWithoutGenerics(type),
                (CustomPrimitiveSerializer) object -> serializationMethod.apply(type.cast(object)),
                (CustomPrimitiveDeserializer) value -> deserializationMethod.apply((String) value)));
    }

    public ManualRegistry withCustomPrimitive(final Definition customPrimitive) {
        if (this.definitions.contains(customPrimitive)) {
            throw new UnsupportedOperationException(format(
                    "The customPrimitive %s has already been added for type %s", customPrimitive, customPrimitive.type().description()));
        }
        this.definitions.add(customPrimitive);

        return this;
    }

    public ManualRegistry withCustomPrimitives(final Class<?>... customPrimitiveTypes) {
        stream(customPrimitiveTypes).forEach(type -> validateNotNull(type, "type"));
        this.manuallyAddedCustomPrimitiveTypes.addAll(asList(customPrimitiveTypes));
        return this;
    }

    public ManualRegistry withSerializedObject(final Definition serializedObject) {
        if (this.definitions.contains(serializedObject)) {
            throw new UnsupportedOperationException(format("The serializedObject %s has already been added for type %s",
                    serializedObject, serializedObject.type().description()));
        }
        this.definitions.add(serializedObject);
        return this;
    }

    public ManualRegistry withSerializedObject(final Class<?> type,
                                               final Field[] serializedFields,
                                               final String deserializationMethodName) {
        final ClassType fullType = fromClassWithoutGenerics(type);
        final List<ResolvedField> resolvedFields = resolvedPublicFields(fullType);
        final TypeDeserializer deserializer = methodNameDeserializer(fullType, deserializationMethodName, resolvedFields);
        final SerializationFields serializationFields = serializationFields(FIELD_DETECTOR.detect(fullType));
        final SerializedObjectSerializer serializer = serializedObjectSerializer(serializationFields).orElseThrow();

        final Definition serializedObject = generalDefinition(fullType, serializer, deserializer);
        return this.withSerializedObject(serializedObject);
    }

    public ManualRegistry withSerializedObjects(final Class<?>... serializedObjectTypes) {
        stream(serializedObjectTypes).forEach(type -> validateNotNull(type, "type"));
        this.manuallyAddedSerializedObjectTypes.addAll(asList(serializedObjectTypes));
        return this;
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        this.definitions.forEach(mapMaidBuilder::withManuallyAddedDefinition);
        this.manuallyAddedCustomPrimitiveTypes.forEach(mapMaidBuilder::withManuallyAddedType);
        this.manuallyAddedSerializedObjectTypes.forEach(mapMaidBuilder::withManuallyAddedType);
    }
}
