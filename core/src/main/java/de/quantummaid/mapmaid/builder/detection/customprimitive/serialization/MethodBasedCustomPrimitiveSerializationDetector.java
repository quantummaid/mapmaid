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

package de.quantummaid.mapmaid.builder.detection.customprimitive.serialization;

import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer.createSerializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.reflect.Modifier.*;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodBasedCustomPrimitiveSerializationDetector implements CustomPrimitiveSerializationDetector {
    private final CustomPrimitiveMappings mappings;

    public static CustomPrimitiveSerializationDetector methodBased(final CustomPrimitiveMappings mappings) {
        validateNotNull(mappings, "mappings");
        return new MethodBasedCustomPrimitiveSerializationDetector(mappings);
    }

    @Override
    public List<TypeSerializer> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        return type.methods().stream()
                .filter(method -> !isStatic(method.getMethod().getModifiers()))
                .filter(method -> method.returnType().isPresent())
                .filter(method -> mappings.isPrimitiveType(method.returnType().get().assignableType()))
                .filter(method -> method.getParameters().isEmpty())
                .map(method -> createSerializer(type, method))
                .collect(toList());
    }
}
