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

package de.quantummaid.mapmaid.shared.types.resolver;

import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.shared.types.TypeResolver.resolveType;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResolvedField {
    private final String name;
    private final ResolvedType type;
    private final Field field;

    public static List<ResolvedField> resolvedFields(final ClassType fullType) {
        final Class<?> type = fullType.assignableType();
        return stream(type.getDeclaredFields())
                .map(field -> {
                    final ResolvedType resolved = resolveType(field.getGenericType(), fullType);
                    return resolvedField(field.getName(), resolved, field);
                })
                .collect(toList());
    }

    private static ResolvedField resolvedField(final String name,
                                               final ResolvedType type,
                                               final Field field) {
        validateNotNull(name, "name");
        validateNotNull(type, "type");
        validateNotNull(field, "field");
        return new ResolvedField(name, type, field);
    }

    public String name() {
        return this.name;
    }

    public ResolvedType type() {
        return this.type;
    }

    public Field field() {
        return this.field;
    }

    public boolean isPublic() {
        final int modifiers = this.field.getModifiers();
        return Modifier.isPublic(modifiers);
    }

    public boolean isStatic() {
        final int modifiers = this.field.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    public boolean isTransient() {
        final int modifiers = this.field.getModifiers();
        return Modifier.isTransient(modifiers);
    }

    public String describe() {
        final StringJoiner joiner = new StringJoiner(" ");
        final int modifiers = this.field.getModifiers();
        if (isPublic()) {
            joiner.add("public");
        }
        if (isProtected(modifiers)) {
            joiner.add("protected");
        }
        if (isPrivate(modifiers)) {
            joiner.add("private");
        }
        if (isStatic()) {
            joiner.add("static");
        }
        if (isTransient()) {
            joiner.add("transient");
        }
        if (isFinal(modifiers)) {
            joiner.add("final");
        }

        final String type = this.type.simpleDescription();
        joiner.add(type);

        joiner.add(this.name);
        return joiner.toString();
    }
}
