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

package de.quantummaid.mapmaid.builder.detection.customprimitive;

import de.quantummaid.mapmaid.builder.detection.DeserializerFactory;
import de.quantummaid.mapmaid.builder.detection.SerializerFactory;
import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.customprimitive.CachedReflectionType.cachedReflectionType;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveDefinitionFactory implements SerializerFactory, DeserializerFactory {
    private final List<CustomPrimitiveSerializationDetector> serializationDetectors;
    private final List<CustomPrimitiveDeserializationDetector> deserializationDetectors;

    public static CustomPrimitiveDefinitionFactory customPrimitiveFactory(
            final List<CustomPrimitiveSerializationDetector> serializationDetectors,
            final List<CustomPrimitiveDeserializationDetector> deserializationDetectors) {
        validateNotNull(serializationDetectors, "serializationDetectors");
        validateNotNull(deserializationDetectors, "deserializationDetectors");
        return new CustomPrimitiveDefinitionFactory(serializationDetectors, deserializationDetectors);
    }

    @Override
    public List<Prioritized<TypeDeserializer>> analyseForDeserializer(final ResolvedType type) {
        final CachedReflectionType cachedReflectionType = cachedReflectionType(type.assignableType()); // TODO
        return this.deserializationDetectors.stream()
                .map(detector -> detector.detect(cachedReflectionType))
                .flatMap(List::stream)
                .collect(toList());
    }

    @Override
    public List<TypeSerializer> analyseForSerializer(final ResolvedType type) {
        final CachedReflectionType cachedReflectionType = cachedReflectionType(type.assignableType()); // TODO
        return this.serializationDetectors.stream()
                .map(detector -> detector.detect(cachedReflectionType))
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
