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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject;

import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.UserProvidedDeserializer.userProvidedDeserializer;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField.serializationField;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields.serializationFields;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder implements Recipe {
    private final ResolvedType type;
    private final List<Field> fields;
    private InvocableDeserializer<?> deserializer;

    public static Builder emptyBuilder(final Class<?> type) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return new Builder(resolvedType, smallList());
    }

    public void addField(final Field field) {
        validateNotNull(field, "field");
        this.fields.add(field);
    }

    public void setDeserializer(final InvocableDeserializer<?> deserializer) {
        validateNotNull(deserializer, "deserializer");
        this.deserializer = deserializer;
    }

    public Definition create() {
        final SerializedObjectDeserializer deserializer = userProvidedDeserializer(this.deserializer, this.fields);
        final List<SerializationField> serializationFieldList = this.fields.stream()
                .map(field -> serializationField(field.type(), field.name(), field::query))
                .collect(Collectors.toList());
        final SerializationFields serializationFields = serializationFields(serializationFieldList);
        final SerializedObjectSerializer serializer = serializedObjectSerializer(serializationFields);
        final Definition definition = generalDefinition(this.type, serializer, deserializer);
        return definition;
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        final Definition definition = create();
        mapMaidBuilder.withManuallyAddedDefinition(definition);
    }
}
