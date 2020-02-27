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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.util.List;

import static de.quantummaid.mapmaid.shared.types.resolver.ResolvedParameter.resolveParameters;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResolvedConstructor {
    private final List<ResolvedParameter> parameters;
    private final Constructor<?> constructor;

    public static List<ResolvedConstructor> resolvePublicConstructors(final ClassType fullType) {
        return stream(fullType.assignableType().getConstructors())
                .filter(constructor -> isPublic(constructor.getModifiers()))
                .map(constructor -> resolveConstructor(constructor, fullType))
                .collect(toList());
    }

    public static ResolvedConstructor resolveConstructor(final Constructor<?> constructor,
                                                                   final ClassType fullType) {
        final List<ResolvedParameter> parameters = resolveParameters(constructor, fullType);
        return new ResolvedConstructor(parameters, constructor);
    }

    public List<ResolvedParameter> parameters() {
        return unmodifiableList(this.parameters);
    }

    public Constructor<?> constructor() {
        return this.constructor;
    }

    public String describe() {
        return this.constructor.toGenericString();
    }
}
