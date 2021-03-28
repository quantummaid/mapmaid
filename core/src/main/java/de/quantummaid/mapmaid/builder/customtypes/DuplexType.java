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
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionDeserializer.inlinedCollectionDeserializer;
import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionSerializer.inlinedCollectionSerializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S1200", "java:S1448"})
public final class DuplexType<T> implements CustomType<T> {
    private final TypeIdentifier type;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static <T, B> DuplexType<T> createCustomPrimitive(
            final TypeIdentifier type,
            final CustomCustomPrimitiveSerializer<T, B> serializer,
            final CustomCustomPrimitiveDeserializer<T, B> deserializer,
            final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        final TypeDeserializer typeDeserializer = deserializer.toTypeDeserializer(baseType);
        return duplexType(type, typeSerializer, typeDeserializer);
    }

    @SuppressWarnings("unchecked")
    public static <C> DuplexType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionListExtractor<?, ?> listExtractor,
            final InlinedCollectionFactory<?, ?> collectionFactory) {
        final InlinedCollectionSerializer serializer =
                inlinedCollectionSerializer(
                        contentType,
                        (InlinedCollectionListExtractor<Object, Object>) listExtractor
                );
        final InlinedCollectionDeserializer deserializer =
                inlinedCollectionDeserializer(
                        contentType,
                        (InlinedCollectionFactory<Object, Object>) collectionFactory
                );
        return duplexType(collectionType, serializer, deserializer);
    }

    public static <T> DuplexType<T> duplexType(
            final TypeIdentifier type,
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
