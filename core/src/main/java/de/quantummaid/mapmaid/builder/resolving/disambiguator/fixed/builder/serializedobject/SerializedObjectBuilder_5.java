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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.Field.field;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.SerializedObjectBuilder_6.serializedObjectBuilder_6;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectBuilder_5<X, A, B, C, D, E> {
    private final Builder builder;

    public static <X, A, B, C, D, E> SerializedObjectBuilder_5<X, A, B, C, D, E> serializedObjectBuilder_5(final Builder builder) {
        return new SerializedObjectBuilder_5<>(builder);
    }

    @SuppressWarnings("unchecked")
    public <F> SerializedObjectBuilder_6<X, A, B, C, D, E, F> withField(final String name,
                                                                        final Class<F> type,
                                                                        final Query<X, F> query) {
        this.builder.addField(field(type, name, (Query<Object, Object>) query));
        return serializedObjectBuilder_6(this.builder);
    }

    public Builder deserializedUsing(final Deserializer_5<X, A, B, C, D, E> deserializer) {
        this.builder.setDeserializer(deserializer);
        return this.builder; // TODO
    }
}
