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

package de.quantummaid.mapmaid.builder.resolving.framework.states.detected;

import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.Report;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequirementsDescriber;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.debug.RequiredAction;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.framework.Report.success;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.CollectionResult.collectionResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.RemoveReasonSignal.removeReasonSignal;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.ToBeDetected.toBeDetected;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.debug.Reason.becauseOf;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Resolved<T> extends StatefulDefinition<T> {

    private Resolved(final Context<T> context) {
        super(context);
    }

    public static <T> StatefulDefinition<T> resolved(final Context<T> context) {
        return new Resolved<>(context);
    }

    @Override
    public StatefulDefinition<T> changeRequirements(final RequirementsReducer reducer) {
        final RequiredAction requiredAction = context.changeRequirements(reducer);
        return requiredAction.map(
                () -> this,
                () -> {
                    removeTransitiveReasons();
                    return toBeDetected(context);
                },
                () -> {
                    removeTransitiveReasons();
                    return unreasoned(context);
                }
        );
    }

    private void removeTransitiveReasons() {
        final Reason transitiveReason = becauseOf(context.type());
        context.dispatch(removeReasonSignal(transitiveReason));
    }

    @Override
    public Report<T> getDefinition(final RequirementsDescriber requirementsDescriber) {
        final T detectionResult = context.detectionResult().orElseThrow();
        final ScanInformationBuilder scanInformationBuilder = context.scanInformationBuilder();
        final DetectionRequirements detectionRequirements = context.detectionRequirements();
        return success(collectionResult(detectionResult, scanInformationBuilder, detectionRequirements));
    }
}
