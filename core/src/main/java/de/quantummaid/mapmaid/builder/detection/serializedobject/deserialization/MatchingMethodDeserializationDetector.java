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

package de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization;

import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static de.quantummaid.mapmaid.builder.detection.priority.Prioritized.prioritized;
import static de.quantummaid.mapmaid.builder.detection.priority.Priority.PRIVILEGED_FACTORY;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.Common.detectDeserializerMethods;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer.methodDeserializer;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MatchingMethodDeserializationDetector implements SerializedObjectDeserializationDetector {
    private final Pattern deserializationMethodNamePattern;

    public static SerializedObjectDeserializationDetector matchingMethodBased(final String pattern) {
        final Pattern deserializationMethodNamePattern = Pattern.compile(pattern);
        return new MatchingMethodDeserializationDetector(deserializationMethodNamePattern);
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final ResolvedType type) {
        return detectDeserializerMethods(type).stream()
                .map(method -> methodDeserializer((ClassType) type, method))
                .map(deserializer -> prioritized(deserializer, PRIVILEGED_FACTORY))
                .collect(toList());
    }

    private Optional<ResolvedMethod> chooseDeserializer(final List<ResolvedMethod> deserializerCandidates,
                                                        final SerializationFields serializedFields,
                                                        final ClassType type) {
        ResolvedMethod deserializerMethod = null;
        if (deserializerCandidates.size() > 1) {
            final Optional<ResolvedMethod> byNamePattern = this.detectByNamePattern(deserializerCandidates, serializedFields);
            if (byNamePattern.isPresent()) {
                deserializerMethod = byNamePattern.get();
            } else {
                final String typeSimpleNameLowerCased = type.assignableType().getSimpleName().toLowerCase();
                final Optional<ResolvedMethod> byTypeName = deserializerCandidates.stream()
                        .filter(method -> method.method().getName().toLowerCase().contains(typeSimpleNameLowerCased))
                        .findFirst();
                if (byTypeName.isPresent()) {
                    deserializerMethod = byTypeName.get();
                }
            }
        }

        return ofNullable(deserializerMethod);
    }

    private Optional<ResolvedMethod> detectByNamePattern(final List<ResolvedMethod> deserializerCandidates,
                                                         final SerializationFields serializedFields) {
        final List<ResolvedMethod> withMatchingName = deserializerCandidates.stream()
                .filter(method -> this.deserializationMethodNamePattern.matcher(method.method().getName()).matches())
                .collect(toList());
        if (withMatchingName.size() == 1) {
            return Optional.of(deserializerCandidates.get(0));
        } else if (withMatchingName.size() > 1) {
            return deserializerCandidates.stream()
                    .filter(method -> Common.isMethodCompatibleWithFields(method.parameters(), serializedFields.typesList()))
                    .findFirst();
        }
        return empty();
    }
}
