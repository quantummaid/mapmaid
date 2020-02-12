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

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeToAdd {
    private final ResolvedType type;
    private final List<Method> reasonsForSerialization;
    private final List<Method> reasonsForDeserialization;

    public static TypeToAdd typeToAdd(final ResolvedType type) {
        validateNotNull(type, "type");
        return new TypeToAdd(type, new ArrayList<>(3), new ArrayList<>(3));
    }

    public void addReasonForSerialization(final Method method) {
        this.reasonsForSerialization.add(method);
    }

    public void addReasonForDeserialization(final Method method) {
        this.reasonsForDeserialization.add(method);
    }
}
