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

package de.quantummaid.mapmaid.builder.detection;

import de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization.CustomPrimitiveDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.customprimitive.serialization.CustomPrimitiveSerializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.SerializedObjectDeserializationDetector;
import de.quantummaid.mapmaid.builder.detection.serializedobject.fields.FieldDetector;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.supertypes.SupertypeSerializers;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions.serializationFieldOptions;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.DESERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.SERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions.serializedObjectOptions;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.typescanner.states.DetectionResult.failure;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleDetector {
    private static final String HINT = "(you can still register it manually)";

    private final List<FieldDetector> fieldDetectors;
    private final List<SerializedObjectDeserializationDetector> serializedObjectDeserializationDetectors;
    private final List<CustomPrimitiveSerializationDetector> customPrimitiveSerializationDetectors;
    private final List<CustomPrimitiveDeserializationDetector> customPrimitiveDeserializationDetectors;

    public static SimpleDetector detector(
            final List<FieldDetector> fieldDetectors,
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

    public DetectionResult<DisambiguationResult> detect(final TypeIdentifier typeIdentifier,
                                                        final ScanInformationBuilder scanInformationBuilder,
                                                        final DetectionRequirements detectionRequirements,
                                                        final Disambiguators disambiguators,
                                                        final List<TypeIdentifier> injectedTypes,
                                                        final SupertypeSerializers supertypeSerializers) {
        if (typeIdentifier.isVirtual()) {
            return failure("can only detect real types");
        }
        final ResolvedType type = typeIdentifier.realType();
        final Optional<DetectionResult<DisambiguationResult>> isNotSupported = validateForSupportedFeatures(type);
        if (isNotSupported.isPresent()) {
            return isNotSupported.get();
        }
        scanInformationBuilder.resetScan();
        final List<TypeSerializer> customPrimitiveSerializers;
        final SerializationFieldOptions serializationFieldOptions;
        if (detectionRequirements.requires(SERIALIZATION)) {
            customPrimitiveSerializers = detectCustomPrimitiveSerializers(type);
            customPrimitiveSerializers.forEach(scanInformationBuilder::addSerializer);
            serializationFieldOptions = detectSerializationFieldOptionsList(type);
            serializationFieldOptions.allFields().forEach(scanInformationBuilder::addSerializationField);
        } else {
            customPrimitiveSerializers = null;
            serializationFieldOptions = null;
        }
        final List<TypeDeserializer> serializedObjectDeserializers;
        final List<TypeDeserializer> customPrimitiveDeserializers;
        if (detectionRequirements.requires(DESERIALIZATION)) {
            serializedObjectDeserializers = detectSerializedObjectDeserializers(type);
            serializedObjectDeserializers.forEach(scanInformationBuilder::addDeserializer);
            customPrimitiveDeserializers = detectCustomPrimitiveDeserializers(type);
            customPrimitiveDeserializers.forEach(scanInformationBuilder::addDeserializer);
        } else {
            serializedObjectDeserializers = null;
            customPrimitiveDeserializers = null;
        }
        final SerializedObjectOptions serializedObjectOptions =
                serializedObjectOptions(serializationFieldOptions, serializedObjectDeserializers);
        final SerializersAndDeserializers customPrimitiveOptions =
                serializersAndDeserializers(customPrimitiveSerializers, customPrimitiveDeserializers);
        return disambiguate(type, disambiguators, serializedObjectOptions,
                customPrimitiveOptions, scanInformationBuilder, detectionRequirements, injectedTypes, supertypeSerializers);
    }

    private DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                               final Disambiguators disambiguators,
                                                               final SerializedObjectOptions serializedObjectOptions,
                                                               final SerializersAndDeserializers customPrimitiveOptions,
                                                               final ScanInformationBuilder scanInformationBuilder,
                                                               final DetectionRequirements detectionRequirements,
                                                               final List<TypeIdentifier> injectedTypes,
                                                               final SupertypeSerializers supertypeSerializers) {
        final Disambiguator disambiguator = disambiguators.disambiguatorFor(type);
        return disambiguator.disambiguate(
                type,
                serializedObjectOptions,
                customPrimitiveOptions,
                scanInformationBuilder,
                detectionRequirements,
                injectedTypes,
                supertypeSerializers
        );
    }

    private static Optional<DetectionResult<DisambiguationResult>> validateForSupportedFeatures(
            final ResolvedType type) {
        if (!isSupported(type)) {
            return Optional.of(failure(
                    format("type '%s' is not supported because it contains wildcard generics (\"?\")",
                            type.description())));
        }
        if (type.isAnnotation()) {
            return Optional.of(failure(
                    format("type '%s' cannot be detected because it is an annotation %s",
                            type.description(), HINT)));
        }
        if (type.isAnonymousClass()) {
            return Optional.of(failure(
                    format("type '%s' cannot be detected because it is an anonymous class %s",
                            type.description(), HINT)));
        }
        if (type.isLocalClass()) {
            return Optional.of(failure(
                    format("type '%s' cannot be detected because it is a local class %s",
                            type.description(), HINT)));
        }
        if (type.isInnerClass() && !type.isStatic()) {
            return Optional.of(failure(
                    format("type '%s' cannot be detected because it is a non-static inner class %s",
                            type.description(), HINT)));
        }
        if (!type.isPublic()) {
            return Optional.of(failure(
                    format("type '%s' cannot be detected because it is not public %s",
                            type.description(), HINT)));
        }
        return Optional.empty();
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

    private SerializationFieldOptions detectSerializationFieldOptionsList(final ResolvedType type) {
        final SerializationFieldOptions serializationFieldOptions = serializationFieldOptions();
        this.fieldDetectors.stream()
                .map(fieldDetector -> fieldDetector.detect(type))
                .flatMap(Collection::stream)
                .forEach(serializationFieldOptions::add);
        return serializationFieldOptions;
    }

    private List<TypeDeserializer> detectSerializedObjectDeserializers(final ResolvedType type) {
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
