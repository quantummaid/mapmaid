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
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;

import static de.quantummaid.mapmaid.mapper.marshalling.registry.Registry.registry;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnmarshallerRegistry {
    private final Registry<Unmarshaller<?>> registry;

    public static UnmarshallerRegistry unmarshallerRegistry(final Map<MarshallingType<?>, Unmarshaller<?>> map) {
        validateNotNull(map, "map");
        final Registry<Unmarshaller<?>> registry = registry(map);
        return new UnmarshallerRegistry(registry);
    }

    @SuppressWarnings("unchecked")
    public <M> Unmarshaller<M> getForType(final MarshallingType<M> type) {
        validateNotNull(type, "type");
        return (Unmarshaller<M>) registry.getForType(type);
    }

    public Set<MarshallingType<?>> supportedTypes() {
        return registry.supportedTypes();
    }
}
