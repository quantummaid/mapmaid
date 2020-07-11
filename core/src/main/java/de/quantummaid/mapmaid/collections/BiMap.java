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

package de.quantummaid.mapmaid.collections;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BiMap<A, B> {
    private final Map<A, B> byA;
    private final Map<B, A> byB;

    public static <A, B> BiMap<A, B> biMap(final Map<A, B> map) {
        final Map<B, A> byB = map.entrySet()
                .stream()
                .collect(toMap(Entry::getValue, Entry::getKey));
        return new BiMap<>(map, byB);
    }

    public Optional<B> lookup(final A key) {
        return Optional.ofNullable(byA.get(key));
    }

    public Optional<A> reverseLookup(final B key) {
        return Optional.ofNullable(byB.get(key));
    }

    public int size() {
        return byA.size();
    }

    public void forEach(final BiConsumer<? super A, ? super B> action) {
        byA.forEach(action);
    }

    public List<B> values() {
        return new ArrayList<>(byA.values());
    }

    public List<A> keys() {
        return new ArrayList<>(byA.keySet());
    }
}
