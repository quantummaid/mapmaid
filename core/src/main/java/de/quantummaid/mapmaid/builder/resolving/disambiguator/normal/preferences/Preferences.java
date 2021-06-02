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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences;

import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirementReasons;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Preferences<T, C> {
    private final Filters<T, C> filters;
    private final List<Preference<T>> preferences;

    public static <T, C> Preferences<T, C> preferences(final List<Filter<T, C>> filters,
                                                       final List<Preference<T>> preferences) {
        return new Preferences<>(Filters.filters(filters), preferences);
    }

    public static <T, C> Preferences<T, C> preferences(final List<Preference<T>> preferences) {
        return preferences(emptyList(), preferences);
    }

    public List<T> preferred(final List<T> options,
                             final C context,
                             final DetectionRequirementReasons detectionRequirements,
                             final ResolvedType containingType,
                             final Striker<T> striker) {
        final List<T> filtered = options.stream()
                .filter(t -> this.filters.isAllowed(t, context, containingType, striker))
                .collect(toList());
        for (final Preference<T> preference : this.preferences) {
            final List<T> preferred = filtered.stream()
                    .filter(element -> preference.prefer(element, detectionRequirements))
                    .collect(toList());
            if (!preferred.isEmpty()) {
                return preferred;
            }
        }
        return filtered;
    }
}
