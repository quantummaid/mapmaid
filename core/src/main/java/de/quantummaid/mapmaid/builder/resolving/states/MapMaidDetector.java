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

package de.quantummaid.mapmaid.builder.resolving.states;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidDetector implements Detector {
    private final SimpleDetector detector;
    private final Disambiguators disambiguators;
    private final List<TypeIdentifier> injectedTypes;

    public static Detector mapMaidDetector(final SimpleDetector detector,
                                           final Disambiguators disambiguators,
                                           final List<TypeIdentifier> injectedTypes) {
        return new MapMaidDetector(detector, disambiguators, injectedTypes);
    }

    @Override
    public DetectionResult<DisambiguationResult> detect(
            final DetectionRequirements detectionRequirements,
            final Context context
    ) {
        final ScanInformationBuilder scanInformationBuilder = context.scanInformationBuilder();
        return context
                .fixedResult()
                .orElseGet(() -> detector.detect(
                        context.type(),
                        scanInformationBuilder,
                        disambiguators,
                        injectedTypes
                ));
    }
}
