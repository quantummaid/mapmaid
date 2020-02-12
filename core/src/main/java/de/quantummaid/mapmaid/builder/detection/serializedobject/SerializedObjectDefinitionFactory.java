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

package de.quantummaid.mapmaid.builder.detection.serializedobject;

import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.builder.detection.DeserializerFactory;
import de.quantummaid.mapmaid.builder.detection.SerializerFactory;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SerializedObjectDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.serializedobject.ClassFilter.allowAll;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.CodeNeedsToBeCompiledWithParameterNamesException.validateParameterNamesArePresent;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions.serializationFieldOptions;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectDefinitionFactory implements SerializerFactory, DeserializerFactory {
    private final ClassFilter filter;
    private final List<FieldDetector> fieldDetectors;
    private final List<SerializedObjectDeserializationDetector> detectors;

    public static DefinitionFactory serializedObjectFactory(
            final List<SerializedObjectDeserializationDetector> detectors) {
        return serializedObjectFactory(allowAll(), detectors);
    }

    public static DefinitionFactory serializedObjectFactory(
            final ClassFilter filter,
            final List<SerializedObjectDeserializationDetector> detectors) {
        throw new UnsupportedOperationException(); // TODO
        //return serializedObjectFactory(filter, singletonList(ModifierFieldDetector.modifierBased()), detectors);
    }

    public static SerializedObjectDefinitionFactory serializedObjectFactory(
            final ClassFilter filter,
            final List<FieldDetector> fieldDetectors,
            final List<SerializedObjectDeserializationDetector> detectors) {
        validateNotNull(filter, "filter");
        validateNotNull(fieldDetectors, "fieldDetectors");
        validateNotNull(detectors, "detectors");
        return new SerializedObjectDefinitionFactory(filter, fieldDetectors, detectors);
    }

    @Override
    public List<Prioritized<TypeDeserializer>> analyseForDeserializer(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        if (!this.filter.filter(type)) {
            return emptyList();
        }
        if (Collection.class.isAssignableFrom(type.assignableType())) {
            return emptyList(); // TODO
        }
        //validateParameterNamesArePresent(type.assignableType()); TODO

        return this.detectors.stream()
                .map(detector -> detector.detect(type))
                .flatMap(List::stream)
                .collect(toList());
    }

    @Override
    public List<TypeSerializer> analyseForSerializer(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        if (!this.filter.filter(type)) {
            return emptyList();
        }

        final ClassType classType = (ClassType) type;
        final SerializationFieldOptions serializationFieldOptions = serializationFieldOptions();
        this.fieldDetectors.stream()
                .map(fieldDetector -> fieldDetector.detect(classType))
                .flatMap(Collection::stream)
                .forEach(serializationFieldOptions::add);
        if (serializationFieldOptions.isEmpty()) {
            return emptyList();
        }
        return List.of(serializationFieldOptions);
    }
}
