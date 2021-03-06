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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal;

import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filters;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.customprimitive.CustomPrimitiveSymmetryBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.EquivalenceClass;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SymmetryBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.TieBreaker;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.supertypes.SupertypeSerializers;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.Requirements.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguationContext.disambiguationContext;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.Picker.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.customprimitive.CustomPrimitiveSymmetryBuilder.customPrimitiveSymmetryBuilder;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SerializedObjectOptions.serializedObjectOptions;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.SymmetryBuilder.symmetryBuilder;
import static de.quantummaid.reflectmaid.typescanner.states.DetectionResult.combine;
import static de.quantummaid.reflectmaid.typescanner.states.DetectionResult.failure;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NormalDisambiguator implements Disambiguator {
    private final Preferences<TypeDeserializer, DisambiguationContext> customPrimitiveDeserializerPreferences;
    private final Preferences<TypeSerializer, DisambiguationContext> customPrimitiveSerializerPreferences;
    private final Preferences<TypeDeserializer, DisambiguationContext> serializedObjectDeserializerPreferences;

    private final Filters<SerializationField, DisambiguationContext> serializationFieldFilters;
    private final Preferences<SerializationField, DisambiguationContext> postSymmetrySerializationFieldPreferences;

    private final TieBreaker tieBreaker;

    public static NormalDisambiguator normalDisambiguator(
            final Preferences<TypeDeserializer, DisambiguationContext> customPrimitiveDeserializerPreferences,
            final Preferences<TypeSerializer, DisambiguationContext> customPrimitiveSerializerPreferences,
            final Preferences<TypeDeserializer, DisambiguationContext> serializedObjectPreferences,
            final Filters<SerializationField, DisambiguationContext> serializationFieldFilters,
            final Preferences<SerializationField, DisambiguationContext> postSymmetrySerializationFieldPreferences,
            final TieBreaker tieBreaker) {
        return new NormalDisambiguator(
                customPrimitiveDeserializerPreferences,
                customPrimitiveSerializerPreferences,
                serializedObjectPreferences,
                serializationFieldFilters,
                postSymmetrySerializationFieldPreferences,
                tieBreaker
        );
    }

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(
            final ResolvedType type,
            final SerializedObjectOptions serializedObjectOptions,
            final SerializersAndDeserializers serializersAndDeserializers,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final List<TypeIdentifier> injectedTypes,
            final SupertypeSerializers supertypeSerializers) {
        if (type.assignableType().getPackageName().startsWith("java.") && !supertypeSerializers.hasRegisteredSupertype(type)) {
            return failure("Native java classes cannot be detected");
        }
        final DisambiguationContext context = disambiguationContext(injectedTypes, supertypeSerializers);
        final SerializedObjectOptions filteredSerializedObjectOptions = filterSerializedObjectOptions(
                type, serializedObjectOptions, scanInformationBuilder, detectionRequirements, context);
        final SerializersAndDeserializers preferredCustomPrimitiveSerializersAndDeserializers;
        if (!detectionRequirements.requires(OBJECT_ENFORCING) && !supertypeSerializers.hasRegisteredSupertype(type)) {
            preferredCustomPrimitiveSerializersAndDeserializers =
                    filterCustomPrimitiveOptions(
                            type, serializersAndDeserializers, scanInformationBuilder, detectionRequirements, context);
        } else {
            preferredCustomPrimitiveSerializersAndDeserializers = SerializersAndDeserializers.empty();
        }
        if (detectionRequirements.requires(SERIALIZATION) && !detectionRequirements.requires(DESERIALIZATION)) {
            return serializationOnly(
                    type,
                    preferredCustomPrimitiveSerializersAndDeserializers,
                    filteredSerializedObjectOptions,
                    scanInformationBuilder,
                    detectionRequirements,
                    context
            );
        }
        if (detectionRequirements.requires(DESERIALIZATION) && !detectionRequirements.requires(SERIALIZATION)) {
            return deserializationOnly(
                    type,
                    preferredCustomPrimitiveSerializersAndDeserializers,
                    filteredSerializedObjectOptions, scanInformationBuilder, detectionRequirements);
        }
        return duplex(type, preferredCustomPrimitiveSerializersAndDeserializers,
                filteredSerializedObjectOptions, scanInformationBuilder, detectionRequirements, context
        );
    }

    private DetectionResult<DisambiguationResult> serializationOnly(
            final ResolvedType type,
            final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
            final SerializedObjectOptions serializedObjectOptions,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final DisambiguationContext context) {
        final List<TypeSerializer> customPrimitiveSerializers = customPrimitiveSerializersAndDeserializers.serializers(type);
        final DetectionResult<TypeSerializer> customPrimitiveSerializer =
                oneOrNone(customPrimitiveSerializers, TypeSerializer::description)
                        .orElseGet(() -> failure("No serializers to choose from"));

        final DetectionResult<TypeSerializer> serializedObjectSerializer = serializedObjectOptions.determineSerializer(
                type, postSymmetrySerializationFieldPreferences, scanInformationBuilder, detectionRequirements, context);

        return this.tieBreaker.breakTieForSerializationOnly(
                customPrimitiveSerializer,
                serializedObjectSerializer,
                scanInformationBuilder,
                detectionRequirements
        )
                .map(DisambiguationResult::serializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> deserializationOnly(
            final ResolvedType type,
            final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
            final SerializedObjectOptions serializedObjectOptions,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements) {
        final List<TypeDeserializer> deserializers = customPrimitiveSerializersAndDeserializers.deserializers(type);
        final DetectionResult<TypeDeserializer> customPrimitiveDeserializer = pickDeserializer(deserializers);
        final DetectionResult<TypeDeserializer> serializedObjectDeserializer =
                pickDeserializer(serializedObjectOptions.deserializers());
        return this.tieBreaker.breakTieForDeserializationOnly(
                customPrimitiveDeserializer,
                serializedObjectDeserializer,
                scanInformationBuilder,
                detectionRequirements
        )
                .map(DisambiguationResult::deserializationOnlyResult);
    }

    private DetectionResult<DisambiguationResult> duplex(
            final ResolvedType type,
            final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
            final SerializedObjectOptions serializedObjectOptions,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final DisambiguationContext context) {
        final CustomPrimitiveSymmetryBuilder customPrimitiveSymmetryBuilder = customPrimitiveSymmetryBuilder();
        customPrimitiveSerializersAndDeserializers.serializers(type).forEach(serializer ->
                customPrimitiveSymmetryBuilder.addSerializer((CustomPrimitiveSerializer) serializer));
        customPrimitiveSerializersAndDeserializers.deserializers(type).forEach(deserializer ->
                customPrimitiveSymmetryBuilder.addDeserializer((CustomPrimitiveDeserializer) deserializer));
        final Optional<SerializersAndDeserializers> customPrimitiveResult =
                customPrimitiveSymmetryBuilder.determineGreatestCommonFields();

        if (customPrimitiveResult.isPresent()) {
            return symmetricCustomPrimitive(type, customPrimitiveResult.get(), scanInformationBuilder);
        }
        return symmetricSerializedObject(type, serializedObjectOptions, scanInformationBuilder, detectionRequirements, context);
    }

    private DetectionResult<DisambiguationResult> symmetricCustomPrimitive(
            final ResolvedType type,
            final SerializersAndDeserializers serializersAndDeserializers,
            final ScanInformationBuilder scanInformationBuilder) {
        final DetectionResult<TypeSerializer> serializer = pickSerializer(type, serializersAndDeserializers);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                    format("less priority than %s", serializer.result().description()));
        }
        final List<TypeDeserializer> deserializers = serializersAndDeserializers.deserializers(type);
        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(deserializers);
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                    format("less priority than %s", deserializer.result().description()));
        }
        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private DetectionResult<DisambiguationResult> symmetricSerializedObject(
            final ResolvedType type,
            final SerializedObjectOptions serializedObjectOptions,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final DisambiguationContext context) {
        final SymmetryBuilder symmetryBuilder = symmetryBuilder();
        final List<TypeDeserializer> deserializers = serializedObjectOptions.deserializers();
        deserializers.forEach(deserializer -> symmetryBuilder.addDeserializer(deserializer, context));
        final SerializationFieldOptions serializationFieldOptions = serializedObjectOptions.serializationFieldOptions();
        symmetryBuilder.addSerializer(serializationFieldOptions);

        final DetectionResult<EquivalenceClass> symmetric = symmetryBuilder.determineGreatestCommonFields();
        if (symmetric.isFailure()) {
            return failure(format("Failed to detect %s:%n%s", type.description(), symmetric.reasonForFailure()));
        }

        final EquivalenceClass symmetricResult = symmetric.result();

        final SerializationFieldInstantiation serializationFields = symmetricResult.serializationFields();
        final DetectionResult<TypeSerializer> serializer = serializationFields.instantiate(
                type,
                this.postSymmetrySerializationFieldPreferences,
                scanInformationBuilder,
                detectionRequirements,
                context
        );
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(), "insufficient symmetry");
        }

        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(symmetricResult.deserializers());
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(), "insufficient symmetry");
        }

        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private SerializersAndDeserializers filterCustomPrimitiveOptions(
            final ResolvedType type,
            final SerializersAndDeserializers serializersAndDeserializers,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final DisambiguationContext context) {
        final List<TypeSerializer> preferredCustomPrimitiveSerializers;
        if (serializersAndDeserializers.hasSerializers()) {
            final List<TypeSerializer> customPrimitiveSerializers = serializersAndDeserializers.serializers(type);
            preferredCustomPrimitiveSerializers = this.customPrimitiveSerializerPreferences.preferred(
                    customPrimitiveSerializers,
                    context,
                    detectionRequirements,
                    type,
                    scanInformationBuilder::ignoreSerializer);
        } else {
            preferredCustomPrimitiveSerializers = null;
        }

        final List<TypeDeserializer> preferredCustomPrimitiveDeserializers;
        if (serializersAndDeserializers.hasDeserializers()) {
            final List<TypeDeserializer> customPrimitiveDeserializers = serializersAndDeserializers.deserializers(type);
            preferredCustomPrimitiveDeserializers = this.customPrimitiveDeserializerPreferences.preferred(
                    customPrimitiveDeserializers,
                    context,
                    detectionRequirements,
                    type,
                    scanInformationBuilder::ignoreDeserializer);
        } else {
            preferredCustomPrimitiveDeserializers = null;
        }

        return serializersAndDeserializers(preferredCustomPrimitiveSerializers, preferredCustomPrimitiveDeserializers);
    }

    private SerializedObjectOptions filterSerializedObjectOptions(
            final ResolvedType containingType,
            final SerializedObjectOptions serializedObjectOptions,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements,
            final DisambiguationContext context) {
        final SerializationFieldOptions serializationFieldOptions = serializedObjectOptions.serializationFieldOptions();
        final SerializationFieldOptions filteredSerializationFields;

        if (serializationFieldOptions != null) {
            filteredSerializationFields = serializationFieldOptions.filter(
                    field -> this.serializationFieldFilters.isAllowed(
                            field,
                            context,
                            containingType,
                            scanInformationBuilder::ignoreSerializationField)
            );
        } else {
            filteredSerializationFields = null;
        }

        final List<TypeDeserializer> filteredSerializedObjectDeserializers;
        if (serializedObjectOptions.deserializers() != null) {
            final List<TypeDeserializer> serializedObjectDeserializers = serializedObjectOptions.deserializers();
            filteredSerializedObjectDeserializers = this.serializedObjectDeserializerPreferences.preferred(
                    serializedObjectDeserializers,
                    context,
                    detectionRequirements,
                    containingType,
                    scanInformationBuilder::ignoreDeserializer
            );
        } else {
            filteredSerializedObjectDeserializers = null;
        }
        return serializedObjectOptions(filteredSerializationFields, filteredSerializedObjectDeserializers);
    }
}
