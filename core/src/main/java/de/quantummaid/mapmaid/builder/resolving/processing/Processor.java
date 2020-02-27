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

package de.quantummaid.mapmaid.builder.resolving.processing;

import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.signals.Signal;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.processing.States.states;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.detect;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.resolve;
import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Processor {
    private States states;
    private final Queue<Signal> pendingSignals;

    public static Processor processor() {
        final Queue<Signal> pendingSignals = new LinkedList<>();
        final States states = states(new ArrayList<>(0));
        final Processor processor = new Processor(states, pendingSignals);
        return processor;
    }

    public void dispatch(final Signal signal) {
        this.pendingSignals.add(signal);
    }

    public void addState(final StatefulDefinition statefulDefinition) {
        validateNotNull(statefulDefinition, "statefulDefinition");
        this.states = this.states.addState(statefulDefinition);
    }

    public Map<ResolvedType, CollectionResult> collect(final SimpleDetector detector,
                                                       final Disambiguators disambiguators) {
        resolveRecursively(detector, disambiguators);
        final Map<ResolvedType, Report> reports = this.states.collect();

        final Map<ResolvedType, CollectionResult> definitions = new HashMap<>(reports.size());
        reports.forEach((type, report) -> {
            if (report.isSuccess()) {
                definitions.put(type, report.result());
            } else {
                final ScanInformation scanInformation = report.result().scanInformation();
                throw mapMaidException(type.description() + ": " + report.errorMessage(), scanInformation);
            }
        });

        return definitions;
    }

    private void resolveRecursively(final SimpleDetector detector,
                                    final Disambiguators disambiguators) {
        while (!this.pendingSignals.isEmpty()) {
            final Signal signal = this.pendingSignals.remove();
            this.states = this.states.apply(signal, this);
        }
        final States detected = this.states.apply(detect(detector, disambiguators), this);
        final States resolved = detected.apply(resolve(), this);
        this.states = resolved;

        if (!this.pendingSignals.isEmpty()) {
            resolveRecursively(detector, disambiguators);
        }
    }
}
