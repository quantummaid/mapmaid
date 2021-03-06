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

import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer10;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Query;
import de.quantummaid.reflectmaid.GenericType;

@SuppressWarnings("java:S1200")
public final class Builder10<X, A, B, C, D, E, F, G, H, I, J>
        extends AbstractBuilder<X, Deserializer10<X, A, B, C, D, E, F, G, H, I, J>> {

    public Builder10(final Builder builder) {
        super(builder);
    }

    public <K> Builder11<X, A, B, C, D, E, F, G, H, I, J, K> withField(final String name,
                                                                       final Class<K> type,
                                                                       final Query<X, K> query) {
        return withField(name, GenericType.genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <K> Builder11<X, A, B, C, D, E, F, G, H, I, J, K> withField(final String name,
                                                                       final GenericType<K> type,
                                                                       final Query<X, K> query) {
        builder.addDuplexField(type, name, (Query<Object, Object>) query);
        return new Builder11<>(builder);
    }
}
