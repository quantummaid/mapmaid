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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker;

import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.MappingFunction;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.IrrefutableHints.irrefutableHints;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.TieBreakingReason.notATieBreakingReason;
import static de.quantummaid.reflectmaid.typescanner.states.DetectionResult.failure;
import static de.quantummaid.reflectmaid.typescanner.states.DetectionResult.followUpFailure;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1192")
public final class TieBreaker {
    private final IrrefutableHints<TypeSerializer> customPrimitiveSerializationHints;
    private final IrrefutableHints<TypeSerializer> serializedObjectSerializationHints;

    private final IrrefutableHints<TypeDeserializer> customPrimitiveDeserializationHints;
    private final IrrefutableHints<TypeDeserializer> serializedObjectDeserializationHints;

    public static TieBreaker tieBreaker(
            final List<IrrefutableHint<TypeSerializer>> customPrimitiveSerializationHints,
            final List<IrrefutableHint<TypeSerializer>> serializedObjectSerializationHints,
            final List<IrrefutableHint<TypeDeserializer>> customPrimitiveDeserializationHints,
            final List<IrrefutableHint<TypeDeserializer>> serializedObjectDeserializationHints) {
        return new TieBreaker(
                irrefutableHints(customPrimitiveSerializationHints),
                irrefutableHints(serializedObjectSerializationHints),
                irrefutableHints(customPrimitiveDeserializationHints),
                irrefutableHints(serializedObjectDeserializationHints)
        );
    }

    public DetectionResult<TypeSerializer> breakTieForSerializationOnly(
            final DetectionResult<TypeSerializer> customPrimitive,
            final DetectionResult<TypeSerializer> serializedObject,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements) {
        if (customPrimitive.isFailure() && serializedObject.isFailure()) {
            return followUpFailure(customPrimitive, serializedObject);
        }
        final TieBreakingReason customPrimitiveBreaking;
        if (!customPrimitive.isFailure()) {
            customPrimitiveBreaking = customPrimitiveSerializationHints.isTieBreaking(
                    customPrimitive.result(),
                    detectionRequirements
            );
        } else {
            customPrimitiveBreaking = notATieBreakingReason();
        }
        final TieBreakingReason serializedObjectBreaking;
        if (!serializedObject.isFailure()) {
            serializedObjectBreaking = this.serializedObjectSerializationHints.isTieBreaking(serializedObject.result(),
                    detectionRequirements);
        } else {
            serializedObjectBreaking = notATieBreakingReason();
        }
        if (customPrimitiveBreaking.isTieBreaking() && serializedObjectBreaking.isTieBreaking()) {
            final String explanation = format("Unable to choose between serialized object and custom primitive%n" +
                            "\tSerialized Object serializer: %s%n" +
                            "\tPrioritized because: %s%n" +
                            "\tCustom Primitive serializer: %s%n" +
                            "\tPrioritized because: %s%n",
                    serializedObject.result().description(), serializedObjectBreaking.getReason(),
                    customPrimitive.result().description(), customPrimitiveBreaking.getReason()
            );
            return failure(explanation);
        }
        return breakTie(
                customPrimitiveBreaking,
                serializedObjectBreaking,
                scanInformationBuilder,
                customPrimitive,
                serializedObject
        );
    }

    public DetectionResult<TypeDeserializer> breakTieForDeserializationOnly(
            final DetectionResult<TypeDeserializer> customPrimitive,
            final DetectionResult<TypeDeserializer> serializedObject,
            final ScanInformationBuilder scanInformationBuilder,
            final DetectionRequirements detectionRequirements) {
        if (customPrimitive.isFailure() && serializedObject.isFailure()) {
            return followUpFailure(customPrimitive, serializedObject);
        }
        final TieBreakingReason customPrimitiveBreaking;
        if (!customPrimitive.isFailure()) {
            customPrimitiveBreaking = this.customPrimitiveDeserializationHints.isTieBreaking(
                    customPrimitive.result(),
                    detectionRequirements
            );
        } else {
            customPrimitiveBreaking = notATieBreakingReason();
        }
        final TieBreakingReason serializedObjectBreaking;
        if (!serializedObject.isFailure()) {
            serializedObjectBreaking = this.serializedObjectDeserializationHints.isTieBreaking(
                    serializedObject.result(),
                    detectionRequirements
            );
        } else {
            serializedObjectBreaking = notATieBreakingReason();
        }
        if (customPrimitiveBreaking.isTieBreaking() && serializedObjectBreaking.isTieBreaking()) {
            final String explanation = format("" +
                            "Unable to choose between serialized object and custom primitive%n" +
                            "\tSerialized Object deserializer: %s%n" +
                            "\tPrioritized because: %s%n" +
                            "\tCustom Primitive deserializer: %s%n" +
                            "\tPrioritized because: %s%n",
                    serializedObject.result().description(),
                    serializedObjectBreaking.getReason(),
                    customPrimitive.result().description(),
                    customPrimitiveBreaking.getReason()
            );
            return failure(explanation);
        }
        return breakTie(customPrimitiveBreaking, serializedObjectBreaking,
                scanInformationBuilder, customPrimitive, serializedObject);
    }

    private static <T extends MappingFunction> DetectionResult<T> breakTie(final TieBreakingReason customPrimitiveBreaking,
                                                                           final TieBreakingReason serializedObjectBreaking,
                                                                           final ScanInformationBuilder scanInformationBuilder,
                                                                           final DetectionResult<T> customPrimitive,
                                                                           final DetectionResult<T> serializedObject) {
        if (customPrimitiveBreaking.isTieBreaking()) {
            serializedObject.ifSuccess(deserializer -> {
                final String reason = format(
                        "custom primitive has been preferred because: %s",
                        customPrimitiveBreaking.getReason());
                scanInformationBuilder.ignore(deserializer, reason);
            });
            return customPrimitive;
        }

        if (serializedObjectBreaking.isTieBreaking()) {
            customPrimitive.ifSuccess(deserializer -> {
                final String reason = format(
                        "serialized object has been preferred because: %s",
                        serializedObjectBreaking.getReason());
                scanInformationBuilder.ignore(deserializer, reason);
            });
            return serializedObject;
        }

        if (customPrimitive.isSuccess()) {
            if (serializedObject.isSuccess() && serializedObject.result().numberOfParameters() > 1) {
                customPrimitive.ifSuccess(deserializer -> scanInformationBuilder.ignore(deserializer,
                        "serialized object has been preferred because it has multiple parameters"));
                return serializedObject;
            } else {
                serializedObject.ifSuccess(deserializer ->
                        scanInformationBuilder.ignore(deserializer, "custom primitive has been preferred"));
                return customPrimitive;
            }
        }

        return serializedObject;
    }
}
