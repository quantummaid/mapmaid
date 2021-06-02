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

package de.quantummaid.mapmaid.builder.resolving.framework.processing.log;

import de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.Signal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.framework.processing.log.StateLog.stateLog;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StateLogBuilder<T> {
    private final List<LogEntry<T>> entries = new ArrayList<>();
    private List<LoggedState> previousLoggedStates = List.of();

    public static <T> StateLogBuilder<T> stateLogBuilder() {
        return new StateLogBuilder<>();
    }

    public void log(final Signal<T> signal, final List<LoggedState> loggedStates) {
        final List<LoggedState> changedStates = loggedStates.stream()
                .filter(loggedState -> !previousLoggedStates.contains(loggedState))
                .collect(toList());
        entries.add(LogEntry.logEntry(signal, changedStates));
        previousLoggedStates = loggedStates;
    }

    public StateLog<T> build() {
        return stateLog(entries);
    }
}
