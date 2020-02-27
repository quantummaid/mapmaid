/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.customprimitive;

import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.function.Function;

import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;

public final class CustomPrimitiveBuilder {

    private CustomPrimitiveBuilder() {
    }

    // TODO lambdas -> interfaces
    @SuppressWarnings("unchecked")
    public static <T> Definition customPrimitive(final Class<T> type,
                                                 final Function<T, String> serializationMethod,
                                                 final Function<String, T> deserializationMethod) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return customPrimitive(resolvedType,
                (Function<Object, String>) serializationMethod,
                (Function<String, Object>) deserializationMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T, U> Definition customPrimitive(final Class<T> type,
                                                    final Class<U> baseType,
                                                    final Function<T, U> serializationMethod,
                                                    final Function<U, T> deserializationMethod) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return customPrimitive(resolvedType, baseType,
                (Function<Object, U>) serializationMethod,
                (Function<U, Object>) deserializationMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T> Definition customPrimitive(final ResolvedType type,
                                                 final Function<Object, String> serializationMethod,
                                                 final Function<String, Object> deserializationMethod) {
        final CustomPrimitiveSerializer serializer = createSerializer(type.assignableType(), serializationMethod, String.class);
        final CustomPrimitiveDeserializer deserializer = createDeserializer(String.class, (Function<Object, ?>) (Object) deserializationMethod);
        return generalDefinition(type, serializer, deserializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Definition customPrimitive(final ResolvedType type,
                                                 final Class<T> baseType,
                                                 final Function<Object, T> serializationMethod,
                                                 final Function<T, Object> deserializationMethod) {
        final CustomPrimitiveSerializer serializer = createSerializer(type.assignableType(), serializationMethod, baseType);
        final CustomPrimitiveDeserializer deserializer = createDeserializer(baseType, (Function<Object, ?>) deserializationMethod);
        return generalDefinition(type, serializer, deserializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Definition serializationOnlyCustomPrimitive(final Class<T> type,
                                                                  final Function<T, String> serializationMethod) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return serializationOnlyCustomPrimitive(resolvedType, (Function<Object, String>) serializationMethod);
    }

    public static <T> Definition serializationOnlyCustomPrimitive(final ResolvedType type,
                                                                  final Function<Object, String> serializationMethod) {
        final CustomPrimitiveSerializer serializer = createSerializer(type.assignableType(), serializationMethod, String.class);
        return generalDefinition(type, serializer, null);
    }

    public static <T> Definition deserializationOnlyCustomPrimitive(final Class<T> type,
                                                                    final Function<String, T> deserializationMethod) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return deserializationOnlyCustomPrimitive(resolvedType, deserializationMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T> Definition deserializationOnlyCustomPrimitive(final ResolvedType type,
                                                                    final Function<String, T> deserializationMethod) {
        final CustomPrimitiveDeserializer deserializer = createDeserializer(String.class, (Function<Object, ?>) (Object) deserializationMethod);
        return generalDefinition(type, null, deserializer);
    }

    private static CustomPrimitiveSerializer createSerializer(final Class<?> type,
                                                              final Function<Object, ?> serializationMethod,
                                                              final Class<?> baseType) {
        final CustomPrimitiveSerializer serializer = new CustomPrimitiveSerializer() {
            @Override
            public Object serialize(final Object object) {
                return serializationMethod.apply(type.cast(object));
            }

            @Override
            public String description() {
                return serializationMethod.toString();
            }

            @Override
            public Class<?> baseType() {
                return baseType;
            }
        };
        return serializer;
    }

    private static CustomPrimitiveDeserializer createDeserializer(final Class<?> baseType,
                                                                  final Function<Object, ?> deserializationMethod) {
        final CustomPrimitiveDeserializer deserializer = new CustomPrimitiveDeserializer() {
            @Override
            public Object deserialize(final Object value) {
                return deserializationMethod.apply(value);
            }

            @Override
            public String description() {
                return deserializationMethod.toString();
            }

            @Override
            public Class<?> baseType() {
                return baseType;
            }
        };
        return deserializer;
    }
}
