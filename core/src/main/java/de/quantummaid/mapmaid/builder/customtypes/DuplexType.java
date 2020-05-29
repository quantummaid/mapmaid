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

import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionSerializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.SerializedObjectBuilder00;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer.inlinedCollectionDeserializer;
import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionSerializer.inlinedCollectionSerializer;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.SerializedObjectBuilder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DuplexType<T> implements CustomType<T> {
    private final TypeIdentifier type;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static <T> SerializedObjectBuilder00<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    public static <T> SerializedObjectBuilder00<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder00(type);
    }

    public static <T> DuplexType<T> customPrimitive(final Class<T> type,
                                                    final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                    final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> customPrimitive(final GenericType<T> type,
                                                    final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                    final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> customPrimitive(final TypeIdentifier type,
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
        return stringBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> stringBasedCustomPrimitive(final TypeIdentifier type,
                                                               final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                               final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, String.class);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final Class<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> longBasedCustomPrimitive(final Class<T> type,
                                                             final CustomCustomPrimitiveSerializer<T, Long> serializer,
                                                             final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> longBasedCustomPrimitive(final GenericType<T> type,
                                                             final CustomCustomPrimitiveSerializer<T, Long> serializer,
                                                             final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final GenericType<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> intBasedCustomPrimitive(final TypeIdentifier type,
                                                            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Integer.class);
    }

    public static <T> DuplexType<T> shortBasedCustomPrimitive(final Class<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Short> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> shortBasedCustomPrimitive(final GenericType<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Short> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> shortBasedCustomPrimitive(final TypeIdentifier type,
                                                            final CustomCustomPrimitiveSerializer<T, Short> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Short.class);
    }

    public static <T> DuplexType<T> byteBasedCustomPrimitive(final Class<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> byteBasedCustomPrimitive(final GenericType<T> type,
                                                            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> byteBasedCustomPrimitive(final TypeIdentifier type,
                                                            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
                                                            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Byte.class);
    }

    public static <T> DuplexType<T> longBasedCustomPrimitive(final TypeIdentifier type,
                                                             final CustomCustomPrimitiveSerializer<T, Long> serializer,
                                                             final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Long.class);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final Class<T> type,
                                                              final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final GenericType<T> type,
                                                              final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> floatBasedCustomPrimitive(final TypeIdentifier type,
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
        return doubleBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> doubleBasedCustomPrimitive(final TypeIdentifier type,
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
        return booleanBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    public static <T> DuplexType<T> booleanBasedCustomPrimitive(final TypeIdentifier type,
                                                                final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Boolean.class);
    }

    private static <T, B> DuplexType<T> createCustomPrimitive(final TypeIdentifier type,
                                                              final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                              final CustomCustomPrimitiveDeserializer<T, B> deserializer,
                                                              final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        return duplexType(type, typeSerializer, typeDeserializer);
    }

    public static <C, T> DuplexType<C> inlinedCollection(final Class<C> collectionType,
                                                         final Class<T> contentType,
                                                         final InlinedCollectionListExtractor<C, T> listExtractor,
                                                         final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> collectionTypeIdentifier = genericType(collectionType);
        final GenericType<T> contentTypeIdentifier = genericType(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor, collectionFactory);
    }

    public static <C, T> DuplexType<C> inlinedCollection(final GenericType<C> collectionType,
                                                         final GenericType<T> contentType,
                                                         final InlinedCollectionListExtractor<C, T> listExtractor,
                                                         final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(collectionType);
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor, collectionFactory);
    }

    @SuppressWarnings("unchecked")
    public static <C> DuplexType<C> inlinedCollection(final TypeIdentifier collectionType,
                                                      final TypeIdentifier contentType,
                                                      final InlinedCollectionListExtractor<?, ?> listExtractor,
                                                      final InlinedCollectionFactory<?, ?> collectionFactory) {
        final InlinedCollectionSerializer serializer =
                inlinedCollectionSerializer(contentType, (InlinedCollectionListExtractor<Object, Object>) listExtractor);
        final InlinedCollectionDeserializer deserializer =
                inlinedCollectionDeserializer(contentType, (InlinedCollectionFactory<Object, Object>) collectionFactory);
        return duplexType(collectionType, serializer, deserializer);
    }

    public static <T> DuplexType<T> duplexType(final TypeIdentifier type,
                                               final TypeSerializer serializer,
                                               final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(serializer, "serializer");
        validateNotNull(deserializer, "deserializer");
        return new DuplexType<>(type, serializer, deserializer);
    }

    @Override
    public TypeIdentifier type() {
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
