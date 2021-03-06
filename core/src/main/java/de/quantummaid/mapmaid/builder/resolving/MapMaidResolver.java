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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.supertypes.SupertypeSerializers;
import de.quantummaid.reflectmaid.typescanner.Reason;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.requirements.RequirementName;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.AddReasonSignal;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;
import de.quantummaid.reflectmaid.typescanner.states.Resolver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.Requirements.*;
import static de.quantummaid.reflectmaid.typescanner.Reason.becauseOf;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidResolver implements Resolver<MapMaidTypeScannerResult> {
    private final SupertypeSerializers supertypeSerializers;

    public static MapMaidResolver mapMaidResolver(final SupertypeSerializers supertypeSerializers) {
        return new MapMaidResolver(supertypeSerializers);
    }

    @Override
    public List<Signal<MapMaidTypeScannerResult>> resolve(final MapMaidTypeScannerResult result,
                                                          final TypeIdentifier type,
                                                          final Scope scope,
                                                          final DetectionRequirements detectionRequirements) {
        final List<Signal<MapMaidTypeScannerResult>> signals = new ArrayList<>();
        final Reason reason = becauseOf(type, scope);
        if (detectionRequirements.requires(SERIALIZATION)) {
            final TypeSerializer serializer = result.disambiguationResult().serializer();
            addSignalsOfSerializer(serializer, scope, reason, signals);
            supertypeSerializers.detectSuperTypeSerializersFor(type).stream()
                    .map(supertypeSerializers::superTypeSerializer)
                    .forEach(superTypeSerializer -> addSignalsOfSerializer(superTypeSerializer, scope, reason, signals));
        }
        if (detectionRequirements.requires(DESERIALIZATION)) {
            final TypeDeserializer deserializer = result.disambiguationResult().deserializer();
            final List<TypeIdentifier> requiredTypes = deserializer.requiredTypes();
            addSignals(requiredTypes, DESERIALIZATION, scope, reason, signals, deserializer.forcesDependenciesToBeObjects());
        }
        return signals;
    }

    private static void addSignalsOfSerializer(final TypeSerializer serializer,
                                               final Scope scope,
                                               final Reason reason,
                                               final List<Signal<MapMaidTypeScannerResult>> signals) {
        final List<TypeIdentifier> requiredTypes = serializer.requiredTypes();
        addSignals(requiredTypes, SERIALIZATION, scope, reason, signals, serializer.forcesDependenciesToBeObjects());
    }

    private static void addSignals(final List<TypeIdentifier> requiredTypes,
                                   final RequirementName requirementName,
                                   final Scope scope,
                                   final Reason reason,
                                   final List<Signal<MapMaidTypeScannerResult>> signals,
                                   final boolean forcesDependenciesToBeObjects) {
        requiredTypes.stream()
                .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, requirementName, reason))
                .forEach(signals::add);
        if (forcesDependenciesToBeObjects) {
            requiredTypes.stream()
                    .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, OBJECT_ENFORCING, reason))
                    .forEach(signals::add);
        }
    }
}
