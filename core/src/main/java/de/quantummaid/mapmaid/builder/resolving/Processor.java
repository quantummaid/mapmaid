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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.signals.Signal;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static de.quantummaid.mapmaid.builder.resolving.States.emptyStates;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.detect;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.resolve;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Processor {
    private States states = emptyStates();
    private final Queue<Signal> pendingSignals = new LinkedList<>();

    public static Processor processor() {
        return new Processor();
    }

    public void dispatch(final Signal signal) {
        this.pendingSignals.add(signal);
    }

    public Map<ResolvedType, Definition> collect(final NewSimpleDetector detector, final BuildContextLog log) {
        resolveRecursively(detector, log);
        final Map<ResolvedType, Report> reports = this.states.collect();

        final Map<ResolvedType, Definition> definitions = new HashMap<>();
        reports.forEach((type, report) -> {
            if(report.isSuccess()) {
                definitions.put(type, report.definition());
            } else {
                throw new UnsupportedOperationException(type.description() + ": " + report.errorMessage()); // TODO
            }
        });

        return definitions;
    }

    private void resolveRecursively(final NewSimpleDetector detector,
                                    final BuildContextLog log) {
        while (!this.pendingSignals.isEmpty()) {
            final Signal signal = this.pendingSignals.remove();
            this.states = this.states.apply(signal, this);
        }
        final States detected = this.states.apply(detect(detector, log), this);
        final States resolved = detected.apply(resolve(), this);
        this.states = resolved;

        if (!this.pendingSignals.isEmpty()) {
            System.out.println("detected = " + detected);
            resolveRecursively(detector, log);
        }
    }
}
