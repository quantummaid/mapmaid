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

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.serialization.supertypes.SupertypeSerializers;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.states.DetectionResult;
import de.quantummaid.reflectmaid.typescanner.states.Detector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors.conventionalDetector;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.debug.ScanInformationBuilder.scanInformationBuilder;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidDetector implements Detector<MapMaidTypeScannerResult> {
    private final SimpleDetector detector;
    private final Disambiguators disambiguators;
    private final List<TypeIdentifier> injectedTypes;
    private final SupertypeSerializers supertypeSerializers;

    public static MapMaidDetector mapMaidDetector(final Disambiguators disambiguators,
                                                  final List<TypeIdentifier> injectedTypes,
                                                  final SupertypeSerializers supertypeSerializers) {
        final SimpleDetector detector = conventionalDetector();
        return new MapMaidDetector(detector, disambiguators, injectedTypes, supertypeSerializers);
    }

    @NotNull
    @Override
    public DetectionResult<MapMaidTypeScannerResult> detect(
            @NotNull final TypeIdentifier type,
            @NotNull final Scope scope,
            @NotNull final DetectionRequirements detectionRequirements
    ) {
        final ScanInformationBuilder scanInformationBuilder = scanInformationBuilder(type);
        final DetectionResult<DisambiguationResult> detectionResult = detector.detect(
                type,
                scanInformationBuilder,
                detectionRequirements,
                disambiguators,
                injectedTypes,
                supertypeSerializers
        );
        return detectionResult.mapWithNull(result -> result(result, scanInformationBuilder));
    }
}
