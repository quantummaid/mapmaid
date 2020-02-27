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
import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SerializedObjectDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions.serializationFieldOptions;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions.serializedObjectOptions;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleDetector {
    private final List<FieldDetector> fieldDetectors;
    private final List<SerializedObjectDeserializationDetector> serializedObjectDeserializationDetectors;
    private final List<CustomPrimitiveSerializationDetector> customPrimitiveSerializationDetectors;
    private final List<CustomPrimitiveDeserializationDetector> customPrimitiveDeserializationDetectors;

    public static SimpleDetector detector(final List<FieldDetector> fieldDetectors,
                                          final List<SerializedObjectDeserializationDetector> serializedObjectDeserializationDetectors,
                                          final List<CustomPrimitiveSerializationDetector> customPrimitiveSerializationDetectors,
                                          final List<CustomPrimitiveDeserializationDetector> customPrimitiveDeserializationDetectors) {
        validateNotNull(fieldDetectors, "fieldDetectors");
        validateNotNull(serializedObjectDeserializationDetectors, "serializedObjectDeserializationDetectors");
        validateNotNull(customPrimitiveSerializationDetectors, "customPrimitiveSerializationDetectors");
        validateNotNull(customPrimitiveDeserializationDetectors, "customPrimitiveDeserializationDetectors");
        return new SimpleDetector(
                fieldDetectors,
                serializedObjectDeserializationDetectors,
                customPrimitiveSerializationDetectors,
                customPrimitiveDeserializationDetectors
        );
    }

    public DetectionResult<DisambiguationResult> detect(final ResolvedType type,
                                                        final ScanInformationBuilder scanInformationBuilder,
                                                        final RequiredCapabilities capabilities,
                                                        final Disambiguators disambiguators) {
        if (!isSupported(type)) {
            return failure(format("type '%s' is not supported because it contains wildcard generics (\"?\")", type.description()));
        }

        scanInformationBuilder.resetScan();

        final List<TypeSerializer> customPrimitiveSerializers;
        final List<SerializationFieldOptions> serializationFieldOptionsList;
        if (capabilities.hasSerialization()) {
            customPrimitiveSerializers = detectCustomPrimitiveSerializers(type);
            customPrimitiveSerializers.forEach(scanInformationBuilder::addSerializer);
            serializationFieldOptionsList = detectSerializationFieldOptionsList(type);
            // TODO add to scan information
        } else {
            customPrimitiveSerializers = null;
            serializationFieldOptionsList = null;
        }

        final List<SerializedObjectDeserializer> serializedObjectDeserializers;
        final List<TypeDeserializer> customPrimitiveDeserializers;
        if (capabilities.hasDeserialization()) {
            serializedObjectDeserializers = detectSerializedObjectDeserializers(type);
            serializedObjectDeserializers.forEach(scanInformationBuilder::addDeserializer);
            customPrimitiveDeserializers = detectCustomPrimitiveDeserializers(type);
            customPrimitiveDeserializers.forEach(scanInformationBuilder::addDeserializer);
        } else {
            serializedObjectDeserializers = null;
            customPrimitiveDeserializers = null;
        }

        final Disambiguator disambiguator = disambiguators.disambiguatorFor(type);
        final SerializedObjectOptions serializedObjectOptions = serializedObjectOptions(serializationFieldOptionsList, serializedObjectDeserializers);
        final SerializersAndDeserializers customPrimitiveOptions = serializersAndDeserializers(customPrimitiveSerializers, customPrimitiveDeserializers);
        return disambiguator.disambiguate(type, serializedObjectOptions, customPrimitiveOptions, scanInformationBuilder);
    }

    private static boolean isSupported(final ResolvedType resolvedType) {
        if (resolvedType.isWildcard()) {
            return false;
        }
        return resolvedType.typeParameters().stream()
                .allMatch(SimpleDetector::isSupported);
    }

    private List<TypeSerializer> detectCustomPrimitiveSerializers(final ResolvedType type) {
        return this.customPrimitiveSerializationDetectors.stream()
                .map(detector -> detector.detect(type))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<SerializationFieldOptions> detectSerializationFieldOptionsList(final ResolvedType type) {
        final SerializationFieldOptions serializationFieldOptions = serializationFieldOptions();
        this.fieldDetectors.stream()
                .map(fieldDetector -> fieldDetector.detect(type))
                .flatMap(Collection::stream)
                .forEach(serializationFieldOptions::add);
        if (serializationFieldOptions.isEmpty()) {
            return emptyList();
        } else {
            return singletonList(serializationFieldOptions);
        }
    }

    private List<SerializedObjectDeserializer> detectSerializedObjectDeserializers(final ResolvedType type) {
        return this.serializedObjectDeserializationDetectors.stream()
                .map(detector -> detector.detect(type))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<TypeDeserializer> detectCustomPrimitiveDeserializers(final ResolvedType type) {
        return this.customPrimitiveDeserializationDetectors.stream()
                .map(detector -> detector.detect(type))
                .flatMap(Collection::stream)
                .collect(toList());
    }
}
