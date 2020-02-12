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

import java.lang.reflect.TypeVariable;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeVariableName {
    private final String name;

    public static TypeVariableName typeVariableName(final String name) {
        NotNullValidator.validateNotNull(name, "name");
        return new TypeVariableName(name);
    }

    public static TypeVariableName typeVariableName(final TypeVariable<?> typeVariable) {
        NotNullValidator.validateNotNull(typeVariable, "typeVariable");
        return typeVariableName(typeVariable.getName());
    }

    public static List<TypeVariableName> typeVariableNamesOf(final Class<?> type) {
        return stream(type.getTypeParameters())
                .map(TypeVariableName::typeVariableName)
                .collect(toList());
    }

    public String name() {
        return this.name;
    }
}
