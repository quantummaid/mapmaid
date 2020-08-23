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
import de.quantummaid.mapmaid.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.customtypes.serializedobject.deserialization_only.Builder00;
import de.quantummaid.mapmaid.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.mapmaid.customtypes.serializedobject.deserialization_only.Builder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@SuppressWarnings("java:S1200")
public interface DeserializationOnlyType<T> extends CustomType<T> {

    static <T> Builder00<T> serializedObject(final Class<T> type) {
        return serializedObject(genericType(type));
    }

    static <T> Builder00<T> serializedObject(final GenericType<T> type) {
        return serializedObjectBuilder00(type);
    }

    static <T> DeserializationOnlyType<T> customPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return customPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> customPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(type, deserializer);
    }

    static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return stringBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> stringBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return createCustomPrimitive(type, deserializer, String.class);
    }

    static <T> DeserializationOnlyType<T> longBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return longBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> longBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return createCustomPrimitive(type, deserializer, Long.class);
    }

    static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return intBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> intBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return createCustomPrimitive(type, deserializer, Integer.class);
    }

    static <T> DeserializationOnlyType<T> shortBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return shortBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> shortBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return createCustomPrimitive(type, deserializer, Short.class);
    }

    static <T> DeserializationOnlyType<T> byteBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return byteBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> byteBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return createCustomPrimitive(type, deserializer, Byte.class);
    }

    static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return floatBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> floatBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return createCustomPrimitive(type, deserializer, Float.class);
    }

    static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return doubleBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> doubleBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return createCustomPrimitive(type, deserializer, Double.class);
    }

    static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(
            final Class<T> type,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return booleanBasedCustomPrimitive(genericType(type), deserializer);
    }

    static <T> DeserializationOnlyType<T> booleanBasedCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return createCustomPrimitive(type, deserializer, Boolean.class);
    }

    static <C, T> DeserializationOnlyType<C> inlinedCollection(
            final Class<C> collectionType,
            final Class<T> contentType,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> collectionTypeIdentifier = genericType(collectionType);
        final GenericType<T> contentTypeIdentifier = genericType(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, collectionFactory);
    }

    static <C, T> DeserializationOnlyType<C> inlinedCollection(
            final GenericType<C> collectionType,
            final GenericType<T> contentType,
            final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(collectionType);
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(contentType);
        return inlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, collectionFactory);
    }

    static <C> DeserializationOnlyType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionFactory<?, ?> collectionFactory) {
        return new DeserializationOnlyType<>() {
            @Override
            public <X> X create(final CustomTypeDriver<X> driver) {
                return driver.deserializationOnlyCollection(collectionType, contentType, collectionFactory);
            }
        };
    }

    private static <T, B> DeserializationOnlyType<T> createCustomPrimitive(
            final GenericType<T> type,
            final CustomCustomPrimitiveDeserializer<T, B> deserializer,
            final Class<B> baseType) {
        return new DeserializationOnlyType<>() {
            @Override
            public <X> X create(final CustomTypeDriver<X> driver) {
                return driver.deserializationOnlyPrimitive(type, deserializer, baseType);
            }
        };
    }
}
