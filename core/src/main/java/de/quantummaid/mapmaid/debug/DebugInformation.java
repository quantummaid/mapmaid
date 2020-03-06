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
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.debug.scaninformation.NeverScannedScanInformation.neverScanned;
import static de.quantummaid.mapmaid.shared.identifier.RealTypeIdentifier.realTypeIdentifier;
import static de.quantummaid.mapmaid.shared.types.ResolvedType.resolvedType;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DebugInformation {
    private final Map<TypeIdentifier, ScanInformation> scanInformations;

    public static DebugInformation debugInformation(final Map<TypeIdentifier, ScanInformation> scanInformations) {
        return new DebugInformation(scanInformations);
    }

    public ScanInformation scanInformationFor(final Class<?> type) {
        return scanInformationFor(resolvedType(type));
    }

    public ScanInformation scanInformationFor(final ResolvedType type) {
        return scanInformationFor(realTypeIdentifier(type));
    }

    public ScanInformation scanInformationFor(final TypeIdentifier type) {
        return optionalScanInformationFor(type)
                .orElseGet(() -> neverScanned(type));
    }

    public Optional<ScanInformation> optionalScanInformationFor(final Class<?> type) {
        return optionalScanInformationFor(resolvedType(type));
    }

    public Optional<ScanInformation> optionalScanInformationFor(final ResolvedType type) {
        return optionalScanInformationFor(realTypeIdentifier(type));
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

    public String dumpAll() {
        return allScanInformations().stream()
                .map(ScanInformation::render)
                .collect(Collectors.joining("\n\n\n"));
    }
}
