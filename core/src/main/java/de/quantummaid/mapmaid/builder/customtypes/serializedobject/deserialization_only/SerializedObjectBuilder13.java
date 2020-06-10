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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only;

import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer13;
import de.quantummaid.reflectmaid.GenericType;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Common.createDeserializationOnlyType;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class SerializedObjectBuilder13<X, A, B, C, D, E, F, G, H, I, J, K, L, M> {
    private final Builder builder;

    public <N> SerializedObjectBuilder14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> withField(final String name,
                                                                                                final Class<N> type) {
        return withField(name, genericType(type));
    }

    public <N> SerializedObjectBuilder14<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N> withField(final String name,
                                                                                                final GenericType<N> type) {
        this.builder.addDeserializationField(type, name);
        return new SerializedObjectBuilder14<>(this.builder);
    }

    public DeserializationOnlyType<X> deserializedUsing(final Deserializer13<X, A, B, C, D, E, F, G, H, I, J, K, L, M> deserializer) {
        this.builder.setDeserializer(deserializer);
        return createDeserializationOnlyType(this.builder);
    }
}
