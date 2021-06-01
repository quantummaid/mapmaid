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
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Report;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactories;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.builder.resolving.processing.log.LoggedState;
import de.quantummaid.mapmaid.builder.resolving.processing.log.StateLogBuilder;
import de.quantummaid.mapmaid.builder.resolving.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirementReasons;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.processing.log.LoggedState.loggedState;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class States {
    private final StateFactories stateFactories;
    private final List<StatefulDefinition> states;

    public static States states(final List<StatefulDefinition> initialDefinitions, final StateFactories stateFactories) {
        final List<StatefulDefinition> states = new ArrayList<>(initialDefinitions);
        return new States(stateFactories, states);
    }

    public States addState(final StatefulDefinition statefulDefinition) {
        if (contains(statefulDefinition.type(), this.states)) {
            throw new IllegalArgumentException(format(
                    "state for type '%s' is already registered",
                    statefulDefinition.type().description()));
        }
        final List<StatefulDefinition> newStates = new ArrayList<>(this.states);
        newStates.add(statefulDefinition);
        return new States(this.stateFactories, newStates);
    }

    public States apply(final ReflectMaid reflectMaid,
                        final Signal signal,
                        final Processor processor,
                        final MapMaidConfiguration configuration,
                        final StateLogBuilder stateLog) {
        final States newStates = apply(reflectMaid, signal, processor, configuration);
        stateLog.log(signal, newStates.dumpForLogging());
        return newStates;
    }

    private States apply(final ReflectMaid reflectMaid,
                         final Signal signal,
                         final Processor processor,
                         final MapMaidConfiguration configuration) {
        final Optional<TypeIdentifier> optionalTarget = signal.target();
        if (optionalTarget.isEmpty()) {
            final List<StatefulDefinition> newStates = this.states.stream()
                    .map(signal::handleState)
                    .collect(toList());
            return new States(this.stateFactories, newStates);
        } else {
            final TypeIdentifier target = optionalTarget.get();
            final List<StatefulDefinition> newStates = new ArrayList<>(this.states);

            if (!contains(target, newStates)) {
                final Context context = emptyContext(processor::dispatch, target);
                final StateFactoryResult state = this.stateFactories.createState(reflectMaid, target, context, configuration);
                newStates.add(state.initialState());
                state.signals().forEach(processor::dispatch);
            }

            newStates.replaceAll(statefulDefinition -> {
                if (statefulDefinition.context.type().equals(target)) {
                    return signal.handleState(statefulDefinition);
                } else {
                    return statefulDefinition;
                }
            });
            return new States(this.stateFactories, newStates);
        }
    }

    public Map<TypeIdentifier, Report> collect() {
        final Map<TypeIdentifier, Report> reports = new HashMap<>();
        this.states.forEach(statefulDefinition ->
                statefulDefinition.getDefinition().ifPresent(report -> {
                    final TypeIdentifier type = statefulDefinition.context.type();
                    reports.put(type, report);
                }));
        return reports;
    }

    private static boolean contains(final TypeIdentifier type,
                                    final List<StatefulDefinition> states) {
        return states.stream()
                .anyMatch(statefulDefinition -> statefulDefinition.context.type().equals(type));
    }

    private List<LoggedState> dumpForLogging() {
        return states.stream()
                .map(statefulDefinition -> {
                    final TypeIdentifier type = statefulDefinition.type();
                    final DetectionRequirementReasons detectionRequirementReasons = statefulDefinition
                            .context
                            .scanInformationBuilder()
                            .detectionRequirementReasons();
                    return loggedState(type, statefulDefinition.getClass(), detectionRequirementReasons);
                })
                .collect(toList());
    }
}
