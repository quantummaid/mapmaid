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

package de.quantummaid.mapmaid.builder.detection;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewSimpleDetector {
    private final List<SerializerFactory> serializerFactories;
    private final List<DeserializerFactory> deserializerFactories;

    public static NewSimpleDetector detector(final List<SerializerFactory> serializerFactories,
                                             final List<DeserializerFactory> deserializerFactories) {
        validateNotNull(serializerFactories, "serializerFactories");
        validateNotNull(deserializerFactories, "deserializerFactories");
        return new NewSimpleDetector(serializerFactories, deserializerFactories);
    }

    // TODO return value
    public DetectionResult<DisambiguationResult> detect(final ResolvedType type,
                                                        final BuildContextLog parentLog,
                                                        final ScanInformationBuilder scanInformationBuilder,
                                                        final RequiredCapabilities capabilities,
                                                        final Disambiguators disambiguators) {
        if (!isSupported(type)) {
            parentLog.logReject(type, "type is not supported because it contains wildcard generics (\"?\")");
            return failure(format("type '%s' is not supported because it contains wildcard generics (\"?\")", type.description()));
        }

        scanInformationBuilder.resetScan();

        final List<TypeSerializer> serializers;
        if (capabilities.hasSerialization()) {
            serializers = this.serializerFactories.stream()
                    .map(serializerFactory -> serializerFactory.analyseForSerializer(type))
                    .flatMap(Collection::stream)
                    .collect(toList());
            serializers.forEach(scanInformationBuilder::addSerializer);
        } else {
            serializers = null;
        }

        final List<TypeDeserializer> deserializers;
        if(capabilities.hasDeserialization()) {
            deserializers = this.deserializerFactories.stream()
                    .map(deserializerFactory -> deserializerFactory.analyseForDeserializer(type))
                    .flatMap(Collection::stream)
                    .sorted()
                    .map(Prioritized::value)
                    .collect(toList());
            deserializers.forEach(scanInformationBuilder::addDeserializer);
        } else {
            deserializers = null;
        }

        final Disambiguator disambiguator = disambiguators.disambiguatorFor(type);
        final SerializersAndDeserializers serializersAndDeserializers = serializersAndDeserializers(serializers, deserializers);
        return disambiguator.disambiguate(type, serializersAndDeserializers, scanInformationBuilder);
    }

    private static boolean isSupported(final ResolvedType resolvedType) {
        if (resolvedType.isWildcard()) {
            return false;
        }
        return resolvedType.typeParameters().stream()
                .allMatch(NewSimpleDetector::isSupported);
    }
}
