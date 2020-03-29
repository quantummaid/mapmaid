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

package de.quantummaid.mapmaid.builder.detection.serializedobject.fields;

import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.SerializationFieldQuery;
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.GetterFieldQuery.getterFieldQuery;
import static java.lang.String.valueOf;
import static java.lang.Void.TYPE;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyList;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetterFieldDetector implements FieldDetector {

    public static FieldDetector getterFieldDetector() {
        return new GetterFieldDetector();
    }

    @Override
    public List<SerializationField> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        return ((ClassType) type).methods().stream()
                .filter(resolvedMethod -> resolvedMethod.method().getName().startsWith("get"))
                .filter(resolvedMethod -> !isStatic(resolvedMethod.method().getModifiers()))
                .filter(resolvedMethod -> resolvedMethod.method().getReturnType() != TYPE)
                .filter(resolvedMethod -> resolvedMethod.parameters().isEmpty())
                .map(resolvedMethod -> {
                    final ResolvedType resolvedType = resolvedMethod.returnType().orElseThrow();
                    final Method method = resolvedMethod.method();
                    final String name = extractGetterFieldName(method.getName());
                    final SerializationFieldQuery query = getterFieldQuery(method);
                    return SerializationField.serializationField(resolvedType, name, query);
                })
                .collect(toList());
    }

    private static String extractGetterFieldName(final String methodName) {
        final String withoutGet = methodName.substring(3);
        final String firstCharacter = valueOf(withoutGet.charAt(0));
        final String lowercaseFirstCharacter = firstCharacter.toLowerCase(US);
        return lowercaseFirstCharacter + withoutGet.substring(1);
    }
}
