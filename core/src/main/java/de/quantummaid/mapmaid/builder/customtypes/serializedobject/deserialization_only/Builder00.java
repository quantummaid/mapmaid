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

import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.Deserializer00;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.Builder.emptyBuilder;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class Builder00<X> extends AbstractBuilder<X, Deserializer00<X>> {

    public Builder00(final Builder builder) {
        super(builder);
    }

    public static <X> Builder00<X> serializedObjectBuilder00(final GenericType<X> type) {
        final TypeIdentifier typeIdentifier = TypeIdentifier.typeIdentifierFor(type);
        final Builder builder = emptyBuilder(typeIdentifier);
        return new Builder00<>(builder);
    }

    public <A> Builder01<X, A> withField(final String name,
                                         final Class<A> type) {
        return withField(name, genericType(type));
    }

    public <A> Builder01<X, A> withField(final String name,
                                         final GenericType<A> type) {
        this.builder.addDeserializationField(type, name);
        return new Builder01<>(this.builder);
    }
}
