/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.builder.serializedobject;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.builder.serializedobject.Field.field;
import static de.quantummaid.mapmaid.builder.builder.serializedobject.SerializedObjectBuilder2.serializedObjectBuilder2;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectBuilder1<X, A> {
    private final Builder builder;

    public static <A, X> SerializedObjectBuilder1<X, A> serializedObjectBuilder1(final Builder builder) {
        return new SerializedObjectBuilder1<>(builder);
    }

    @SuppressWarnings("unchecked")
    public <B> SerializedObjectBuilder2<X, A, B> withField(final String name,
                                                           final Class<B> type,
                                                           final Query<X, B> query) {
        this.builder.addField(field(type, name, (Query<Object, Object>) query));
        return serializedObjectBuilder2(this.builder);
    }

    public Builder deserializedUsing(final Deserializer1<X, A> deserializer) {
        this.builder.setDeserializer(deserializer);
        return this.builder;
    }
}
