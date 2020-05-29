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

package de.quantummaid.mapmaid.shared.mapping;

import de.quantummaid.mapmaid.mapper.universal.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.shared.mapping.TypeMappings.typeMappings;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveMappings {
    private final Map<Class<?>, UniversalTypeMapper> mappings;
    private final TypeMappings typeMappings = typeMappings(
            Mapping.mapping(UniversalDouble.class, UniversalDouble.class, identity()),
            Mapping.mapping(UniversalDouble.class, UniversalLong.class, UniversalLong::universalLongFromUniversalDouble),
            Mapping.mapping(UniversalDouble.class, UniversalString.class, UniversalString::universalStringFromUniversalDouble),
            Mapping.mapping(UniversalLong.class, UniversalLong.class, identity()),
            Mapping.mapping(UniversalLong.class, UniversalDouble.class, UniversalDouble::universalDoubleFromUniversalLong),
            Mapping.mapping(UniversalLong.class, UniversalString.class, UniversalString::universalStringFromUniversalLong),
            Mapping.mapping(UniversalString.class, UniversalString.class, identity()),
            Mapping.mapping(UniversalString.class, UniversalDouble.class, UniversalDouble::universalDoubleFromUniversalString),
            Mapping.mapping(UniversalString.class, UniversalLong.class, UniversalLong::universalLongFromUniversalString),
            Mapping.mapping(UniversalString.class, UniversalBoolean.class, UniversalBoolean::universalBooleanFromUniversalString),
            Mapping.mapping(UniversalBoolean.class, UniversalBoolean.class, identity()),
            Mapping.mapping(UniversalBoolean.class, UniversalString.class, UniversalString::universalStringFromUniversalBoolean));

    public static CustomPrimitiveMappings customPrimitiveMappings(final UniversalTypeMapper... mappings) {
        validateNotNull(mappings, "mappings");
        final Map<Class<?>, UniversalTypeMapper> map = Arrays.stream(mappings)
                .collect(toMap(UniversalTypeMapper::normalType, identity()));
        return new CustomPrimitiveMappings(map);
    }

    public boolean isPrimitiveType(final Class<?> type) {
        validateNotNull(type, "type");
        return this.mappings.containsKey(type);
    }

    public UniversalPrimitive toUniversal(final Object object) {
        validateNotNull(object, "object");
        if (!this.mappings.containsKey(object.getClass())) {
            throw new UnsupportedOperationException(format("Type '%s' is not registered as a primitive", object.getClass()));
        }
        final UniversalTypeMapper universalTypeMapper = this.mappings.get(object.getClass());
        return universalTypeMapper.toUniversal(object);
    }

    public Object fromUniversal(final UniversalPrimitive universal, final Class<?> type) {
        validateNotNull(universal, "universal");
        validateNotNull(type, "type");
        if (!this.mappings.containsKey(type)) {
            throw new UnsupportedOperationException(format("Type '%s' is not registered as a primitive", type));
        }
        final UniversalTypeMapper universalTypeMapper = this.mappings.get(type);
        final Class<? extends UniversalPrimitive> requiredUniversalType = universalTypeMapper.universalType();
        final UniversalPrimitive universalPrimitive = this.typeMappings.map(universal, requiredUniversalType).orElseThrow();
        return universalTypeMapper.fromUniversal(universalPrimitive);
    }

    public List<Class<?>> registeredTypes() {
        return new ArrayList<>(mappings.keySet());
    }

}
