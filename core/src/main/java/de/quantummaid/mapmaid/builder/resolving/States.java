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

import de.quantummaid.mapmaid.builder.resolving.signals.Signal;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.unreasoned.Unreasoned.unreasoned;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class States {
    private final List<StatefulDefinition> states;

    public static States emptyStates() {
        return new States(new LinkedList<>());
    }

    public States apply(final Signal signal, final Processor processor) {
        if (signal.target().isEmpty()) {
            final List<StatefulDefinition> newStates = this.states.stream()
                    .map(signal::handleState)
                    .collect(toList());
            return new States(newStates);
        } else {
            final ResolvedType target = signal.target().get();
            final List<StatefulDefinition> newStates = new LinkedList<>(this.states);

            if (!contains(target, newStates)) {
                newStates.add(unreasoned(emptyContext(processor, target)));
            }

            newStates.replaceAll(statefulDefinition -> {
                if (statefulDefinition.context.type().equals(target)) {
                    final StatefulDefinition replacement = signal.handleState(statefulDefinition);
                    return replacement;
                } else {
                    return statefulDefinition;
                }
            });
            return new States(newStates);
        }
    }

    public Map<ResolvedType, Report> collect() {
        return this.states.stream().collect(toMap(
                state -> state.context.type(), StatefulDefinition::getDefinition)
        );
    }

    public int size() {
        return this.states.size();
    }

    private static boolean contains(final ResolvedType type,
                                    final List<StatefulDefinition> states) {
        return states.stream()
                .anyMatch(statefulDefinition -> statefulDefinition.context.type().equals(type));
    }
}
