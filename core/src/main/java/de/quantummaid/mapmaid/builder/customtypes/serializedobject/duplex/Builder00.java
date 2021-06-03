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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex;

import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer00;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Query;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder.emptyBuilder;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.Common.createDuplexType;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder00<X> {
    private final Builder builder;

    public static <X> Builder00<X> serializedObjectBuilder00(final ReflectMaid reflectMaid,
                                                             final GenericType<X> type) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        final Builder builder = emptyBuilder(reflectMaid, typeIdentifier);
        return new Builder00<>(builder);
    }

    public <A> Builder01<X, A> withField(final String name,
                                         final Class<A> type,
                                         final Query<X, A> query) {
        return withField(name, genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <A> Builder01<X, A> withField(final String name,
                                         final GenericType<A> type,
                                         final Query<X, A> query) {
        builder.addDuplexField(type, name, (Query<Object, Object>) query);
        return new Builder01<>(builder);
    }

    public DuplexType<X> deserializedUsing(final Deserializer00<X> deserializer) {
        builder.setDeserializer(deserializer);
        return createDuplexType(builder);
    }
}
