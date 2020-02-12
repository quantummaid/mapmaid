/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.signals;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.Optional;

public interface Signal {

    static Signal detect(final NewSimpleDetector detector, final BuildContextLog log, final Disambiguators disambiguators) {
        return definition -> definition.detect(detector, log, disambiguators);
    }

    static Signal addSerialization(final ResolvedType type, final Reason reason) {
        return to(type, definition -> definition.addSerialization(reason));
    }

    static Signal addDeserialization(final ResolvedType type, final Reason reason) {
        return to(type, definition -> definition.addDeserialization(reason));
    }

    static Signal resolve() {
        return StatefulDefinition::resolve;
    }

    static Signal to(final ResolvedType target, final Signal signal) {
        return new Signal() {

            @Override
            public Optional<ResolvedType> target() {
                return Optional.of(target);
            }

            @Override
            public StatefulDefinition handleState(final StatefulDefinition definition) {
                return signal.handleState(definition);
            }
        };
    }

    default Optional<ResolvedType> target() {
        return Optional.empty();
    }

    StatefulDefinition handleState(final StatefulDefinition definition);
}
