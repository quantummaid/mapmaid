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

package de.quantummaid.mapmaid.shared.types;

import java.lang.reflect.Type;
import java.util.List;

import static de.quantummaid.mapmaid.shared.types.ArrayType.fromArrayClass;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;

public interface ResolvedType {

    static ResolvedType resolvedType(final Class<?> type) {
        if (type.isArray()) {
            return fromArrayClass(type);
        } else {
            return fromClassWithoutGenerics(type);
        }
    }

    static ResolvedType resolveType(final Type type, final ClassType fullType) {
        return TypeResolver.resolveType(type, fullType);
    }

    Class<?> assignableType();

    List<ResolvedType> typeParameters();

    boolean isAbstract();

    boolean isInterface();

    boolean isWildcard();

    String description();

    default boolean isInstantiatable() {
        if (isAbstract()) {
            return false;
        }
        if (isInterface()) {
            return false;
        }
        if (isWildcard()) {
            return false;
        }
        return typeParameters().stream()
                .allMatch(ResolvedType::isInstantiatable);
    }
}
