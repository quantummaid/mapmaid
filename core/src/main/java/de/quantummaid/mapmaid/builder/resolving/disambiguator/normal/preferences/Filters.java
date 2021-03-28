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

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.combined;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Filters<T, C> {
    private final List<Filter<T, C>> filters;

    public static <T, C> Filters<T, C> filters(final List<Filter<T, C>> filters) {
        return new Filters<>(filters);
    }

    public boolean isAllowed(final T t, final C context, final ResolvedType containingType, final Striker<T> striker) {
        final List<FilterResult> filterResults = this.filters.stream()
                .map(filter -> filter.filter(t, context, containingType))
                .collect(toList());
        final FilterResult combinedResult = combined(filterResults);
        if (combinedResult.isAllowed()) {
            return true;
        } else {
            striker.strike(t, combinedResult.reasonsForDenial());
            return false;
        }
    }
}
