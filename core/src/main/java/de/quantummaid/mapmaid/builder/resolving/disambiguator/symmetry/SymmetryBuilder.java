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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.EquivalenceClass.equivalenceClass;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.EquivalenceSignature.ofDeserializer;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SymmetryBuilder {
    private final Map<EquivalenceSignature, EquivalenceClass> equivalenceClasses;

    public static SymmetryBuilder symmetryBuilder() {
        return new SymmetryBuilder(new HashMap<>(10));
    }

    public void addDeserializer(final SerializedObjectDeserializer deserializer) {
        final EquivalenceSignature equivalenceSignature = ofDeserializer(deserializer);
        if (!this.equivalenceClasses.containsKey(equivalenceSignature)) {
            this.equivalenceClasses.put(equivalenceSignature, equivalenceClass());
        }
        this.equivalenceClasses.get(equivalenceSignature).addDeserializer(deserializer);
    }

    public void addSerializer(final SerializationFieldOptions serializer) {
        for (final EquivalenceSignature signature : this.equivalenceClasses.keySet()) {
            signature.match(serializer).ifPresent(specializedSerializer ->
                    this.equivalenceClasses.get(signature).addSerializer(specializedSerializer));
        }
    }

    // TODO different classes with same size?
    public DetectionResult<SerializersAndDeserializers> determineGreatestCommonFields() {
        final List<EquivalenceSignature> sorted = this.equivalenceClasses.keySet().stream()
                .sorted()
                .collect(toList());
        return sorted.stream()
                .map(this.equivalenceClasses::get)
                .filter(EquivalenceClass::fullySupported)
                .findFirst() // TODO
                .map(equivalenceClass -> {
                    return serializersAndDeserializers(equivalenceClass.serializers(), equivalenceClass.deserializers());
                })
                .map(DetectionResult::success)
                .orElseGet(() -> failure("No symmetric result")); // TODO
    }
}
