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

package de.quantummaid.mapmaid.builder.resolving.requirements;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.disambiguationResult;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionRequirements {
    public final boolean serialization;
    public final boolean deserialization;
    public final boolean hasToBeObject;
    public final boolean hasToBeInlinedPrimitive;
    private final TypeSerializer manuallyConfiguredSerializer;
    private final TypeDeserializer manuallyConfiguredDeserializer;

    static DetectionRequirements detectionRequirements(final boolean serialization,
                                                       final boolean deserialization,
                                                       final boolean hasToBeObject,
                                                       final boolean hasToBeInlinedPrimitive,
                                                       final TypeSerializer manuallyConfiguredSerializer,
                                                       final TypeDeserializer manuallyConfiguredDeserializer) {
        return new DetectionRequirements(serialization,
                deserialization,
                hasToBeObject,
                hasToBeInlinedPrimitive,
                manuallyConfiguredSerializer,
                manuallyConfiguredDeserializer);
    }

    public boolean isUnreasoned() {
        return !serialization && !deserialization;
    }

    public boolean isSerializationOnly() {
        return serialization && !deserialization;
    }

    public boolean isDeserializationOnly() {
        return !serialization && deserialization;
    }

    public boolean isDuplex() {
        return serialization && deserialization;
    }

    public String describe() {
        if (isSerializationOnly()) {
            return "serialization";
        }
        if (isDeserializationOnly()) {
            return "deserialization";
        }
        if (isDuplex()) {
            return "duplex";
        }
        throw new UnsupportedOperationException();
    }

    public Optional<DetectionResult<DisambiguationResult>> fixedResult() {
        if (manuallyConfiguredSerializer != null || manuallyConfiguredDeserializer != null) {
            return Optional.of(success(disambiguationResult(manuallyConfiguredSerializer, manuallyConfiguredDeserializer)));
        } else {
            return Optional.empty();
        }
    }
}
