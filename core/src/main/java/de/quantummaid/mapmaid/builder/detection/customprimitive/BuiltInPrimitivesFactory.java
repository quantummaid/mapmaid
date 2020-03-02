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

package de.quantummaid.mapmaid.builder.detection.customprimitive;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.function.Function.identity;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuiltInPrimitivesFactory implements DefinitionFactory {
    private static final Map<ResolvedType, Definition> PRIMITIVE_DEFINITIONS;

    static {
        PRIMITIVE_DEFINITIONS = new HashMap<>();
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(int.class), toCustomPrimitiveDefinition(int.class, Integer::parseInt));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Integer.class), toCustomPrimitiveDefinition(Integer.class, Integer::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(long.class), toCustomPrimitiveDefinition(long.class, Long::parseLong));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Long.class), toCustomPrimitiveDefinition(Long.class, Long::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(short.class), toCustomPrimitiveDefinition(short.class, Short::parseShort));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Short.class), toCustomPrimitiveDefinition(Short.class, Short::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(double.class), toCustomPrimitiveDefinition(double.class, Double::parseDouble));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Double.class), toCustomPrimitiveDefinition(Double.class, Double::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(float.class), toCustomPrimitiveDefinition(float.class, Float::parseFloat));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Float.class), toCustomPrimitiveDefinition(Float.class, Float::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(boolean.class), toCustomPrimitiveDefinition(boolean.class, Boolean::parseBoolean));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(Boolean.class), toCustomPrimitiveDefinition(Boolean.class, Boolean::valueOf));
        PRIMITIVE_DEFINITIONS.put(fromClassWithoutGenerics(String.class), toCustomPrimitiveDefinition(String.class, identity()));
    }

    public static DefinitionFactory builtInPrimitivesFactory() {
        return new BuiltInPrimitivesFactory();
    }

    @Override
    public Optional<Definition> analyze(final ResolvedType type,
                                        final RequiredCapabilities capabilities) {
        if (PRIMITIVE_DEFINITIONS.containsKey(type)) {
            return of(PRIMITIVE_DEFINITIONS.get(type));
        }
        return empty();
    }

    private static <T> Definition toCustomPrimitiveDefinition(final Class<T> type,
                                                              final Function<String, T> deserializer) {
        final CustomPrimitiveSerializer customPrimitiveSerializer = obj -> {
            if (obj != null) {
                return String.valueOf(obj);
            } else {
                return null;
            }
        };
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return generalDefinition(
                resolvedType,
                customPrimitiveSerializer,
                (CustomPrimitiveDeserializer) value -> deserializer.apply((String) value));
    }
}
