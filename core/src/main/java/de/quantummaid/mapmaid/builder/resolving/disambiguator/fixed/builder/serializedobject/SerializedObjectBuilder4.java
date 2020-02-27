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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.Field.field;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.SerializedObjectBuilder5.serializedObjectBuilder5;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectBuilder4<X, A, B, C, D> {
    private final Builder builder;

    public static <X, A, B, C, D> SerializedObjectBuilder4<X, A, B, C, D> serializedObjectBuilder4(final Builder builder) {
        return new SerializedObjectBuilder4<>(builder);
    }

    @SuppressWarnings("unchecked")
    public <E> SerializedObjectBuilder5<X, A, B, C, D, E> withField(final String name,
                                                                    final Class<E> type,
                                                                    final Query<X, E> query) {
        this.builder.addField(field(type, name, (Query<Object, Object>) query));
        return serializedObjectBuilder5(this.builder);
    }

    public Builder deserializedUsing(final Deserializer4<X, A, B, C, D> deserializer) {
        this.builder.setDeserializer(deserializer);
        // TODO
        return this.builder;
    }
}
