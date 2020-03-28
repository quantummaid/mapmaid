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

package de.quantummaid.mapmaid.builder.recipes.di;

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.recipes.injection.FixedInjector;
import de.quantummaid.mapmaid.builder.recipes.injection.InjectionSerializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.duplexType;
import static de.quantummaid.mapmaid.builder.recipes.injection.FixedInjectionDeserializer.diDeserializer;
import static java.util.Arrays.asList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiRecipe implements Recipe {
    private final GeneralDependencyInjector injector;
    private final List<Class<?>> types;

    public static Recipe toUseDependencyInjectionWith(final GeneralDependencyInjector injector,
                                                      final Class<?>... types) {
        return new DiRecipe(injector, asList(types));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder) {
        this.types.forEach(type -> {
            final GenericType<Object> genericType = genericType((Class<Object>) type);
            final TypeIdentifier typeIdentifier = TypeIdentifier.typeIdentifierFor(genericType);
            final FixedInjector<?> fixedInjector = this.injector.specializedFor(type);
            final TypeSerializer serializer = InjectionSerializer.injectionSerializer(typeIdentifier);
            final TypeDeserializer deserializer = diDeserializer(fixedInjector);
            final DuplexType<Object> duplexType = duplexType(genericType, serializer, deserializer);
            mapMaidBuilder.serializingAndDeserializing(duplexType);
        });
    }
}
