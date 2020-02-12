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

package de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PrimitiveInformation {
    final Class<?> type;
    final Function<String, Object> deserializer;
    final String description;

    public static Map<ResolvedType, PrimitiveInformation> primitiveInformations() {
        return Stream.of(
                primitiveInformation(String.class, identity(), "is already a String"),
                primitiveInformation(int.class, Integer::parseInt, "Integer::parseInt"),
                primitiveInformation(Integer.class, Integer::valueOf, "Integer::valueOf"),
                primitiveInformation(long.class, Long::parseLong, "Long::parseLong"),
                primitiveInformation(Long.class, Long::valueOf, "Long::valueOf"),
                primitiveInformation(short.class, Short::parseShort, "Short::parseShort"),
                primitiveInformation(Short.class, Short::valueOf, "Short::valueOf"),
                primitiveInformation(double.class, Double::parseDouble, "Double::parseDouble"),
                primitiveInformation(Double.class, Double::valueOf, "Double::valueOf"),
                primitiveInformation(float.class, Float::parseFloat, "Float::parseFloat"),
                primitiveInformation(Float.class, Float::valueOf, "Float::valueOf"),
                primitiveInformation(boolean.class, Boolean::parseBoolean, "Boolean::parseBoolean"),
                primitiveInformation(Boolean.class, Boolean::valueOf, "Boolean::valueOf")
        )
                .collect(toMap(
                        primitiveInformation -> fromClassWithoutGenerics(primitiveInformation.type),
                        primitiveInformation -> primitiveInformation)
                );

    }

    @SuppressWarnings("unchecked")
    public static <T> PrimitiveInformation primitiveInformation(final Class<T> type,
                                                                final Function<String, T> deserializer,
                                                                final String description) {
        NotNullValidator.validateNotNull(type, "type");
        NotNullValidator.validateNotNull(deserializer, "deserializer");
        NotNullValidator.validateNotNull(description, "description");
        return new PrimitiveInformation(type, (Function<String, Object>) deserializer, description);
    }
}
