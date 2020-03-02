/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.states.undetected;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulSerializer;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.builder.resolving.states.resolving.ResolvingSerializer.resolvingSerializer;
import static de.quantummaid.mapmaid.builder.resolving.states.undetectable.UndetectableSerializer.undetectableSerializer;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class UndetectedSerializer extends StatefulSerializer {

    private UndetectedSerializer(final Context context) {
        super(context);
    }

    public static UndetectedSerializer undetectedSerializer(final Context context) {
        return new UndetectedSerializer(context);
    }

    @Override
    public StatefulDefinition detect(final SimpleDetector detector,
                                     final Disambiguators disambiguators) {
        final ScanInformationBuilder scanInformationBuilder = this.context.scanInformationBuilder();
        final DetectionResult<DisambiguationResult> result = detector.detect(
                this.context.type(), scanInformationBuilder, serialization(), disambiguators);
        if (result.isFailure()) {
            return undetectableSerializer(this.context, format("no serializer detected:%n%s", result.reasonForFailure()));
        }
        this.context.setSerializer(result.result().serializer());
        return resolvingSerializer(this.context);
    }
}
