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

package de.quantummaid.mapmaid.shared.types;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Array;
import java.util.List;

import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static java.util.Collections.singletonList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayType implements ResolvedType {
    private final ResolvedType componentType;

    public static ArrayType fromArrayClass(final Class<?> clazz) {
        NotNullValidator.validateNotNull(clazz, "clazz");
        if (!clazz.isArray()) {
            throw new UnsupportedOperationException();
        }
        final ResolvedType componentType = fromClassWithoutGenerics(clazz.getComponentType());
        return arrayType(componentType);
    }

    public static ArrayType arrayType(final ResolvedType componentType) {
        NotNullValidator.validateNotNull(componentType, "componentType");
        return new ArrayType(componentType);
    }

    public ResolvedType componentType() {
        return this.componentType;
    }

    @Override
    public String description() {
        return this.componentType.description() + "[]";
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public List<ResolvedType> typeParameters() {
        return singletonList(this.componentType);
    }

    @Override
    public Class<?> assignableType() {
        return Array.newInstance(this.componentType.assignableType(), 0).getClass();
    }
}
