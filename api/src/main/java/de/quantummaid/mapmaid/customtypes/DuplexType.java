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

package de.quantummaid.mapmaid.customtypes;

import de.quantummaid.mapmaid.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.mapmaid.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.customtypes.serializedobject.duplex.Builder00;
import de.quantummaid.mapmaid.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.mapmaid.customtypes.serializedobject.duplex.Builder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@SuppressWarnings({"java:S1200", "java:S1448"})
public interface DuplexType<T> extends CustomType<T> {

    static <T> Builder00<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    static <T> Builder00<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder00(type);
    }

    static <T> DuplexType<T> customPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> customPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> customPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(type, serializer, deserializer);
    }

    static <T> DuplexType<T> stringBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> stringBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> stringBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, String> serializer,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, String.class);
    }

    static <T> DuplexType<T> longBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Long> serializer,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> longBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Long> serializer,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> longBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Long> serializer,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Long.class);
    }

    static <T> DuplexType<T> intBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> intBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> intBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Integer> serializer,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Integer.class);
    }

    static <T> DuplexType<T> shortBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Short> serializer,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> shortBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Short> serializer,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> shortBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Short> serializer,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Short.class);
    }

    static <T> DuplexType<T> byteBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> byteBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> byteBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Byte> serializer,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Byte.class);
    }

    static <T> DuplexType<T> floatBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Float> serializer,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> floatBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Float> serializer,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> floatBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Float> serializer,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Float.class);
    }

    static <T> DuplexType<T> doubleBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Double> serializer,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return doubleBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> doubleBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Double> serializer,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return doubleBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> doubleBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Double> serializer,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Double.class);
    }

    static <T> DuplexType<T> booleanBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(genericType(type), serializer, deserializer);
    }

    static <T> DuplexType<T> booleanBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(typeIdentifierFor(type), serializer, deserializer);
    }

    static <T> DuplexType<T> booleanBasedCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, serializer, deserializer, Boolean.class);
    }

    private static <T, B> DuplexType<T> createCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, B> serializer,
            final CustomCustomPrimitiveDeserializer<T, B> deserializer,
            final Class<B> baseType) {
        return new DuplexType<>() {
            @Override
            public <X> X create(final CustomTypeDriver<X> driver) {
                return driver.duplexPrimitive(type, serializer, deserializer, baseType);
            }
        };
    }

    static <C, T> DuplexType<C> inlinedCollection(
            final Class<C> collectionType,
            final Class<T> contentType,
            final InlinedCollectionListExtractor<C, T> listExtractor,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> collectionTypeIdentifier = genericType(collectionType);
        final GenericType<T> contentTypeIdentifier = genericType(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor, collectionFactory);
    }

    static <C, T> DuplexType<C> inlinedCollection(
            final GenericType<C> collectionType,
            final GenericType<T> contentType,
            final InlinedCollectionListExtractor<C, T> listExtractor,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(collectionType);
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor, collectionFactory);
    }

    static <C> DuplexType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionListExtractor<?, ?> listExtractor,
            final InlinedCollectionFactory<?, ?> collectionFactory) {
        return new DuplexType<>() {
            @Override
            public <X> X create(final CustomTypeDriver<X> driver) {
                return driver.duplexCollection(collectionType, contentType, listExtractor, collectionFactory);
            }
        };
    }
}
