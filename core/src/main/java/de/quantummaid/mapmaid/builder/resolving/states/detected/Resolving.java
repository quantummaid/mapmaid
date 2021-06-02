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
import de.quantummaid.mapmaid.builder.resolving.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.Resolver;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.RequiredAction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.states.detected.Resolved.resolved;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.ToBeDetected.toBeDetected;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Resolving<T> extends StatefulDefinition<T> {

    private Resolving(final Context<T> context) {
        super(context);
    }

    public static <T> Resolving<T> resolving(final Context<T> context) {
        return new Resolving<>(context);
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
    public StatefulDefinition<T> resolve(final Resolver<T> resolver) {
        final T detectionResult = context.detectionResult().get();
        final List<Signal<T>> signals = resolver.resolve(detectionResult, type(), context.detectionRequirements());
        signals.forEach(context::dispatch);
        return resolved(context);
    }
}
