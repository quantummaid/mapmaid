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

package de.quantummaid.mapmaid.mapper.marshalling;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarshallerRegistry<T> {
    private final Map<MarshallingType, T> map;

    public static <T> MarshallerRegistry<T> marshallerRegistry(final Map<MarshallingType, T> map) {
        NotNullValidator.validateNotNull(map, "map");
        return new MarshallerRegistry<>(map);
    }

    public T getForType(final MarshallingType type) {
        NotNullValidator.validateNotNull(type, "type");
        final T entry = this.map.get(type);
        if (entry == null) {
            throw UnsupportedMarshallingTypeException.unsupportedMarshallingTypeException(type, this.map.keySet());
        }
        return entry;
    }

    public Set<MarshallingType> supportedTypes() {
        return this.map.keySet();
    }
}
