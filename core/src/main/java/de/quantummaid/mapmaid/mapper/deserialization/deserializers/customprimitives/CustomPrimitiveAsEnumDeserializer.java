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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveAsEnumDeserializer implements CustomPrimitiveDeserializer {
    private final Class<? extends Enum<?>> enumType;
    private final Map<String, ? extends Enum<?>> valuesMap;

    public static CustomPrimitiveDeserializer enumDeserializer(final Class<? extends Enum<?>> enumType) {
        final Enum<?>[] values = values(enumType);
        final Map<String, ? extends Enum<?>> valuesMap = Arrays.stream(values)
                .collect(toMap(Enum::name, value -> value));
        return new CustomPrimitiveAsEnumDeserializer(enumType, valuesMap);
    }

    @Override
    public Object deserialize(final Object value) {
        if (!this.valuesMap.containsKey(value)) {
            throw mapMaidException(format("'%s' is not valid value of enum %s", value, this.enumType.getName())); // TODO append enum
            // TODO test
        }
        return this.valuesMap.get(value);
    }

    @Override
    public String description() {
        return format("as custom primitive using values of enum %s", this.enumType.getName());
    }

    private static Enum<?>[] values(final Class<? extends Enum<?>> enumType) {
        try {
            final Method method = enumType.getDeclaredMethod("values");
            return (Enum<?>[]) method.invoke(null);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException("This should never happen", e);
        }
    }
}
