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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefinitionsCollector<T> {
    private static final int INITIAL_CAPACITY = 10;
    private final Map<ResolvedType, T> map;
    private final String type;

    public static <T> DefinitionsCollector<T> definitionsCollector(final String type) {
        return new DefinitionsCollector<>(new HashMap<>(INITIAL_CAPACITY), type);
    }

    public void add(final ResolvedType type,
                    final T serializer) {
        if (this.map.containsKey(type)) {
            if (this.map.get(type).equals(serializer)) {
                return;
            } else {
                throw new UnsupportedOperationException(String.format(
                        "Unable to register %s %n'%s'%n for the type '%s' because there is already registered %n'%s'",
                        this.type, serializer, type.description(), this.map.get(type)));
            }
        }
        this.map.put(type, serializer);
    }

    public Optional<T> get(final ResolvedType type) {
        if(!isPresent(type)) {
            return empty();
        }
        return of(this.map.get(type));
    }

    public Set<ResolvedType> keys() {
        return this.map.keySet();
    }

    public Collection<T> values() {
        return new HashSet<>(this.map.values());
    }

    public boolean isPresent(final ResolvedType type) {
        return this.map.containsKey(type);
    }
}
