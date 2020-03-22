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

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer09;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Common.createDeserializationOnlyType;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.SerializedObjectBuilder10.serializedObjectBuilder10;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectBuilder09<X, A, B, C, D, E, F, G, H, I> {
    private final Builder builder;

    public static <X, A, B, C, D, E, F, G, H, I> SerializedObjectBuilder09<X, A, B, C, D, E, F, G, H, I> serializedObjectBuilder09(final Builder builder) {
        return new SerializedObjectBuilder09<>(builder);
    }

    public <J> SerializedObjectBuilder10<X, A, B, C, D, E, F, G, H, I, J> withField(final String name,
                                                                                    final Class<J> type) {
        return withField(name, genericType(type));
    }

    public <J> SerializedObjectBuilder10<X, A, B, C, D, E, F, G, H, I, J> withField(final String name,
                                                                                    final GenericType<J> type) {
        this.builder.addDeserializationField(type, name);
        return serializedObjectBuilder10(this.builder);
    }

    public DeserializationOnlyType<X> deserializedUsing(final Deserializer09<X, A, B, C, D, E, F, G, H, I> deserializer) {
        this.builder.setDeserializer(deserializer);
        return createDeserializationOnlyType(this.builder);
    }
}
