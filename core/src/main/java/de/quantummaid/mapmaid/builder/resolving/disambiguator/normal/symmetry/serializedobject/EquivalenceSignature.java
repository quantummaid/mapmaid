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
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguationContext;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject.Combinations.allCombinations;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EquivalenceSignature implements Comparable<EquivalenceSignature> {
    private final Map<String, TypeIdentifier> fields;

    public static List<EquivalenceSignature> allOfDeserializer(final SerializedObjectDeserializer deserializer,
                                                               final DisambiguationContext context) {
        final Map<String, TypeIdentifier> fields = deserializer.fields().fields();
        final List<Map<String, TypeIdentifier>> combinations = combinations(fields, context);
        return combinations.stream()
                .map(EquivalenceSignature::new)
                .collect(toList());
    }

    private static List<Map<String, TypeIdentifier>> combinations(final Map<String, TypeIdentifier> fields,
                                                                  final DisambiguationContext context) {
        final List<String> requiredKeys = smallList();
        final List<String> optionalKeys = smallList();
        fields.forEach((key, typeIdentifier) -> {
            if (context.isInjected(typeIdentifier)) {
                optionalKeys.add(key);
            } else {
                requiredKeys.add(key);
            }
        });

        final List<List<String>> keyCombinations = allCombinations(requiredKeys, optionalKeys);
        return keyCombinations.stream()
                .map(keys -> subMap(keys, fields))
                .collect(toList());
    }

    public Optional<SerializationFieldInstantiation> match(final SerializationFieldOptions serializer) {
        final DetectionResult<SerializationFieldInstantiation> instance = serializer.instantiate(this.fields);
        if (instance.isFailure()) {
            return empty();
        }
        return Optional.of(instance.result());
    }

    public int size() {
        return this.fields.size();
    }

    @Override
    public int compareTo(final EquivalenceSignature other) {
        final Integer mySize = this.fields.size();
        final Integer otherSize = other.fields.size();
        return mySize.compareTo(otherSize) * -1;
    }

    private static Map<String, TypeIdentifier> subMap(final List<String> keys,
                                                      final Map<String, TypeIdentifier> map) {
        final Map<String, TypeIdentifier> subMap = new HashMap<>(keys.size());
        keys.forEach(key -> {
            final TypeIdentifier typeIdentifier = map.get(key);
            subMap.put(key, typeIdentifier);
        });
        return subMap;
    }
}
