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

package de.quantummaid.mapmaid.builder.resolving.undetected;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.StatefulDeserializer;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.undetectable.UndetectableDeserializer;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.builder.resolving.resolving.ResolvingDeserializer.resolvingDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.undetectable.UndetectableDeserializer.undetectableDeserializer;

@ToString
@EqualsAndHashCode
public final class UndetectedDeserializer extends StatefulDeserializer {

    private UndetectedDeserializer(final Context context) {
        super(context);
    }

    public static UndetectedDeserializer undetectedDeserializer(final Context context) {
        return new UndetectedDeserializer(context);
    }

    @Override
    public StatefulDefinition detect(final NewSimpleDetector detector,
                                     final BuildContextLog log,
                                     final Disambiguators disambiguators) {
        final ScanInformationBuilder scanInformationBuilder = this.context.scanInformationBuilder();
        final DetectionResult<DisambiguationResult> result = detector.detect(
                this.context.type(), log, scanInformationBuilder, deserializationOnly(), disambiguators);
        if (result.isFailure()) {
            return undetectableDeserializer(this.context);
        }
        this.context.setDeserializer(result.result().deserializer());
        return resolvingDeserializer(this.context);
    }
}
