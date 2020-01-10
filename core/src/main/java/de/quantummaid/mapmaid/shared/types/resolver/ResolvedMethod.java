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

package de.quantummaid.mapmaid.shared.types.resolver;

import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.UnresolvableTypeVariableException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.types.TypeResolver.resolveType;
import static de.quantummaid.mapmaid.shared.types.resolver.ResolvedParameter.resolveParameters;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.*;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResolvedMethod {
    private final ResolvedType returnType;
    private final List<ResolvedParameter> parameters;
    private final Method method;

    @SuppressWarnings("unchecked")
    public static List<ResolvedMethod> resolvePublicMethodsWithResolvableTypeVariables(final ClassType fullType) {
        final Class<?> type = fullType.assignableType();
        final Method[] declaredMethods = type.getDeclaredMethods();
        return stream(declaredMethods)
                .filter(method -> isPublic(method.getModifiers()))
                .map(method -> {
                    try {
                        return of(resolveMethod(method, fullType));
                    } catch (final UnresolvableTypeVariableException e) {
                        return empty();
                    }
                })
                .map(o -> (Optional<ResolvedMethod>) o)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public static ResolvedMethod resolveMethod(final Method method, final ClassType context) {
        final Type genericReturnType = method.getGenericReturnType();
        final ResolvedType returnType;
        if (genericReturnType != Void.TYPE) {
            returnType = resolveType(genericReturnType, context);
        } else {
            returnType = null;
        }
        final List<ResolvedParameter> parameters = resolveParameters(method, context);
        return new ResolvedMethod(returnType, parameters, method);
    }

    public Optional<ResolvedType> returnType() {
        return ofNullable(this.returnType);
    }

    public boolean hasParameters(final List<ResolvedType> parameters) {
        if (parameters.size() != this.parameters.size()) {
            return false;
        }
        for (int i = 0; i < parameters.size(); ++i) {
            if (!parameters.get(i).equals(this.parameters.get(i).type())) {
                return false;
            }
        }
        return true;
    }

    public List<ResolvedParameter> parameters() {
        return unmodifiableList(this.parameters);
    }

    public Method method() {
        return this.method;
    }
}
