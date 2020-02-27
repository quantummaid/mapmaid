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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.customprimitive.CustomPrimitiveSymmetryBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Filters;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Preferences;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.combine;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.Picker.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.customprimitive.CustomPrimitiveSymmetryBuilder.customPrimitiveSymmetryBuilder;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions.serializedObjectOptions;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder.symmetryBuilder;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultDisambiguator implements Disambiguator {
    private final Preferences<TypeDeserializer> customPrimitiveDeserializerPreferences;
    private final Preferences<TypeSerializer> customPrimitiveSerializerPreferences;
    private final Preferences<TypeDeserializer> serializedObjectPreferences;
    private final Filters<SerializationField> serializationFieldFilters;

    public static DefaultDisambiguator defaultDisambiguator(final Preferences<TypeDeserializer> customPrimitiveDeserializerPreferences,
                                                            final Preferences<TypeSerializer> customPrimitiveSerializerPreferences,
                                                            final Preferences<TypeDeserializer> serializedObjectPreferences,
                                                            final Filters<SerializationField> serializationFieldFilters) {
        return new DefaultDisambiguator(
                customPrimitiveDeserializerPreferences,
                customPrimitiveSerializerPreferences,
                serializedObjectPreferences,
                serializationFieldFilters);
    }

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                              final SerializedObjectOptions serializedObjectOptions,
                                                              final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                              final ScanInformationBuilder scanInformationBuilder) {
        if (type.assignableType().getPackageName().startsWith("java.util")) {
            // TODO
            return failure("Native java classes cannot be detected");
        }

        final SerializationFieldOptions serializationFieldOptions = serializedObjectOptions.serializationFieldOptions();
        final SerializationFieldOptions filteredSerialzationFields;
        if (serializationFieldOptions != null) {
            filteredSerialzationFields = serializationFieldOptions.filter(
                    field -> this.serializationFieldFilters.isAllowed(field, scanInformationBuilder::ignoreSerializationField)
            );
        } else {
            filteredSerialzationFields = null;
        }
        final SerializedObjectOptions filteredSerializedObjectOptions = serializedObjectOptions(filteredSerialzationFields, serializedObjectOptions.deserializers());

        // TODO
        if (customPrimitiveSerializersAndDeserializers.serializationOnly()) {
            return serializationOnly(customPrimitiveSerializersAndDeserializers, filteredSerializedObjectOptions, scanInformationBuilder);
        }

        // TODO
        if (customPrimitiveSerializersAndDeserializers.deserializationOnly()) {
            return deserializationOnly(customPrimitiveSerializersAndDeserializers, filteredSerializedObjectOptions, scanInformationBuilder);
        }

        final CustomPrimitiveSymmetryBuilder customPrimitiveSymmetryBuilder = customPrimitiveSymmetryBuilder();
        customPrimitiveSerializersAndDeserializers.serializers()
                .forEach(serializer -> customPrimitiveSymmetryBuilder.addSerializer((CustomPrimitiveSerializer) serializer));
        customPrimitiveSerializersAndDeserializers.deserializers()
                .forEach(deserializer -> customPrimitiveSymmetryBuilder.addDeserializer((CustomPrimitiveDeserializer) deserializer));
        final Optional<SerializersAndDeserializers> customPrimitiveResult = customPrimitiveSymmetryBuilder.determineGreatestCommonFields();

        if (customPrimitiveResult.isPresent()) {
            return symmetricCustomPrimitive(customPrimitiveResult.get(), scanInformationBuilder);
        }
        return symmetricSerializedObject(type, filteredSerializedObjectOptions, scanInformationBuilder);
    }

    private DetectionResult<DisambiguationResult> serializationOnly(final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                                    final SerializedObjectOptions serializedObjectOptions,
                                                                    final ScanInformationBuilder scanInformationBuilder) {
        final List<TypeSerializer> customPrimitiveSerializers = customPrimitiveSerializersAndDeserializers.serializers();
        final List<TypeSerializer> preferredCustomPrimitives = this.customPrimitiveSerializerPreferences.preferred(
                customPrimitiveSerializers, scanInformationBuilder::ignoreSerializer);
        final DetectionResult<TypeSerializer> customPrimitiveSerializer = oneOrNone(preferredCustomPrimitives, TypeSerializer::description)
                .orElseGet(() -> failure("No serializers to choose from"));

        if (!customPrimitiveSerializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(customPrimitiveSerializer.result(),
                    format("less priority than %s", customPrimitiveSerializer.result().description()));
            return customPrimitiveSerializer.map(DisambiguationResult::serializationOnlyResult);
        }

        final SerializationFieldOptions serializationFieldOptions = serializedObjectOptions.serializationFieldOptions();
        if (serializationFieldOptions.isEmpty()) {
            return failure("No serialization fields");
        }

        final DetectionResult<TypeSerializer> serializedObjectSerializer = pickSerializer(
                serializedObjectOptions.toSerializersAndDeserializers(),
                this.customPrimitiveSerializerPreferences,
                scanInformationBuilder
        );
        return serializedObjectSerializer.map(DisambiguationResult::serializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> deserializationOnly(final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                                      final SerializedObjectOptions serializedObjectOptions,
                                                                      final ScanInformationBuilder scanInformationBuilder) {
        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(
                customPrimitiveSerializersAndDeserializers,
                this.customPrimitiveDeserializerPreferences,
                this.serializedObjectPreferences,
                scanInformationBuilder);
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                    format("less priority than %s", deserializer.result().description()));
        }
        return deserializer
                .or(() -> pickDeserializer(
                        serializedObjectOptions.toSerializersAndDeserializers(),
                        this.customPrimitiveDeserializerPreferences,
                        this.serializedObjectPreferences,
                        scanInformationBuilder)
                )
                .map(DisambiguationResult::deserializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> symmetricCustomPrimitive(final SerializersAndDeserializers serializersAndDeserializers,
                                                                           final ScanInformationBuilder scanInformationBuilder) {
        final DetectionResult<TypeSerializer> serializer = pickSerializer(
                serializersAndDeserializers,
                this.customPrimitiveSerializerPreferences,
                scanInformationBuilder);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                    format("less priority than %s", serializer.result().description()));
        }
        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(
                serializersAndDeserializers,
                this.customPrimitiveDeserializerPreferences,
                this.serializedObjectPreferences,
                scanInformationBuilder
        );
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                    format("less priority than %s", deserializer.result().description()));
        }
        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private DetectionResult<DisambiguationResult> symmetricSerializedObject(final ResolvedType type,
                                                                            final SerializedObjectOptions serializedObjectOptions,
                                                                            final ScanInformationBuilder scanInformationBuilder) {
        final SymmetryBuilder symmetryBuilder = symmetryBuilder();
        final List<SerializedObjectDeserializer> deserializers = serializedObjectOptions.deserializers();
        deserializers.forEach(symmetryBuilder::addDeserializer);
        symmetryBuilder.addSerializer(serializedObjectOptions.serializationFieldOptions());

        final DetectionResult<SerializersAndDeserializers> symmetric = symmetryBuilder.determineGreatestCommonFields();
        if (symmetric.isFailure()) {
            scanInformationBuilder.ignoreAllSerializers("no symmetric match");
            scanInformationBuilder.ignoreAllDeserializers("no symmetric match");
            return failure(format("Failed to detect %s: %s", type.description(), symmetric.reasonForFailure()));
        }

        final SerializersAndDeserializers symmetricResult = symmetric.result();

        final DetectionResult<TypeSerializer> serializer = pickSerializer(
                symmetricResult,
                this.customPrimitiveSerializerPreferences,
                scanInformationBuilder);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(), format("most symmetry in %s", serializer.result().description()));
        }

        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(
                symmetricResult,
                this.customPrimitiveDeserializerPreferences,
                this.serializedObjectPreferences,
                scanInformationBuilder);
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(), format("most symmetry in %s", deserializer.result().description()));
        }

        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }
}