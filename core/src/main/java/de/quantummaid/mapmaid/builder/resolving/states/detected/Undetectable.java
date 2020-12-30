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

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.Lingo;
import de.quantummaid.mapmaid.debug.RequiredAction;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.Report.failure;
import static de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult.collectionResult;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.ToBeDetected.toBeDetected;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Undetectable extends StatefulDefinition {
    private final String reason;

    private Undetectable(final Context context,
                         final String reason) {
        super(context);
        this.reason = reason;
    }

    public static StatefulDefinition undetectable(final Context context,
                                                  final String reason) {
        return new Undetectable(context, reason);
    }

    @Override
    public StatefulDefinition changeRequirements(final RequirementsReducer reducer) {
        final RequiredAction requiredAction = context.scanInformationBuilder().changeRequirements(reducer);
        return requiredAction.map(
                () -> this,
                () -> toBeDetected(context),
                () -> unreasoned(context)
        );
    }

    @Override
    public Optional<Report> getDefinition() {
        final ScanInformationBuilder scanInformationBuilder = context.scanInformationBuilder();
        final CollectionResult collectionResult = collectionResult(null, scanInformationBuilder);
        final DetectionRequirements detectionRequirements = context.scanInformationBuilder().detectionRequirements();
        final String mode = Lingo.mode(detectionRequirements.serialization, detectionRequirements.deserialization);
        return Optional.of(failure(collectionResult, format("unable to detect %s:%n%s", mode, reason)));
    }
}
