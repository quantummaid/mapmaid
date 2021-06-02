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

package de.quantummaid.mapmaid.builder.resolving.framework.states;

import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.Report;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;

public abstract class StatefulDefinition<T> {
    public final Context<T> context;

    protected StatefulDefinition(final Context<T> context) {
        this.context = context;
    }

    public abstract StatefulDefinition<T> changeRequirements(RequirementsReducer reducer);

    public TypeIdentifier type() {
        return context.type();
    }

    public StatefulDefinition<T> detect(final Detector<T> detector, final RequirementsDescriber requirementsDescriber) {
        return this;
    }

    public StatefulDefinition<T> resolve(final Resolver<T> resolver) {
        return this;
    }

    public Report<T> getDefinition(final RequirementsDescriber requirementsDescriber) {
        throw new UnsupportedOperationException(this.getClass() + " " + this.context.toString());
    }
}
