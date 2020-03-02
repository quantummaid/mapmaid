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

package de.quantummaid.mapmaid.builder.contextlog;

import de.quantummaid.mapmaid.mapper.DefinitionScanLog;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuildContextLog implements DefinitionScanLog {
    private final List<Class<?>> origin;
    private final List<ContextLogEntry> entries;

    public static BuildContextLog emptyLog() {
        return new BuildContextLog(new LinkedList<>(), new LinkedList<>());
    }

    public BuildContextLog stepInto(final Class<?> origin) {
        final LinkedList<Class<?>> newOrigin = new LinkedList<>(this.origin);
        newOrigin.add(origin);
        return new BuildContextLog(newOrigin, this.entries);
    }

    public void logReject(final ResolvedType type,
                          final String message) {
        log(type, format("rejecting '%s' because: %s", type.description(), message));
    }

    public void log(final ResolvedType type,
                    final String message) {
        final ContextLogEntry entry = ContextLogEntry.logEntry(type, this.origin, message);
        this.entries.add(entry);
    }

    @Override
    public String summaryFor(final ResolvedType resolvedType) {
        final List<String> relatedEntries = this.entries.stream()
                .filter(contextLogEntry -> contextLogEntry.isRelated(resolvedType))
                .map(ContextLogEntry::render)
                .collect(Collectors.toList());
        if (relatedEntries.isEmpty()) {
            return format("%nNo log entries for %s", resolvedType.description());
        }
        final String joined = String.join("\n", relatedEntries);
        return format("%nLog entries for '%s'%n%s:", resolvedType.description(), joined);
    }
}
