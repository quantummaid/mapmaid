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
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.serializedobject.fields.GetterFieldQuery.getterFieldQuery;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField.serializationField;
import static java.lang.Void.TYPE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BooleanGetterFieldDetector implements FieldDetector {

    public static FieldDetector booleanGetterFieldDetector() {
        return new BooleanGetterFieldDetector();
    }

    @Override
    public List<SerializationField> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        return type.methods().stream()
                .filter(resolvedMethod -> resolvedMethod.name().startsWith("is"))
                .filter(resolvedMethod -> !resolvedMethod.isStatic())
                .filter(resolvedMethod -> resolvedMethod.getMethod().getReturnType() != TYPE)
                .filter(resolvedMethod -> resolvedMethod.getParameters().isEmpty())
                .map(resolvedMethod -> {
                    final ResolvedType resolvedType = resolvedMethod.returnType().orElseThrow();
                    final String name = resolvedMethod.name();
                    final SerializationFieldQuery query = getterFieldQuery(resolvedMethod);
                    return serializationField(resolvedType, name, query);
                })
                .collect(toList());
    }
}
