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

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.allowed;

public interface Filter<T, C> {

    @SuppressWarnings("unchecked")
    static <T, V extends T, C> Filter<T, C> filterOfType(final Class<V> type,
                                                         final Filter<V, C> filter) {
        return (t, context, containingType) -> {
            if(!type.isInstance(t)) {
                return allowed();
            } else {
                return filter.filter((V) t, context, containingType);
            }
        };
    }

    FilterResult filter(T t, C context, ResolvedType containingType);
}
