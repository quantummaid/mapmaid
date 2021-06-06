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

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.OnCollectionError;
import de.quantummaid.reflectmaid.typescanner.Report;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.log.StateLog;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidOnCollectionError implements OnCollectionError<MapMaidTypeScannerResult> {
    private final ReflectMaid reflectMaid;

    public static MapMaidOnCollectionError mapMaidOnCollectionError(final ReflectMaid reflectMaid) {
        return new MapMaidOnCollectionError(reflectMaid);
    }

    @Override
    public void onCollectionError(final @NotNull Map<TypeIdentifier, ? extends Map<Scope, CollectionResult<MapMaidTypeScannerResult>>> results,
                                  final @NotNull StateLog<MapMaidTypeScannerResult> log,
                                  final @NotNull Map<TypeIdentifier, ? extends Map<Scope, Report<MapMaidTypeScannerResult>>> failures) {
        final DebugInformation debugInformation = debugInformation(results, log, reflectMaid);
        final StringJoiner errorMessageJoiner = new StringJoiner("\n\n");
        final List<ScanInformation> scanInformations = new ArrayList<>(failures.size());
        failures.forEach((typeIdentifier, reportByScope) -> {
            final Report<MapMaidTypeScannerResult> report = reportByScope.get(rootScope());
            errorMessageJoiner.add(typeIdentifier.description() + ": " + report.errorMessage());
            final ScanInformation scanInformation = debugInformation.scanInformationFor(typeIdentifier);
            scanInformations.add(scanInformation);
        });
        final String errorMessage = format("The following classes could not be detected properly:%n%n%s",
                errorMessageJoiner);
        throw mapMaidException(errorMessage, scanInformations);
    }
}
