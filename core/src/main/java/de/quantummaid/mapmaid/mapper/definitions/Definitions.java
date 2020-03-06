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

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.mapper.definitions.DefinitionNotFoundException.definitionNotFound;
import static java.lang.String.format;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Definitions {
    private final Map<TypeIdentifier, Definition> definitions;
    private final DebugInformation debugInformation;

    public static Definitions definitions(final Map<TypeIdentifier, Definition> definitions,
                                          final DebugInformation debugInformation) {
        final Definitions definitionsObject = new Definitions(definitions, debugInformation);
        definitionsObject.validateNoUnsupportedOutgoingReferences(debugInformation);
        return definitionsObject;
    }

    public Definition getDefinitionForType(final TypeIdentifier targetType) {
        return getOptionalDefinitionForType(targetType)
                .orElseThrow(() -> definitionNotFound(targetType, this.debugInformation.dumpAll()));
    }

    public Optional<Definition> getOptionalDefinitionForType(final TypeIdentifier targetType) {
        if (!this.definitions.containsKey(targetType)) {
            return Optional.empty();
        }
        return of(this.definitions.get(targetType));
    }

    private void validateNoUnsupportedOutgoingReferences(final DebugInformation debugInformation) {
        this.definitions.values().forEach(definition -> {
            if (definition.deserializer().isPresent()) {
                validateDeserialization(
                        definition.type(),
                        definition.type(),
                        new ArrayList<>(this.definitions.size()),
                        debugInformation);
            }
            if (definition.serializer().isPresent()) {
                validateSerialization(definition.type(), definition.type(), new ArrayList<>(this.definitions.size()));
            }
        });
    }

    private void validateDeserialization(final TypeIdentifier candidate,
                                         final TypeIdentifier reason,
                                         final List<TypeIdentifier> alreadyVisited,
                                         final DebugInformation debugInformation) {
        if (alreadyVisited.contains(candidate)) {
            return;
        }
        alreadyVisited.add(candidate);
        final Definition definition = getOptionalDefinitionForType(candidate).orElseThrow(() -> {
            final ScanInformation candidateInformation = debugInformation.scanInformationFor(candidate);
            final ScanInformation reasonInformation = debugInformation.scanInformationFor(reason);
            return MapMaidException.mapMaidException(
                    format("Type '%s' is not registered but needs to be in order to support deserialization of '%s'",
                            candidate.description(), reason.description()),
                    candidateInformation, reasonInformation);
        });

        if (definition.deserializer().isEmpty()) {
            throw new UnsupportedOperationException(
                    format("'%s' is not deserializable but needs to be in order to support deserialization of '%s'",
                            candidate.description(), reason.description()));
        } else {
            definition.deserializer().get().requiredTypes().forEach(type -> validateDeserialization(type, reason, alreadyVisited, debugInformation));
        }
    }

    private void validateSerialization(final TypeIdentifier candidate, final TypeIdentifier reason, final List<TypeIdentifier> alreadyVisited) {
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
                    format("'%s' is not serializable but needs to be in order to support serialization of '%s'",
                            candidate.description(), reason.description()));
        } else {
            definition.serializer().get().requiredTypes().forEach(type -> validateSerialization(type, reason, alreadyVisited));
        }
    }
}
