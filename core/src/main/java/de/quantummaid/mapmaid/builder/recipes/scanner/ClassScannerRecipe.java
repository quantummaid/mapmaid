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

package de.quantummaid.mapmaid.builder.recipes.scanner;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassScannerRecipe implements Recipe {
    private static final List<String> OBJECT_METHODS = stream(Object.class.getMethods())
            .map(Method::getName)
            .collect(toList());

    private final ReflectMaid reflectMaid;
    private final Collection<Class<?>> classes;
    private final Collection<Class<?>> serializationExclusions;
    private final Collection<Class<?>> deserializationExclusions;

    public static ClassScannerRecipe addAllReferencedClassesIn(final ReflectMaid reflectMaid,
                                                               final Class<?>... classes) {
        validateNotNull(classes, "classes");
        validateNotNull(reflectMaid, "reflectMaid");
        return new ClassScannerRecipe(reflectMaid, asList(classes), emptyList(), emptyList());
    }

    @Override
    public void cook(final MapMaidBuilder builder) {
        this.classes.forEach(clazz -> addReferencesIn(clazz, builder));
    }

    private void addReferencesIn(final Class<?> clazz,
                                 final MapMaidBuilder builder) {
        final ClassType fullType = (ClassType) reflectMaid.resolve(clazz);
        final List<ResolvedMethod> methods = fullType.methods();
        for (final ResolvedMethod method : methods) {
            if (!method.isPublic()) {
                continue;
            }
            if (!OBJECT_METHODS.contains(method.getMethod().getName())) {
                method.getParameters().stream()
                        .map(ResolvedParameter::getType)
                        .filter(type -> !this.deserializationExclusions.contains(type.assignableType()))
                        .map(GenericType::fromResolvedType)
                        .forEach(type -> builder.withType(
                                type, deserialization(), format(
                                        "because parameter type of method %s", method.describe())));
                method.returnType().ifPresent(type -> {
                    if (this.serializationExclusions.contains(type.assignableType())) {
                        return;
                    }
                    final GenericType<?> genericType = GenericType.fromResolvedType(type);
                    builder.withType(
                            genericType,
                            serialization(),
                            format("because return type of method %s", method.describe()));
                });
            }
        }
    }
}
