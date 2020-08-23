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

package de.quantummaid.mapmaid.customtypes.serializedobject.deserialization_only;

import de.quantummaid.mapmaid.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.customtypes.serializedobject.Deserializer16;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.customtypes.serializedobject.deserialization_only.Common.createDeserializationOnlyType;

@RequiredArgsConstructor
@SuppressWarnings("java:S1200")
public final class Builder16<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> {
    private final Builder builder;

    public DeserializationOnlyType<X> deserializedUsing(
            final Deserializer16<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> deserializer) {
        builder.setDeserializer(deserializer);
        return createDeserializationOnlyType(builder);
    }
}
