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

import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionSerializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionSerializer.inlinedCollectionSerializer;
import static de.quantummaid.mapmaid.builder.customtypes.SimpleSerializationOnlyType.simpleSerializationOnlyType;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

public interface SerializationOnlyType<T> extends CustomType<T> {

    @SuppressWarnings("unchecked")
    static <C> SerializationOnlyType<C> inlinedCollection(
            final TypeIdentifier collectionType,
            final TypeIdentifier contentType,
            final InlinedCollectionListExtractor<?, ?> listExtractor) {
        final InlinedCollectionSerializer serializer =
                inlinedCollectionSerializer(
                        contentType,
                        (InlinedCollectionListExtractor<Object, Object>) listExtractor
                );
        return serializationOnlyType(collectionType, serializer);
    }

    static <T, B> SerializationOnlyType<T> createCustomPrimitive(
            final TypeIdentifier typeIdentifier,
            final CustomCustomPrimitiveSerializer<T, B> serializer,
            final Class<B> baseType) {
        final TypeSerializer typeSerializer = serializer.toTypeSerializer(baseType);
        return serializationOnlyType(typeIdentifier, typeSerializer);
    }

    static <T> SerializationOnlyType<T> serializationOnlyType(final TypeIdentifier type,
                                                              final TypeSerializer serializer) {
        validateNotNull(type, "type");
        validateNotNull(serializer, "serializer");
        return simpleSerializationOnlyType(type, serializer);
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
