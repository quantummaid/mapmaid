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

package de.quantummaid.mapmaid.builder.contextlog;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ContextLogEntry {
    private final ResolvedType resolvedType;
    private final List<Class<?>> origin;
    private final String message;

    static ContextLogEntry logEntry(final ResolvedType type,
                                    final List<Class<?>> origin,
                                    final String message) {
        validateNotNull(type, "type");
        validateNotNull(origin, "origin");
        validateNotNull(message, "message");
        return new ContextLogEntry(type, origin, message);
    }

    String render() {
        final String origin = this.origin.stream()
                .map(Class::getSimpleName)
                .collect(joining(" -> "));
        return format("%s: %s", origin, this.message);
    }

    boolean isRelated(final ResolvedType type) {
        return this.resolvedType.equals(type);
    }
}
