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
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import static de.quantummaid.mapmaid.shared.types.TypeResolver.resolveType;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResolvedParameter {
    private final ResolvedType type;
    private final Parameter parameter;

    public static List<ResolvedParameter> resolveParameters(final Executable executable,
                                                            final ClassType fullType) {
        return stream(executable.getParameters())
                .map(parameter -> resolveParameter(fullType, parameter))
                .collect(toList());
    }

    public static ResolvedParameter resolveParameter(final ClassType declaringType,
                                                     final Parameter parameter) {
        NotNullValidator.validateNotNull(declaringType, "declaringType");
        NotNullValidator.validateNotNull(parameter, "parameter");

        final Type parameterizedType = parameter.getParameterizedType();
        final ResolvedType resolvedType = resolveType(parameterizedType, declaringType);
        return new ResolvedParameter(resolvedType, parameter);
    }

    public ResolvedType type() {
        return this.type;
    }

    public Parameter parameter() {
        return this.parameter;
    }
}
