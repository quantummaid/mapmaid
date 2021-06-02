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
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Detector;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequirementsDescriber;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequiredAction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Resolving.resolving;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Undetectable.undetectable;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class ToBeDetected<T> extends StatefulDefinition<T> {

    private ToBeDetected(final Context<T> context) {
        super(context);
    }

    public static <T> ToBeDetected<T> toBeDetected(final Context<T> context) {
        if (context.detectionRequirements().isUnreasoned()) {
            throw mapMaidException(format("type %s has entered state %s but has no reasons to be detected" +
                    " - this should never happen", context.type().description(), ToBeDetected.class.getSimpleName()));
        }
        return new ToBeDetected<>(context);
    }

    @Override
    public StatefulDefinition<T> changeRequirements(final RequirementsReducer reducer) {
        final RequiredAction requiredAction = context.changeRequirements(reducer);
        return requiredAction.map(
                () -> this,
                () -> this,
                () -> unreasoned(context)
        );
    }

    @Override
    public StatefulDefinition<T> detect(final Detector<T> detector, final RequirementsDescriber requirementsDescriber) {
        final DetectionRequirements requirements = context.detectionRequirements();
        final DetectionResult<T> result = context
                .manuallyConfiguredResult()
                .map(DetectionResult::success)
                .orElseGet(() -> detector.detect(type(), requirements));
        context.setDetectionResult(result);
        if (result.isFailure()) {
            return undetectable(context);
        } else {
            return resolving(context);
        }
    }
}
