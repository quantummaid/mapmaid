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
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByConstructorDeserializer;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

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
    public Optional<CustomPrimitiveDeserializer> detect(final CachedReflectionType type) {
        return fittingConstructor(type.type())
                .map(constructor -> CustomPrimitiveByConstructorDeserializer.createDeserializer(type.type(), constructor));
    }

    private Optional<Constructor<?>> fittingConstructor(final Class<?> type) {
        final Constructor<?>[] constructors = type.getConstructors();
        for (final Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() != 1) {
                continue;
            }
            final Class<?> parameterType = constructor.getParameterTypes()[0];
            if (this.mappings.isPrimitiveType(parameterType)) {
                return of(constructor);
            }
        }
        return empty();
    }
}
