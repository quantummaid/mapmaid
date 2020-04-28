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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguationContext;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.Collection.smallMap;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.EquivalenceClass.equivalenceClass;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.EquivalenceSignature.allOfDeserializer;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SymmetryBuilder {
    private static final String SEPARATOR = "\n-------------\n";

    private final Map<EquivalenceSignature, EquivalenceClass> equivalenceClasses;

    public static SymmetryBuilder symmetryBuilder() {
        return new SymmetryBuilder(smallMap());
    }

    public void addDeserializer(final TypeDeserializer deserializer, final DisambiguationContext context) {
        if (!(deserializer instanceof SerializedObjectDeserializer)) {
            throw new UnsupportedOperationException("This should never happen. " +
                    "Only serialized object deserializers can be checked for symmetry, but got: " + deserializer);
        }
        final SerializedObjectDeserializer serializedObjectDeserializer = (SerializedObjectDeserializer) deserializer;
        final List<EquivalenceSignature> equivalenceSignatures = allOfDeserializer(serializedObjectDeserializer, context);
        equivalenceSignatures.forEach(equivalenceSignature -> {
            if (!this.equivalenceClasses.containsKey(equivalenceSignature)) {
                final int size = equivalenceSignature.size();
                this.equivalenceClasses.put(equivalenceSignature, equivalenceClass(size));
            }
            this.equivalenceClasses.get(equivalenceSignature).addDeserializer(deserializer);
        });
    }

    public void addSerializer(final SerializationFieldOptions serializer) {
        this.equivalenceClasses.forEach((signature, equivalenceClass) -> signature.match(serializer)
                .ifPresent(specializedSerializer ->
                        this.equivalenceClasses.get(signature).setSerializationFields(specializedSerializer)));
    }

    public DetectionResult<EquivalenceClass> determineGreatestCommonFields() {
        final List<EquivalenceSignature> sorted = this.equivalenceClasses.keySet().stream()
                .sorted()
                .collect(toList());
        final List<EquivalenceClass> supportedClasses = sorted.stream()
                .map(this.equivalenceClasses::get)
                .filter(EquivalenceClass::fullySupported)
                .collect(toList());
        if (supportedClasses.isEmpty()) {
            return failure("no symmetric result");
        }

        final int maxSize = supportedClasses.stream()
                .mapToInt(EquivalenceClass::size)
                .max()
                .orElseThrow(() -> new UnsupportedOperationException("This should never happen"));

        final List<EquivalenceClass> maxClasses = supportedClasses.stream()
                .filter(equivalenceClass -> equivalenceClass.size() == maxSize)
                .collect(toList());

        if (maxClasses.size() != 1) {
            final StringJoiner joiner = new StringJoiner(SEPARATOR, SEPARATOR, SEPARATOR);
            maxClasses.stream()
                    .map(EquivalenceClass::describe)
                    .forEach(joiner::add);
            final String message = format("ambiguous options as serialized object:%n%s", joiner.toString());
            return failure(message);
        }

        final EquivalenceClass winner = maxClasses.get(0);
        return success(winner);
    }
}
