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

package de.quantummaid.mapmaid.mapper.marshalling.registry;

import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static de.quantummaid.mapmaid.mapper.marshalling.UnsupportedMarshallingTypeException.unsupportedMarshallingTypeException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
final class Registry<T> {
    private final Map<MarshallingType<?>, T> map;

    static <T> Registry<T> registry(final Map<MarshallingType<?>, T> map) {
        validateNotNull(map, "map");
        return new Registry<>(map);
    }

    T getForType(final MarshallingType<?> type) {
        validateNotNull(type, "type");
        final T entry = this.map.get(type);
        if (entry == null) {
            throw unsupportedMarshallingTypeException(type, this.map.keySet());
        }
        return entry;
    }

    Set<MarshallingType<?>> supportedTypes() {
        return this.map.keySet();
    }
}
