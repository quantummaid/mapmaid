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

package de.quantummaid.mapmaid.builder.resolving.states.fixed;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.states.fixed.FixedResolvingDuplex.fixedResolvingDuplex;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class FixedUnreasoned extends StatefulDefinition {

    private FixedUnreasoned(final Context context) {
        super(context);
    }

    public static StatefulDefinition fixedUnreasoned(final Context context) {
        return new FixedUnreasoned(context);
    }

    @Override
    public StatefulDefinition changeRequirements(final RequirementsReducer reducer) {
        final boolean changed = this.context.scanInformationBuilder().changeRequirements(reducer);
        if (changed) {
            return fixedResolvingDuplex(context);
        } else {
            return this;
        }
    }
}
