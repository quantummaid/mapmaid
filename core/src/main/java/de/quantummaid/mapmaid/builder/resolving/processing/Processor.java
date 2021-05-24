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

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.processing.log.StateLog;
import de.quantummaid.mapmaid.builder.resolving.processing.log.StateLogBuilder;
import de.quantummaid.mapmaid.builder.resolving.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.processing.States.states;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactories.stateFactories;
import static de.quantummaid.mapmaid.builder.resolving.processing.log.StateLogBuilder.stateLogBuilder;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.DetectSignal.detect;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.ResolveSignal.resolve;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Processor {
    private States states;
    private final Queue<Signal> pendingSignals;
    private final StateLogBuilder log;

    public static Processor processor(final List<StateFactory> stateFactories) {
        final Queue<Signal> pendingSignals = new LinkedList<>();
        final States states = states(smallList(), stateFactories(stateFactories));
        final StateLogBuilder log = stateLogBuilder();
        return new Processor(states, pendingSignals, log);
    }

    public void dispatch(final Signal signal) {
        this.pendingSignals.add(signal);
    }

    public void addState(final StatefulDefinition statefulDefinition) {
        validateNotNull(statefulDefinition, "statefulDefinition");
        this.states = this.states.addState(statefulDefinition);
    }

    public Map<TypeIdentifier, CollectionResult> collect(final ReflectMaid reflectMaid,
                                                         final SimpleDetector detector,
                                                         final Disambiguators disambiguators,
                                                         final MapMaidConfiguration configuration) {
        resolveRecursively(reflectMaid, detector, disambiguators, configuration);
        final Map<TypeIdentifier, Report> reports = this.states.collect();

        final Map<TypeIdentifier, ScanInformationBuilder> scanInformationBuilders = new HashMap<>(reports.size());
        final Map<TypeIdentifier, CollectionResult> definitions = new HashMap<>(reports.size());
        final Map<TypeIdentifier, Report> failures = smallMap();
        reports.forEach((type, report) -> {
            final CollectionResult result = report.result();
            scanInformationBuilders.put(type, result.scanInformation());
            if (report.isSuccess()) {
                definitions.put(type, result);
            } else {
                failures.put(type, report);
            }
        });

        if (!failures.isEmpty()) {
            final DebugInformation debugInformation = debugInformation(scanInformationBuilders, log.build(), reflectMaid);
            final StringJoiner errorMessageJoiner = new StringJoiner("\n\n");
            final List<ScanInformation> scanInformations = new ArrayList<>(failures.size());
            failures.forEach((typeIdentifier, report) -> {
                errorMessageJoiner.add(typeIdentifier.description() + ": " + report.errorMessage());
                final ScanInformation scanInformation = debugInformation.scanInformationFor(typeIdentifier);
                scanInformations.add(scanInformation);
            });
            final String errorMessage = format("The following classes could not be detected properly:%n%n%s",
                    errorMessageJoiner.toString());
            throw mapMaidException(errorMessage, scanInformations);
        }
        return definitions;
    }

    private void resolveRecursively(final ReflectMaid reflectMaid,
                                    final SimpleDetector detector,
                                    final Disambiguators disambiguators,
                                    final MapMaidConfiguration configuration) {
        final List<TypeIdentifier> injectedTypes = this.states.injections();
        while (!pendingSignals.isEmpty()) {
            final Signal signal = pendingSignals.remove();
            states = states.apply(reflectMaid, signal, this, configuration, log);
        }
        final States detected = states.apply(reflectMaid, detect(detector, disambiguators, injectedTypes), this, configuration, log);
        final States resolved = detected.apply(reflectMaid, resolve(), this, configuration, log);
        states = resolved;

        if (!pendingSignals.isEmpty()) {
            resolveRecursively(reflectMaid, detector, disambiguators, configuration);
        }
    }

    public StateLog log() {
        return log.build();
    }
}
