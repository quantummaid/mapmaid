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

import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.Report;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactories;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.log.LoggedState;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.log.StateLogBuilder;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementName;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequirementsDescriber;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.framework.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.log.LoggedState.loggedState;
import static de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements.empty;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class States<T> {
    private final StateFactories<T> stateFactories;
    private final List<StatefulDefinition<T>> states;
    private final List<RequirementName> primaryRequirements;
    private final List<RequirementName> secondaryRequirements;

    public static <T> States<T> states(final List<StatefulDefinition<T>> initialDefinitions,
                                       final StateFactories<T> stateFactories,
                                       final List<RequirementName> primaryRequirements,
                                       final List<RequirementName> secondaryRequirements) {
        final List<StatefulDefinition<T>> states = new ArrayList<>(initialDefinitions);
        return new States<>(stateFactories, states, primaryRequirements, secondaryRequirements);
    }

    public States<T> addState(final StatefulDefinition<T> statefulDefinition) {
        if (contains(statefulDefinition.type(), states)) {
            throw new IllegalArgumentException(format(
                    "state for type '%s' is already registered",
                    statefulDefinition.type().description()));
        }
        final List<StatefulDefinition<T>> newStates = new ArrayList<>(this.states);
        newStates.add(statefulDefinition);
        return new States<>(this.stateFactories, newStates, primaryRequirements, secondaryRequirements);
    }

    public States<T> apply(final Signal<T> signal,
                           final Processor<T> processor,
                           final StateLogBuilder<T> stateLog) {
        final States<T> newStates = apply(signal, processor);
        stateLog.log(signal, newStates.dumpForLogging());
        return newStates;
    }

    private States<T> apply(final Signal<T> signal,
                            final Processor<T> processor) {
        final Optional<TypeIdentifier> optionalTarget = signal.target();
        if (optionalTarget.isEmpty()) {
            final List<StatefulDefinition<T>> newStates = states.stream()
                    .map(signal::handleState)
                    .collect(toList());
            return new States<>(stateFactories, newStates, primaryRequirements, secondaryRequirements);
        } else {
            final TypeIdentifier target = optionalTarget.get();
            final List<StatefulDefinition<T>> newStates = new ArrayList<>(states);

            if (!contains(target, newStates)) {
                final DetectionRequirements detectionRequirements = empty(primaryRequirements, secondaryRequirements);
                final Context<T> context = emptyContext(processor::dispatch, target, detectionRequirements);
                final StatefulDefinition<T> state = stateFactories.createState(target, context);
                newStates.add(state);
            }

            newStates.replaceAll(statefulDefinition -> {
                if (statefulDefinition.context.type().equals(target)) {
                    return signal.handleState(statefulDefinition);
                } else {
                    return statefulDefinition;
                }
            });
            return new States<>(this.stateFactories, newStates, primaryRequirements, secondaryRequirements);
        }
    }

    public Map<TypeIdentifier, Report<T>> collect(final RequirementsDescriber requirementsDescriber) {
        final Map<TypeIdentifier, Report<T>> reports = new HashMap<>();
        states.forEach(statefulDefinition -> {
            final Report<T> report = statefulDefinition.getDefinition(requirementsDescriber);
            if (!report.isEmpty()) {
                final TypeIdentifier type = statefulDefinition.context.type();
                reports.put(type, report);

            }
        });
        return reports;
    }

    private boolean contains(final TypeIdentifier type,
                             final List<StatefulDefinition<T>> states) {
        return states.stream()
                .anyMatch(statefulDefinition -> statefulDefinition.context.type().equals(type));
    }

    private List<LoggedState> dumpForLogging() {
        return states.stream()
                .map(statefulDefinition -> {
                    final TypeIdentifier type = statefulDefinition.type();
                    final DetectionRequirements detectionRequirements = statefulDefinition
                            .context
                            .detectionRequirements();
                    return loggedState(type, statefulDefinition.getClass(), detectionRequirements);
                })
                .collect(toList());
    }
}
