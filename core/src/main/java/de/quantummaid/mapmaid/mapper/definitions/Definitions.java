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

package de.quantummaid.mapmaid.mapper.definitions;

import de.quantummaid.mapmaid.mapper.DefinitionScanLog;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Definitions {
    private final DefinitionScanLog definitionScanLog;
    private final Map<ResolvedType, Definition> definitions;

    public static Definitions definitions(final DefinitionScanLog definitionScanLog,
                                          final Map<ResolvedType, Definition> definitions) {
        final Definitions definitionsObject = new Definitions(definitionScanLog, definitions);
        definitionsObject.validateNoUnsupportedOutgoingReferences();
        return definitionsObject;
    }

    public Definition getDefinitionForType(final ResolvedType targetType) {
        return getOptionalDefinitionForType(targetType)
                .orElseThrow(() -> DefinitionNotFoundException.definitionNotFound(targetType, dump()));
    }

    public Optional<Definition> getOptionalDefinitionForType(final ResolvedType targetType) {
        if (!this.definitions.containsKey(targetType)) {
            return Optional.empty();
        }
        return of(this.definitions.get(targetType));
    }

    private void validateNoUnsupportedOutgoingReferences() {
        this.definitions.values().forEach(definition -> {
            if(definition.deserializer().isPresent()) {
                validateDeserialization(definition.type(), definition.type(), new LinkedList<>());
            }
            if(definition.serializer().isPresent()) {
                validateSerialization(definition.type(), definition.type(), new LinkedList<>());
            }
        });
    }

    private void validateDeserialization(final ResolvedType candidate, final ResolvedType reason, final List<ResolvedType> alreadyVisited) {
        if (alreadyVisited.contains(candidate)) {
            return;
        }
        alreadyVisited.add(candidate);
        final Definition definition = getOptionalDefinitionForType(candidate).orElseThrow(() ->
                new UnsupportedOperationException(
                        format("Type '%s' is not registered but needs to be in order to support deserialization of '%s'.%s",
                                candidate.description(), reason.description(), this.definitionScanLog.summaryFor(candidate))));

        if (definition.deserializer().isEmpty()) {
            throw new UnsupportedOperationException(
                    format("'%s' is not deserializable but needs to be in order to support deserialization of '%s'. %s",
                            candidate.description(), reason.description(), this.definitionScanLog.summaryFor(candidate)));
        } else {
            definition.deserializer().get().requiredTypes().forEach(type -> validateDeserialization(type, reason, alreadyVisited));
        }
    }

    private void validateSerialization(final ResolvedType candidate, final ResolvedType reason, final List<ResolvedType> alreadyVisited) {
        if (alreadyVisited.contains(candidate)) {
            return;
        }
        alreadyVisited.add(candidate);
        final Definition definition = getOptionalDefinitionForType(candidate).orElseThrow(() ->
                new UnsupportedOperationException(
                        format("Type '%s' is not registered but needs to be in order to support serialization of '%s'",
                                candidate.description(), reason.description())));

        if (definition.serializer().isEmpty()) {
            throw new UnsupportedOperationException(
                    format("'%s' is not serializable but needs to be in order to support serialization of '%s'. %s",
                            candidate.description(), reason.description(), this.definitionScanLog.summaryFor(candidate)));
        } else {
            definition.serializer().get().requiredTypes().forEach(type -> validateSerialization(type, reason, alreadyVisited));
        }
    }

    public int countCustomPrimitives() {
        return (int) this.definitions.values().stream()
                .filter(definition -> definition.classification().equals("Custom Primitive"))
                .count();
    }

    public int countSerializedObjects() {
        return (int) this.definitions.values().stream()
                .filter(definition -> definition.classification().equals("Serialized Object"))
                .count();
    }

    public String dump() {
        final StringBuilder stringBuilder = new StringBuilder(10);
        stringBuilder.append("------------------------------\n");
        stringBuilder.append("Serialized Objects:\n");
        this.definitions.values().stream()
                .filter(definition -> definition.classification().equals("Serialized Object"))
                .map(Definition::type)
                .map(ResolvedType::description)
                .sorted()
                .forEach(type -> stringBuilder.append(type).append("\n"));
        stringBuilder.append("------------------------------\n");
        stringBuilder.append("Custom Primitives:\n");
        this.definitions.values().stream()
                .filter(definition -> definition.classification().equals("Custom Primitive"))
                .map(Definition::type)
                .map(ResolvedType::description)
                .sorted()
                .forEach(type -> stringBuilder.append(type).append("\n"));
        stringBuilder.append("------------------------------\n");
        stringBuilder.append("Collections:\n");
        this.definitions.values().stream()
                .filter(definition -> definition.classification().equals("Collection"))
                .map(Definition::type)
                .map(ResolvedType::description)
                .sorted()
                .forEach(type -> stringBuilder.append(type).append("\n"));
        stringBuilder.append("------------------------------\n");
        return stringBuilder.toString();
    }
}
