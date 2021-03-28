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
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer.inlinedCollectionDeserializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class DeserializationOnlyType<T> implements CustomType<T> {
    private final TypeIdentifier type;
    private final TypeDeserializer deserializer;

    @SuppressWarnings("unchecked")
    public static <C> DeserializationOnlyType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionFactory<?, ?> collectionFactory) {
        final InlinedCollectionDeserializer deserializer =
                inlinedCollectionDeserializer(
                        contentType,
                        (InlinedCollectionFactory<Object, Object>) collectionFactory
                );
        return deserializationOnlyType(collectionType, deserializer);
    }

    public static <T> DeserializationOnlyType<T> deserializationOnlyType(
            final TypeIdentifier type,
            final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        validateNotNull(deserializer, "deserializer");
        return new DeserializationOnlyType<>(type, deserializer);
    }

    public static <T, B> DeserializationOnlyType<T> createCustomPrimitive(
            final TypeIdentifier typeIdentifier,
            final CustomCustomPrimitiveDeserializer<T, B> deserializer,
            final Class<B> baseType) {
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
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
