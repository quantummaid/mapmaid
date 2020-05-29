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

package de.quantummaid.mapmaid.mapper.universal;

import de.quantummaid.mapmaid.Collection;
import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class UniversalPrimitiveMapping {
    private final Map<Class<?>, Function<Object, UniversalPrimitive>> mappings = Collection.smallMap();

    public static UniversalPrimitiveMapping universalPrimitiveMapping() {
        return new UniversalPrimitiveMapping();
    }

    public <T> UniversalPrimitiveMapping with(final Class<T> type, final Function<T, UniversalPrimitive> mapping) {
        this.put(type, mapping);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> void put(final Class<T> type, final Function<T, UniversalPrimitive> mapping) {
        this.mappings.put(type, (Function<Object, UniversalPrimitive>) mapping);
    }

    public boolean canMap(final Class<?> type) {
        return this.mappings.containsKey(type);
    }

    public UniversalPrimitive map(final Class<?> type, final Object value) {
        NotNullValidator.validateNotNull(value, "value");
        final Function<Object, UniversalPrimitive> function = this.mappings.get(type);
        if (function != null) {
            return function.apply(value);
        } else {
            final String message = format("Type '%s' is not a primitive.", type.getSimpleName());
            throw MapMaidException.mapMaidException(message);
        }
    }
}
