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

package de.quantummaid.mapmaid.customtypes.serializedobject.duplex;

import de.quantummaid.mapmaid.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.customtypes.serializedobject.Deserializer08;
import de.quantummaid.mapmaid.customtypes.serializedobject.Query;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class Builder08<X, A, B, C, D, E, F, G, H>
        extends AbstractBuilder<X, Deserializer08<X, A, B, C, D, E, F, G, H>> {

    public Builder08(final Builder builder) {
        super(builder);
    }

    public <I> Builder09<X, A, B, C, D, E, F, G, H, I> withField(final String name,
                                                                 final Class<I> type,
                                                                 final Query<X, I> query) {
        return withField(name, genericType(type), query);
    }

    @SuppressWarnings("unchecked")
    public <I> Builder09<X, A, B, C, D, E, F, G, H, I> withField(final String name,
                                                                 final GenericType<I> type,
                                                                 final Query<X, I> query) {
        builder.addDuplexField(type, name, (Query<Object, Object>) query);
        return new Builder09<>(builder);
    }
}
