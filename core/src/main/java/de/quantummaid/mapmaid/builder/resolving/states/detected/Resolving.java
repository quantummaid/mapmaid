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
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.processing.Signal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.states.detected.ToBeDetected.toBeDetected;
import static de.quantummaid.mapmaid.debug.Reason.becauseOf;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Resolved.resolvedDuplex;

@ToString
@EqualsAndHashCode(callSuper = true)
public final class Resolving extends StatefulDefinition {

    private Resolving(final Context context) {
        super(context);
    }

    public static Resolving resolvingDuplex(final Context context) {
        return new Resolving(context);
    }

    @Override
    public StatefulDefinition changeRequirements(final RequirementsReducer reducer) {
        final boolean changed = this.context.scanInformationBuilder().changeRequirements(reducer);
        if (changed) {
            return toBeDetected(context);
        } else {
            return this;
        }
    }

    @Override
    public StatefulDefinition resolve() {
        final DetectionRequirements requirements = this.context.scanInformationBuilder().detectionRequirements();

        if (requirements.serialization) {
            this.context.serializer().orElseThrow().requiredTypes().forEach(type ->
                    this.context.dispatch(Signal.addSerialization(type, becauseOf(this.context.type()))));
        }
        if (requirements.deserialization) {
            this.context.deserializer().orElseThrow().requiredTypes().forEach(type ->
                    this.context.dispatch(Signal.addDeserialization(type, becauseOf(this.context.type()))));
        }
        return resolvedDuplex(this.context);
    }
}
