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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.disambigurator.preferences.Preferences;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SymmetryResult {
    private final List<SerializationFieldInstantiation> serializers;
    private final List<TypeDeserializer> deserializers;

    public static SymmetryResult symmetryResult(final List<SerializationFieldInstantiation> serializers,
                                                final List<TypeDeserializer> deserializers) {
        if (serializers.isEmpty()) {
            throw new IllegalArgumentException("serializers must not be empty");
        }
        if (deserializers.isEmpty()) {
            throw new IllegalArgumentException("deserializers must not be empty");
        }
        return new SymmetryResult(serializers, deserializers);
    }

    public List<TypeDeserializer> deserializers() {
        return this.deserializers;
    }

    public DetectionResult<TypeSerializer> determineSerializer(final Preferences<SerializationField> preferences,
                                                               final ScanInformationBuilder scanInformationBuilder) {
        if (this.serializers.size() != 1) {
            final String options = this.serializers.stream()
                    .map(SerializationFieldInstantiation::describe)
                    .collect(Collectors.joining("----\n", "----", "----"));
            return failure(format("cannot decide between '%s'", options));
        }

        final SerializationFieldInstantiation instantiation = this.serializers.get(0);
        return instantiation.instantiate(preferences, scanInformationBuilder);
    }
}
