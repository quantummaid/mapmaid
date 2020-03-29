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

package de.quantummaid.mapmaid.builder.customtypes;

import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only.SerializationOnlySerializedObject;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.GenericType;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only.SerializationOnlySerializedObject.serializationOnlySerializedObject;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface SerializationOnlyType<T> extends CustomType<T> {

    static <T> SerializationOnlySerializedObject<T> serializedObject(final Class<T> type) {
        return serializationOnlySerializedObject(genericType(type));
    }

    static <T> SerializationOnlySerializedObject<T> serializedObject(final GenericType<T> type) {
        return serializationOnlySerializedObject(type);
    }

    static <T> SerializationOnlyType<T> customPrimitive(final Class<T> type,
                                                        final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return customPrimitive(genericType(type), serializer);
    }

    static <T> SerializationOnlyType<T> customPrimitive(final GenericType<T> type,
                                                        final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return stringBasedCustomPrimitive(type, serializer);
    }

    static <T> SerializationOnlyType<T> stringBasedCustomPrimitive(final Class<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return stringBasedCustomPrimitive(genericType(type), serializer);
    }

    static <T> SerializationOnlyType<T> stringBasedCustomPrimitive(final GenericType<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return createCustomPrimitive(type, serializer, String.class);
    }

    static <T> SerializationOnlyType<T> intBasedCustomPrimitive(final Class<T> type,
                                                                final CustomCustomPrimitiveSerializer<T, Integer> serializer) {
        return intBasedCustomPrimitive(genericType(type), serializer);
    }

    static <T> SerializationOnlyType<T> intBasedCustomPrimitive(final GenericType<T> type,
                                                                final CustomCustomPrimitiveSerializer<T, Integer> serializer) {
        return createCustomPrimitive(type, serializer, Integer.class);
    }

    static <T> SerializationOnlyType<T> floatBasedCustomPrimitive(final Class<T> type,
                                                                  final CustomCustomPrimitiveSerializer<T, Float> serializer) {
        return floatBasedCustomPrimitive(genericType(type), serializer);
    }

    static <T> SerializationOnlyType<T> floatBasedCustomPrimitive(final GenericType<T> type,
                                                                  final CustomCustomPrimitiveSerializer<T, Float> serializer) {
        return createCustomPrimitive(type, serializer, Float.class);
    }

    static <T> SerializationOnlyType<T> doubleBasedCustomPrimitive(final Class<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Double> serializer) {
        return createCustomPrimitive(genericType(type), serializer, Double.class);
    }

    static <T> SerializationOnlyType<T> doubleBasedCustomPrimitive(final GenericType<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Double> serializer) {
        return createCustomPrimitive(type, serializer, Double.class);
    }

    static <T> SerializationOnlyType<T> booleanBasedCustomPrimitive(final Class<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Boolean> serializer) {
        return createCustomPrimitive(genericType(type), serializer, Boolean.class);
    }

    static <T> SerializationOnlyType<T> booleanBasedCustomPrimitive(final GenericType<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Boolean> serializer) {
        return createCustomPrimitive(type, serializer, Boolean.class);
    }

    private static <T, B> SerializationOnlyType<T> createCustomPrimitive(final GenericType<T> type,
                                                                         final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                                         final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        return SimpleSerializationOnlyType.simpleSerializationOnlyType(type, typeSerializer);
    }

    TypeSerializer createSerializer();

    @Override
    default Optional<TypeDeserializer> deserializer() {
        return Optional.empty();
    }

    @Override
    default Optional<TypeSerializer> serializer() {
        final TypeSerializer serializer = createSerializer();
        return Optional.of(serializer);
    }
}
