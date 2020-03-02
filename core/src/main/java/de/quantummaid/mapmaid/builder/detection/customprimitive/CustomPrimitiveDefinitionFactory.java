/*
 * Copyright (c) 2020 Richard Hauswald - https://quantummaid.de/.
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

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.customprimitive.CachedReflectionType.cachedReflectionType;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveDefinitionFactory implements DefinitionFactory {
    private final CustomPrimitiveSerializationDetector serializationDetector;
    private final List<CustomPrimitiveDeserializationDetector> deserializationDetectors;

    public static CustomPrimitiveDefinitionFactory customPrimitiveFactory(
            final CustomPrimitiveSerializationDetector serializationDetector,
            final CustomPrimitiveDeserializationDetector... deserializationDetectors) {
        validateNotNull(serializationDetector, "serializationDetector");
        validateNotNull(deserializationDetectors, "deserializationDetectors");
        return new CustomPrimitiveDefinitionFactory(serializationDetector, asList(deserializationDetectors));
    }

    @Override
    public Optional<Definition> analyze(final ResolvedType type,
                                        final RequiredCapabilities capabilities) {
        final CachedReflectionType cachedReflectionType = cachedReflectionType(type.assignableType());

        final Optional<CustomPrimitiveSerializer> serializer;
        if (capabilities.hasSerialization()) {
            serializer = this.serializationDetector.detect(cachedReflectionType);
        } else {
            serializer = empty();
        }

        final Optional<CustomPrimitiveDeserializer> deserializer;
        if (capabilities.hasDeserialization()) {
            deserializer = this.deserializationDetectors.stream()
                    .map(detector -> detector.detect(cachedReflectionType))
                    .flatMap(Optional::stream)
                    .findFirst();
        } else {
            deserializer = empty();
        }

        if (serializer.isPresent() || deserializer.isPresent()) {
            return of(generalDefinition(type, serializer.orElse(null), deserializer.orElse(null))
            );
        }
        return empty();
    }
}
