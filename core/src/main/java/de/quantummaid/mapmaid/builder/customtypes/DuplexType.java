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

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.SerializedObjectBuilder0;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.SerializedObjectBuilder0.serializedObjectBuilder0;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DuplexType<T> implements CustomType<T> {
    private final GenericType<T> type;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static <T> SerializedObjectBuilder0<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    public static <T> SerializedObjectBuilder0<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder0(type);
    }

    public static <T> DuplexType<T> customPrimitive(final Class<T> type,
                                                    final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                    final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> customPrimitive(final GenericType<T> type,
                                                    final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                    final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(type, serializer, deserializer);
    }

    public static <T> DuplexType<T> stringBasedCustomPrimitive(final Class<T> type,
                                                               final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> stringBasedCustomPrimitive(final GenericType<T> type,
                                                               final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, String.class);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final Class<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final GenericType<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Integer.class);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final Class<T> type,
                                                              final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final GenericType<T> type,
                                                              final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Float.class);
    }

    public static <T> DuplexType<T> doubleBasedCustomPrimitive(final Class<T> type,
                                                               final CustomCustomPrimitiveSerializer<T, Double> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return doubleBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> doubleBasedCustomPrimitive(final GenericType<T> type,
                                                               final CustomCustomPrimitiveSerializer<T, Double> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Double.class);
    }

    public static <T> DuplexType<T> booleanBasedCustomPrimitive(final Class<T> type,
                                                                final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> booleanBasedCustomPrimitive(final GenericType<T> type,
                                                                final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Boolean.class);
    }

    private static <T, B> DuplexType<T> createCustomPrimitive(final GenericType<T> type,
                                                              final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, B> deserializer,
                                                              final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        return new DuplexType<>(type, typeSerializer, typeDeserializer);
    }

    public static <T> DuplexType<T> duplexType(final GenericType<T> type,
                                               final TypeSerializer serializer,
                                               final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(serializer, "serializer");
        validateNotNull(deserializer, "deserializer");
        return new DuplexType<>(type, serializer, deserializer);
    }

    @Override
    public GenericType<T> type() {
        return this.type;
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        return Optional.of(this.deserializer);
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        return Optional.of(this.serializer);
    }
}
