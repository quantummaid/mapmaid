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

package de.quantummaid.mapmaid.debug;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.SubReasonProvider;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.log.StateLog;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.Map.Entry;

import static de.quantummaid.mapmaid.debug.scaninformation.NeverScannedScanInformation.neverScanned;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DebugInformation {
    private final ReflectMaid reflectMaid;
    private final Map<TypeIdentifier, ScanInformation> scanInformations;
    private final StateLog<MapMaidTypeScannerResult> stateLog;

    public static DebugInformation debugInformation(
            final Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<MapMaidTypeScannerResult>>> resultsByScope,
            final StateLog<MapMaidTypeScannerResult> stateLog,
            final ReflectMaid reflectMaid
    ) {
        final Map<TypeIdentifier, CollectionResult<MapMaidTypeScannerResult>> results = resultsByScope.entrySet().stream()
                .collect(toMap(Entry::getKey, entry -> {
                    final Map<Scope, CollectionResult<MapMaidTypeScannerResult>> byScope = entry.getValue();
                    return byScope.get(rootScope());
                }));
        final Map<TypeIdentifier, ScanInformation> scanInformations = new HashMap<>(results.size());
        final SubReasonProvider serializationSubReasonProvider =
                typeIdentifier -> scanInformations.get(typeIdentifier).reasonsForSerialization();
        final SubReasonProvider deserializationSubReasonProvider =
                typeIdentifier -> scanInformations.get(typeIdentifier).reasonsForDeserialization();
        results.forEach(
                (typeIdentifier, result) -> {
                    final ScanInformationBuilder scanInformationBuilder = result.getDefinition().scanInformationBuilder();
                    final DetectionRequirements detectionRequirements = result.getDetectionRequirements();
                    final ScanInformation scanInformation =
                            scanInformationBuilder.build(
                                    serializationSubReasonProvider,
                                    deserializationSubReasonProvider,
                                    detectionRequirements,
                                    result.getDefinition().disambiguationResult()
                            );
                    scanInformations.put(typeIdentifier, scanInformation);
                }
        );
        return new DebugInformation(reflectMaid, scanInformations, stateLog);
    }

    public ScanInformation scanInformationFor(final Class<?> type) {
        return scanInformationFor(genericType(type));
    }

    public ScanInformation scanInformationFor(final GenericType<?> type) {
        return scanInformationFor(reflectMaid.resolve(type));
    }

    public ScanInformation scanInformationFor(final ResolvedType type) {
        return scanInformationFor(typeIdentifierFor(type));
    }

    public ScanInformation scanInformationFor(final TypeIdentifier type) {
        return optionalScanInformationFor(type)
                .orElseGet(() -> neverScanned(type));
    }

    public Optional<ScanInformation> optionalScanInformationFor(final Class<?> type) {
        return optionalScanInformationFor(reflectMaid.resolve(type));
    }

    public Optional<ScanInformation> optionalScanInformationFor(final ResolvedType type) {
        return optionalScanInformationFor(typeIdentifierFor(type));
    }

    public Optional<ScanInformation> optionalScanInformationFor(final TypeIdentifier type) {
        if (!this.scanInformations.containsKey(type)) {
            return empty();
        }
        return of(this.scanInformations.get(type));
    }

    public List<ScanInformation> allScanInformations() {
        return new ArrayList<>(this.scanInformations.values());
    }

    public StateLog<MapMaidTypeScannerResult> stateLog() {
        return stateLog;
    }

    public String dumpAll() {
        return allScanInformations().stream()
                .map(ScanInformation::render)
                .collect(joining("\n\n\n"));
    }
}
