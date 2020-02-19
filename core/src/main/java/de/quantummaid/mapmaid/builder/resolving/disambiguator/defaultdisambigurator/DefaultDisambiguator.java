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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.customprimitive.CustomPrimitiveSymmetryBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SerializedObjectOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.combine;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.Picker.pickDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.Picker.pickSerializer;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.customprimitive.CustomPrimitiveSymmetryBuilder.customPrimitiveSymmetryBuilder;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder.symmetryBuilder;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultDisambiguator implements Disambiguator {

    public static DefaultDisambiguator defaultDisambiguator() {
        return new DefaultDisambiguator();
    }

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                              final SerializedObjectOptions serializedObjectOptions,
                                                              final SerializersAndDeserializers customPrimitiveSerializersAndDeserializers,
                                                              final ScanInformationBuilder scanInformationBuilder) {
        if (type.assignableType().getPackageName().startsWith("java.util")) {
            return failure("Native java classes cannot be detected"); // TODO
        }

        if (customPrimitiveSerializersAndDeserializers.serializationOnly()) {
            final DetectionResult<TypeSerializer> serializer = pickSerializer(customPrimitiveSerializersAndDeserializers);

            if (!serializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                        format("less priority than %s", serializer.result().description()));
            }
            return serializer
                    .or(() -> pickSerializer(serializedObjectOptions.toSerializersAndDeserializers()))
                    .map(DisambiguationResult::serializationOnlyResult);
        }

        if (customPrimitiveSerializersAndDeserializers.deserializationOnly()) {
            final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(customPrimitiveSerializersAndDeserializers);
            if (!deserializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                        format("less priority than %s", deserializer.result().description()));
            }
            return deserializer
                    .or(() -> pickDeserializer(serializedObjectOptions.toSerializersAndDeserializers()))
                    .map(DisambiguationResult::deserializationOnlyResult);
        }

        final CustomPrimitiveSymmetryBuilder customPrimitiveSymmetryBuilder = customPrimitiveSymmetryBuilder();
        customPrimitiveSerializersAndDeserializers.serializers()
                .forEach(serializer -> customPrimitiveSymmetryBuilder.addSerializer((CustomPrimitiveSerializer) serializer));
        customPrimitiveSerializersAndDeserializers.deserializers()
                .forEach(deserializer -> customPrimitiveSymmetryBuilder.addDeserializer((CustomPrimitiveDeserializer) deserializer));
        final Optional<SerializersAndDeserializers> customPrimitiveResult = customPrimitiveSymmetryBuilder.determineGreatestCommonFields();
        if (customPrimitiveResult.isPresent()) {
            final SerializersAndDeserializers serializersAndDeserializers = customPrimitiveResult.get();
            final DetectionResult<TypeSerializer> serializer = pickSerializer(serializersAndDeserializers);
            if (!serializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                        format("less priority than %s", serializer.result().description()));
            }
            final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(serializersAndDeserializers);
            if (!deserializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                        format("less priority than %s", deserializer.result().description()));
            }
            return combine(serializer, deserializer, DisambiguationResult::duplexResult);
        }


        final SymmetryBuilder symmetryBuilder = symmetryBuilder();
        final List<SerializedObjectDeserializer> deserializers = serializedObjectOptions.deserializers();
        deserializers.forEach(symmetryBuilder::addDeserializer);
        serializedObjectOptions.serializationFieldOptions().forEach(symmetryBuilder::addSerializer);

        final DetectionResult<SerializersAndDeserializers> symmetric = symmetryBuilder.determineGreatestCommonFields();
        if (symmetric.isFailure()) {
            scanInformationBuilder.ignoreAllSerializers("no symmetric match");
            scanInformationBuilder.ignoreAllDeserializers("no symmetric match");
            return failure(format("Failed to detect %s: %s", type.description(), symmetric.reasonForFailure()));
        }

        final SerializersAndDeserializers symmetricResult = symmetric.result();

        final DetectionResult<TypeSerializer> serializer = pickSerializer(symmetricResult);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(), format("most symmetry in %s", serializer.result().description()));
        }

        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(symmetricResult);
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(), format("most symmetry in %s", deserializer.result().description()));
        }

        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }
}
