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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences;

import de.quantummaid.mapmaid.Collection;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Preferences<T> {
    private final Filters<T> filters;
    private final List<Preference<T>> preferences;

    public static <T> Preferences<T> preferences(final List<Filter<T>> filters,
                                                 final List<Preference<T>> preferences) {
        return new Preferences<>(Filters.filters(filters), preferences);
    }

    public static <T> Preferences<T> preferences(final List<Preference<T>> preferences) {
        return preferences(emptyList(), preferences);
    }

    public List<T> prefered(final List<T> options, final BiConsumer<T, List<String>> striker) {
        final List<T> filtered = Collection.smallList();
        for(final T t : options) {
            final FilterResult filterResult = this.filters.isAllowed(t);
            if(filterResult.isAllowed()) {
                filtered.add(t);
            } else {
                striker.accept(t, filterResult.reasonsForDenial());
            }
        }
        for (final Preference<T> preference : this.preferences) {
            final List<T> preferred = filtered.stream()
                    .filter(preference::prefer)
                    .collect(toList());
            if (!preferred.isEmpty()) {
                return preferred;
            }
        }
        return filtered;
    }
}
