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

package de.quantummaid.mapmaid.builder.detection.customprimitive.deserialization;

import de.quantummaid.mapmaid.builder.detection.customprimitive.CachedReflectionType;
import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByConstructorDeserializer;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.priority.Priority.CONSTRUCTOR;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstructorBasedCustomPrimitiveDeserializationDetector implements CustomPrimitiveDeserializationDetector {
    private final CustomPrimitiveMappings mappings;

    public static CustomPrimitiveDeserializationDetector constructorBased(final CustomPrimitiveMappings mappings) {
        NotNullValidator.validateNotNull(mappings, "mappings");
        return new ConstructorBasedCustomPrimitiveDeserializationDetector(mappings);
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final CachedReflectionType type) {
        return fittingConstructors(type.type()).stream()
                .map(constructor -> CustomPrimitiveByConstructorDeserializer.createDeserializer(type.type(), constructor))
                .map(customPrimitiveDeserializer -> Prioritized.prioritized(customPrimitiveDeserializer, CONSTRUCTOR))
                .collect(toList());
    }

    private List<Constructor<?>> fittingConstructors(final Class<?> type) {
        final Constructor<?>[] constructors = type.getConstructors();
        return stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == 1)
                .filter(constructor -> {
                    final Class<?> parameterType = constructor.getParameterTypes()[0];
                    return this.mappings.isPrimitiveType(parameterType);
                })
                .collect(toList());
    }
}
