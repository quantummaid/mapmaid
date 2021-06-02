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
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.RequiredAction;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.ToBeDetected.toBeDetected;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Unreasoned<T> extends StatefulDefinition<T> {

    private Unreasoned(final Context<T> context) {
        super(context);
    }

    public static <T> StatefulDefinition<T> unreasoned(final Context<T> context) {
        return new Unreasoned<>(context);
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
    public Report<T> getDefinition() {
        return Report.empty();
    }
}
