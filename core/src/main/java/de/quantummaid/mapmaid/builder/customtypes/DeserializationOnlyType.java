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
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.SerializedObjectBuilder0;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.SerializedObjectBuilder0.serializedObjectBuilder0;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeserializationOnlyType<T> implements CustomType<T> {
    private final TypeIdentifier type;
    private final TypeDeserializer deserializer;

    public static <T> SerializedObjectBuilder0<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    public static <T> SerializedObjectBuilder0<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder0(type);
    }

    public static <T> DeserializationOnlyType<T> customPrimitive(final Class<T> type,
                                                                 final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> customPrimitive(final GenericType<T> type,
                                                                 final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(type, deserializer);
    }

    public static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(final Class<T> type,
                                                                            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(final GenericType<T> type,
                                                                            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, deserializer, String.class);
    }

    public static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(final Class<T> type,
                                                                         final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(genericType(type), deserializer, Integer.class);
    }

    public static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(final GenericType<T> type,
                                                                         final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, deserializer, Integer.class);
    }

    public static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(final Class<T> type,
                                                                           final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(final GenericType<T> type,
                                                                           final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(type, deserializer, Float.class);
    }

    public static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(final Class<T> type,
                                                                            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(genericType(type), deserializer, Double.class);
    }

    public static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(final GenericType<T> type,
                                                                            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(type, deserializer, Double.class);
    }

    public static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(final Class<T> type,
                                                                             final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(final GenericType<T> type,
                                                                             final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, deserializer, Boolean.class);
    }

    public static <T> DeserializationOnlyType<T> deserializationOnlyType(final Class<T> type,
                                                                         final TypeDeserializer deserializer) {
        return deserializationOnlyType(genericType(type), deserializer);
    }

    public static <T> DeserializationOnlyType<T> deserializationOnlyType(final GenericType<T> type,
                                                                         final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(deserializer, "deserializer");
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return deserializationOnlyType(typeIdentifier, deserializer);
    }

    public static <T> DeserializationOnlyType<T> deserializationOnlyType(final TypeIdentifier type,
                                                                         final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(deserializer, "deserializer");
        return new DeserializationOnlyType<>(type, deserializer);
    }

    private static <T, B> DeserializationOnlyType<T> createCustomPrimitive(final GenericType<T> type,
                                                                           final CustomCustomPrimitiveDeserializer<T, B> deserializer,
                                                                           final Class<B> baseType) {
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return new DeserializationOnlyType<>(typeIdentifier, typeDeserializer);
    }

    @Override
    public TypeIdentifier type() {
        return this.type;
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        return Optional.empty();
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        return Optional.of(this.deserializer);
    }
}
