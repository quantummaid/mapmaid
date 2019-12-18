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

package de.quantummaid.mapmaid.builder.recipes.injection;

import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.recipes.injection.InjectionDeserializer.injectionDeserializer;
import static de.quantummaid.mapmaid.builder.recipes.injection.InjectionSerializer.injectionSerializer;
import static de.quantummaid.mapmaid.shared.types.ResolvedType.resolvedType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectionRecipe implements Recipe {
    private final ResolvedType resolvedType;

    public static Recipe injectionOnly(final Class<?> type) {
        return new InjectionRecipe(resolvedType(type));
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        mapMaidBuilder.withManuallyAddedDefinition(GeneralDefinition.generalDefinition(
                this.resolvedType,
                injectionSerializer(),
                injectionDeserializer()));
    }
}
