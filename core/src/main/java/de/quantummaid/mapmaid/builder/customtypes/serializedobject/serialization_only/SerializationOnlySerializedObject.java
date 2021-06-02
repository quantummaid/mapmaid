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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only;

import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Query;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder.emptyBuilder;
import static de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier.typeIdentifierFor;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationOnlySerializedObject<T> implements SerializationOnlyType<T> {
    private final Builder builder;

    public static <T> SerializationOnlySerializedObject<T> serializationOnlySerializedObject(
            final ReflectMaid reflectMaid,
            final GenericType<T> type
    ) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return serializationOnlySerializedObject(reflectMaid, typeIdentifier);
    }

    public static <T> SerializationOnlySerializedObject<T> serializationOnlySerializedObject(
            final ReflectMaid reflectMaid,
            final TypeIdentifier typeIdentifier
    ) {
        final Builder builder = emptyBuilder(reflectMaid, typeIdentifier);
        return new SerializationOnlySerializedObject<>(builder);
    }

    public <B> SerializationOnlySerializedObject<T> withField(final String name,
                                                              final Class<B> type,
                                                              final Query<T, B> query) {
        return withField(name, GenericType.genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <B> SerializationOnlySerializedObject<T> withField(final String name,
                                                              final GenericType<B> type,
                                                              final Query<T, B> query) {
        this.builder.addSerializationField(type, name, (Query<Object, Object>) query);
        return this;
    }

    @Override
    public TypeSerializer createSerializer() {
        return this.builder.createSerializer();
    }

    @Override
    public TypeIdentifier type() {
        return this.builder.getType();
    }
}
