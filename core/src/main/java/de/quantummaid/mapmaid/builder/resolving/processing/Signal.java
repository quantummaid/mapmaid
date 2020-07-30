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

package de.quantummaid.mapmaid.builder.resolving.processing;

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface Signal {

    static Signal detect(final SimpleDetector detector,
                         final Disambiguators disambiguators,
                         final List<TypeIdentifier> injectedTypes) {
        return definition -> definition.detect(detector, disambiguators, injectedTypes);
    }

    static Signal addSerialization(final TypeIdentifier type, final Reason reason) {
        return to(type, definition -> definition.changeRequirements(current -> current.addSerialization(reason)));
    }

    static Signal removeSerialization(final Reason reason) {
        return definition -> definition.changeRequirements(current -> current.removeSerialization(reason));
    }

    static Signal addDeserialization(final TypeIdentifier type, final Reason reason) {
        return to(type, definition -> definition.changeRequirements(current -> current.addDeserialization(reason)));
    }

    static Signal removeDeserialization(final Reason reason) {
        return definition -> definition.changeRequirements(current -> current.removeDeserialization(reason));
    }

    static Signal resolve() {
        return StatefulDefinition::resolve;
    }

    static Signal to(final TypeIdentifier target, final Signal signal) {
        return new Signal() {

            @Override
            public Optional<TypeIdentifier> target() {
                return Optional.of(target);
            }

            @Override
            public StatefulDefinition handleState(final StatefulDefinition definition) {
                return signal.handleState(definition);
            }
        };
    }

    default Optional<TypeIdentifier> target() {
        return Optional.empty();
    }

    StatefulDefinition handleState(StatefulDefinition definition);
}
