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

import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogEntry<T> {
    public static final String DELIMITER = "----------------\n";
    private final Signal<T> signal;
    private final List<LoggedState> changedStates;

    public static <T> LogEntry<T> logEntry(final Signal<T> signal,
                                           final List<LoggedState> changedStates) {
        return new LogEntry<>(signal, changedStates);
    }

    public Signal<T> getSignal() {
        return signal;
    }

    public List<LoggedState> getChangedStates() {
        return changedStates;
    }

    String dump() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format("%n%n[%s]%n", signal.description()));
        stringBuilder.append(DELIMITER);
        changedStates.forEach(loggedState -> stringBuilder.append(loggedState.dump()).append("\n"));
        stringBuilder.append(DELIMITER);
        return stringBuilder.toString();
    }
}
