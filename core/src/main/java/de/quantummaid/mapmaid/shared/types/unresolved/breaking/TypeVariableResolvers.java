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

package de.quantummaid.mapmaid.shared.types.unresolved.breaking;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeVariableResolvers {
    private final List<String> relevantTypeVariables;
    private final Map<String, Optional<TypeVariableResolver>> typeVariableResolvers;

    public static TypeVariableResolvers resolversFor(final Class<?> type) {
        final TypeVariable<? extends Class<?>>[] typeParameters = type.getTypeParameters();
        final List<String> names = stream(typeParameters)
                .map(TypeVariable::getName)
                .collect(toList());
        final Map<String, Optional<TypeVariableResolver>> resolvers = new HashMap<>(typeParameters.length);
        stream(typeParameters).forEach(typeVariable -> {
            final String name = typeVariable.getName();
            final Optional<TypeVariableResolver> typeVariableResolver = resolverForVariable(typeVariable, type);
            resolvers.put(name, typeVariableResolver);
        });
        return resolvers(names, resolvers);
    }

    private static Optional<TypeVariableResolver> resolverForVariable(final TypeVariable<?> typeVariable, final Class<?> type) {
        final Optional<TypeVariableResolver> fieldResolver = stream(type.getFields())
                .filter(field -> typeVariable.equals(field.getGenericType()))
                .map(FieldTypeVariableResolver::fieldTypeVariableResolver)
                .findFirst();
        return fieldResolver;
    }

    private static Optional<TypeVariableResolver> resolverForMethod(final TypeVariable<?> typeVariable, final Method method) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            if (!parameter.getParameterizedType().equals(typeVariable)) {
                continue;
            }
            return of(MethodParameterVariableResolver.methodParameterVariableResolver(method.getName(), method.getParameterTypes(), i));
        }
        return empty();
    }

    private static TypeVariableResolvers resolvers(final List<String> relevantTypeVariables,
                                                   final Map<String, Optional<TypeVariableResolver>> resolverMap) {
        validateNotNull(relevantTypeVariables, "relevantTypeVariables");
        validateNotNull(resolverMap, "resolverMap");
        return new TypeVariableResolvers(relevantTypeVariables, resolverMap);
    }

    public List<ResolvedType> resolve(final Object object) {
        return this.relevantTypeVariables.stream()
                .map(this.typeVariableResolvers::get)
                .map(resolver -> resolver.orElseThrow(() -> {
                    return new UnsupportedOperationException(format("Unable to resolve type variables based on object '%s'", object));
                }))
                .map(resolver -> resolver.resolve(object))
                .collect(toList());
    }
}
