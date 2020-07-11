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

package de.quantummaid.mapmaid.builder.builder;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.injection.FixedInjector;
import de.quantummaid.mapmaid.builder.injection.InjectionDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.mapmaid.builder.injection.FixedInjectionDeserializer.diDeserializer;
import static de.quantummaid.mapmaid.builder.injection.InjectionDeserializer.injectionDeserializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface InjectingBuilder {

    default MapMaidBuilder injecting(final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return injecting(genericType);
    }

    default MapMaidBuilder injecting(final GenericType<?> genericType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(genericType);
        return injecting(typeIdentifier);
    }

    default MapMaidBuilder injecting(final TypeIdentifier typeIdentifier) {
        final InjectionDeserializer deserializer = injectionDeserializer(typeIdentifier);
        return injecting(typeIdentifier, deserializer);
    }

    default <T> MapMaidBuilder injecting(final Class<T> type, final FixedInjector<T> injector) {
        final GenericType<T> genericType = genericType(type);
        return injecting(genericType, injector);
    }

    default <T> MapMaidBuilder injecting(final GenericType<T> genericType, final FixedInjector<T> injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(genericType);
        return injecting(typeIdentifier, injector);
    }

    default MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final FixedInjector<?> injector) {
        final TypeDeserializer deserializer = diDeserializer(injector);
        return injecting(typeIdentifier, deserializer);
    }

    MapMaidBuilder injecting(TypeIdentifier typeIdentifier, TypeDeserializer deserializer);
}
