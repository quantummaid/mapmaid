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

package de.quantummaid.mapmaid.customtypes.serializedobject.serialization_only;

import de.quantummaid.mapmaid.customtypes.CustomTypeDriver;
import de.quantummaid.mapmaid.customtypes.SerializationOnlyType;
import de.quantummaid.mapmaid.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.customtypes.serializedobject.Query;
import de.quantummaid.mapmaid.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.customtypes.serializedobject.Builder.emptyBuilder;
import static de.quantummaid.mapmaid.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationOnlySerializedObject<T> implements SerializationOnlyType<T> {
    private final Builder builder;

    public static <T> SerializationOnlySerializedObject<T> serializationOnlySerializedObject(
            final GenericType<T> type
    ) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializationOnlySerializedObject(typeIdentifier);
    }

    public static <T> SerializationOnlySerializedObject<T> serializationOnlySerializedObject(
            final TypeIdentifier typeIdentifier
    ) {
        final Builder builder = emptyBuilder(typeIdentifier);
        return new SerializationOnlySerializedObject<>(builder);
    }

    public <B> SerializationOnlySerializedObject<T> withField(final String name,
                                                              final Class<B> type,
                                                              final Query<T, B> query) {
        return withField(name, genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <B> SerializationOnlySerializedObject<T> withField(final String name,
                                                              final GenericType<B> type,
                                                              final Query<T, B> query) {
        this.builder.addSerializationField(type, name, (Query<Object, Object>) query);
        return this;
    }

    @Override
    public <X> X create(final CustomTypeDriver<X> driver) {
        return driver.serializationOnlyObject(builder);
    }
}
