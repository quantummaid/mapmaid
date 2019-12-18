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

import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodParameterVariableResolver implements TypeVariableResolver {
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final int parameterIndex;

    public static MethodParameterVariableResolver methodParameterVariableResolver(final String methodName,
                                                                                  final Class<?>[] parameterTypes,
                                                                                  final int parameterIndex) {
        return new MethodParameterVariableResolver(methodName, parameterTypes, parameterIndex);
    }

    @Override
    public ResolvedType resolve(final Object object) {
        try {
            final Method method = object.getClass().getMethod(this.methodName, this.parameterTypes);
            final Parameter parameter = method.getParameters()[this.parameterIndex];
            final Class<?> type = parameter.getType();
            return fromClassWithoutGenerics(type);
            //final Type parameterizedType = parameter.getParameterizedType();
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
