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
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Detector;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.debug.ScanInformationBuilder.scanInformationBuilder;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidDetector implements Detector<MapMaidTypeScannerResult> {
    private final SimpleDetector detector;
    private final Disambiguators disambiguators;
    private final List<TypeIdentifier> injectedTypes;

    public static MapMaidDetector mapMaidDetector(final SimpleDetector detector,
                                                  final Disambiguators disambiguators,
                                                  final List<TypeIdentifier> injectedTypes) {
        return new MapMaidDetector(detector, disambiguators, injectedTypes);
    }

    @Override
    public DetectionResult<MapMaidTypeScannerResult> detect(
            final TypeIdentifier type,
            final DetectionRequirements detectionRequirements
    ) {
        final ScanInformationBuilder scanInformationBuilder = scanInformationBuilder(type);
        final DetectionResult<DisambiguationResult> detectionResult = detector.detect(
                type,
                scanInformationBuilder,
                detectionRequirements,
                disambiguators,
                injectedTypes
        );
        return detectionResult.mapWithNull(result -> result(result, scanInformationBuilder));
    }
}
