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
import de.quantummaid.reflectmaid.typescanner.Reason;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.AddReasonSignal;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;
import de.quantummaid.reflectmaid.typescanner.states.Resolver;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.Requirements.*;
import static de.quantummaid.reflectmaid.typescanner.Reason.becauseOf;

public final class MapMaidResolver implements Resolver<MapMaidTypeScannerResult> {

    public static MapMaidResolver mapMaidResolver() {
        return new MapMaidResolver();
    }

    @Override
    public List<Signal<MapMaidTypeScannerResult>> resolve(final MapMaidTypeScannerResult result,
                                                          final TypeIdentifier type,
                                                          final Scope scope,
                                                          final DetectionRequirements detectionRequirements) {
        final List<Signal<MapMaidTypeScannerResult>> signals = new ArrayList<>();
        final Reason reason = becauseOf(type);
        if (detectionRequirements.requires(SERIALIZATION)) {
            final TypeSerializer serializer = result.disambiguationResult().serializer();
            final List<TypeIdentifier> requiredTypes = serializer.requiredTypes();
            requiredTypes.stream()
                    .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, SERIALIZATION, reason))
                    .forEach(signals::add);
            if (serializer.forcesDependenciesToBeObjects()) {
                requiredTypes.stream()
                        .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, OBJECT_ENFORCING, reason))
                        .forEach(signals::add);
            }
        }
        if (detectionRequirements.requires(DESERIALIZATION)) {
            final TypeDeserializer deserializer = result.disambiguationResult().deserializer();
            final List<TypeIdentifier> requiredTypes = deserializer.requiredTypes();
            requiredTypes.stream()
                    .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, DESERIALIZATION, reason))
                    .forEach(signals::add);
            if (deserializer.forcesDependenciesToBeObjects()) {
                requiredTypes.stream()
                        .map(requiredType -> AddReasonSignal.<MapMaidTypeScannerResult>addReasonSignal(requiredType, scope, OBJECT_ENFORCING, reason))
                        .forEach(signals::add);
            }
        }
        return signals;
    }
}
