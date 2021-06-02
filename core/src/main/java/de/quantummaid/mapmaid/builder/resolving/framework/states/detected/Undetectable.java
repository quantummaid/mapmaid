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
import de.quantummaid.mapmaid.builder.resolving.framework.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequirementsDescriber;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequiredAction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.framework.Report.failure;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.CollectionResult.collectionResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.ToBeDetected.toBeDetected;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Undetectable<T> extends StatefulDefinition<T> {

    private Undetectable(final Context<T> context) {
        super(context);
    }

    public static <T> StatefulDefinition<T> undetectable(final Context<T> context) {
        return new Undetectable<>(context);
    }

    @Override
    public StatefulDefinition<T> changeRequirements(final RequirementsReducer reducer) {
        final RequiredAction requiredAction = context.changeRequirements(reducer);
        return requiredAction.map(
                () -> this,
                () -> toBeDetected(context),
                () -> unreasoned(context)
        );
    }

    @Override
    public Report<T> getDefinition(final RequirementsDescriber requirementsDescriber) {
        final DetectionRequirements detectionRequirements = context.detectionRequirements();
        final DetectionResult<T> detectionResult = context.detectionResult();
        final CollectionResult<T> collectionResult = collectionResult(detectionResult.result(), detectionRequirements);
        final String mode = requirementsDescriber.describe(detectionRequirements);
        final String reason = format("no %s detected:%n%s",
                requirementsDescriber.describe(context.detectionRequirements()),
                context.detectionResult().reasonForFailure()
        );
        return failure(collectionResult, format("unable to detect %s:%n%s", mode, reason));
    }
}
