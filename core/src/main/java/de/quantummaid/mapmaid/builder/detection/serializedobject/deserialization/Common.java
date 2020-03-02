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

package de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization;

import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedParameter;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

public final class Common {

    private Common() {
    }

    public static List<ResolvedMethod> detectDeserializerMethods(final ClassType type) {
        return type.publicMethods().stream()
                .filter(resolvedMethod -> isStatic(resolvedMethod.method().getModifiers()))
                .filter(resolvedMethod -> {
                    final Optional<ResolvedType> resolvedType = resolvedMethod.returnType();
                    final Optional<Boolean> optional = resolvedType.map(type::equals);
                    return optional.orElse(false);
                })
                .filter(resolvedMethod -> resolvedMethod.parameters().size() > 0)
                .collect(toList());
    }

    public static <T> Optional<T> findMatchingMethod(final List<ResolvedType> serializedFields,
                                                     final List<T> candidates,
                                                     final Function<T, List<ResolvedParameter>> parameterQuery) {
        if (serializedFields.isEmpty()) {
            return empty();
        }

        T match = null;
        if (candidates.size() == 1) {
            match = candidates.get(0);
        } else {
            final Optional<T> firstCompatibleDeserializerConstructor = candidates.stream()
                    .filter(candidate -> isMethodCompatibleWithFields(parameterQuery.apply(candidate), serializedFields))
                    .findFirst();
            if (firstCompatibleDeserializerConstructor.isPresent()) {
                match = firstCompatibleDeserializerConstructor.get();
            }
        }

        if (match == null) {
            return empty();
        }

        if (isMostLikelyACustomPrimitive(serializedFields, parameterQuery.apply(match))) {
            return empty();
        }

        return of(match);
    }

    static boolean isMethodCompatibleWithFields(final List<ResolvedParameter> parameters, final List<ResolvedType> fields) {
        final List<ResolvedType> parameterTypes = parameters.stream()
                .map(ResolvedParameter::type)
                .collect(toList());
        if (fields.size() != parameterTypes.size()) {
            return false;
        }

        for (final ResolvedType serializedField : fields) {
            final boolean present = parameterTypes.contains(serializedField);
            if (!present) {
                return false;
            }
        }
        return true;
    }

    private static boolean isMostLikelyACustomPrimitive(final List<ResolvedType> fields, final List<ResolvedParameter> parameters) {
        final boolean isMostLikelyACustomPrimitive = fields.isEmpty() &&
                parameters.size() == 1 &&
                parameters.get(0).parameter().getType() == String.class;
        return isMostLikelyACustomPrimitive;
    }
}
