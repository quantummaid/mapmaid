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

import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.recipes.di.DiDeserializer.diDeserializer;
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

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        this.types.stream()
                .map(type -> {
                    final ResolvedType resolvedType = ResolvedType.resolvedType(type);
                    final DependencyInjector<?> dependencyInjector = this.injector.specialzedFor(type);
                    return GeneralDefinition.generalDefinition(resolvedType, DiSerializer.diSerializer(), diDeserializer(dependencyInjector));
                })
                .forEach(mapMaidBuilder::withManuallyAddedDefinition);
    }
}
