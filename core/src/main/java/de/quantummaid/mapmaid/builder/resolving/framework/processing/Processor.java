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

package de.quantummaid.mapmaid.builder.resolving.framework.processing;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.framework.Report;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.log.StateLog;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.log.StateLogBuilder;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Detector;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Resolver;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.framework.processing.States.states;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactories.stateFactories;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.log.StateLogBuilder.stateLogBuilder;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.DetectSignal.detect;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.ResolveSignal.resolve;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Processor<T> {
    private States<T> states;
    private final Queue<Signal<T>> pendingSignals;
    private final StateLogBuilder<T> log;

    public static <T> Processor<T> processor(final List<StateFactory<T>> stateFactories) {
        final Queue<Signal<T>> pendingSignals = new LinkedList<>();
        final States<T> states = states(smallList(), stateFactories(stateFactories));
        final StateLogBuilder<T> log = stateLogBuilder();
        return new Processor<>(states, pendingSignals, log);
    }

    public void dispatch(final Signal<T> signal) {
        this.pendingSignals.add(signal);
    }

    public void addState(final StatefulDefinition<T> statefulDefinition) {
        validateNotNull(statefulDefinition, "statefulDefinition");
        this.states = this.states.addState(statefulDefinition);
    }

    public Map<TypeIdentifier, CollectionResult<T>> collect(final ReflectMaid reflectMaid,
                                                            final Detector<T> detector,
                                                            final Resolver<T> resolver,
                                                            final MapMaidConfiguration configuration,
                                                            final OnCollectionError<T> onError) {
        resolveRecursively(reflectMaid, detector, resolver, configuration);
        final Map<TypeIdentifier, Report<T>> reports = states.collect();
        final Map<TypeIdentifier, CollectionResult<T>> definitions = new HashMap<>(reports.size());
        final Map<TypeIdentifier, CollectionResult<T>> all = new HashMap<>(reports.size());
        final Map<TypeIdentifier, Report<T>> failures = smallMap();
        reports.forEach((type, report) -> {
            final CollectionResult<T> result = report.result();
            all.put(type, result);
            if (report.isSuccess()) {
                definitions.put(type, result);
            } else {
                failures.put(type, report);
            }
        });

        if (!failures.isEmpty()) {
            onError.onCollectionError(all, log.build(), failures);
        }
        return definitions;
    }

    private void resolveRecursively(final ReflectMaid reflectMaid,
                                    final Detector<T> detector,
                                    final Resolver<T> resolver,
                                    final MapMaidConfiguration configuration) {
        while (!pendingSignals.isEmpty()) {
            final Signal<T> signal = pendingSignals.remove();
            states = states.apply(reflectMaid, signal, this, configuration, log);
        }
        final States<T> detected = states.apply(reflectMaid, detect(detector), this, configuration, log);
        final States<T> resolved = detected.apply(reflectMaid, resolve(resolver), this, configuration, log);
        states = resolved;

        if (!pendingSignals.isEmpty()) {
            resolveRecursively(reflectMaid, detector, resolver, configuration);
        }
    }

    public StateLog<T> log() {
        return log.build();
    }
}
