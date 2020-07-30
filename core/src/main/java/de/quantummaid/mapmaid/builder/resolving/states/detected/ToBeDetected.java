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

package de.quantummaid.mapmaid.builder.resolving.states.detected;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.states.detected.Resolving.resolvingDuplex;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Undetectable.undetectableDuplex;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class ToBeDetected extends StatefulDefinition {

    private ToBeDetected(final Context context) {
        super(context);
    }

    public static ToBeDetected toBeDetected(final Context context) {
        return new ToBeDetected(context);
    }

    @Override
    public StatefulDefinition changeRequirements(final RequirementsReducer reducer) {
        this.context.scanInformationBuilder().changeRequirements(reducer);
        return this;
    }

    @Override
    public StatefulDefinition detect(final SimpleDetector detector,
                                     final Disambiguators disambiguators,
                                     final List<TypeIdentifier> injectedTypes) {
        final ScanInformationBuilder scanInformationBuilder = this.context.scanInformationBuilder();
        final DetectionRequirements requirements = scanInformationBuilder.detectionRequirements();
        final RequiredCapabilities requiredCapabilities = requirements.toCapabilities();
        final DetectionResult<DisambiguationResult> result = detector.detect(
                this.context.type(),
                scanInformationBuilder,
                requiredCapabilities,
                disambiguators,
                injectedTypes
        );
        if (result.isFailure()) {
            return undetectableDuplex(this.context, format("no duplex detected:%n%s", result.reasonForFailure()));
        }
        if (requirements.serialization) {
            this.context.setSerializer(result.result().serializer());
        }
        if (requirements.deserialization) {
            this.context.setDeserializer(result.result().deserializer());
        }
        return resolvingDuplex(this.context);
    }
}
