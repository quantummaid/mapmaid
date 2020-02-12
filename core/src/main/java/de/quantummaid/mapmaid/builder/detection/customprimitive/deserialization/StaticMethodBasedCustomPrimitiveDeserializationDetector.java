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
import de.quantummaid.mapmaid.builder.detection.priority.Priority;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaticMethodBasedCustomPrimitiveDeserializationDetector implements CustomPrimitiveDeserializationDetector {
    private static final Pattern MATCH_ALL = compile(".*");

    private final CustomPrimitiveMappings mappings;
    private final Pattern deserializationMethodName;

    public static CustomPrimitiveDeserializationDetector staticMethodBased(final CustomPrimitiveMappings mappings) {
        return new StaticMethodBasedCustomPrimitiveDeserializationDetector(mappings, MATCH_ALL);
    }

    public static CustomPrimitiveDeserializationDetector staticMethodBased(final CustomPrimitiveMappings mappings, final String pattern) {
        NotNullValidator.validateNotNull(mappings, "mappings");
        NotNullValidator.validateNotNull(pattern, "pattern");
        return new StaticMethodBasedCustomPrimitiveDeserializationDetector(mappings, compile(pattern));
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final CachedReflectionType type) {
        return findDeserializerMethod(type).stream()
                .map(method -> CustomPrimitiveByMethodDeserializer.createDeserializer(type.type(), method))
                .map(customPrimitiveDeserializer -> Prioritized.prioritized(customPrimitiveDeserializer, Priority.FACTORY))
                .collect(toList());
    }

    private List<Method> findDeserializerMethod(final CachedReflectionType type) {
        return stream(type.methods())
                .filter(method -> isStatic(method.getModifiers()))
                .filter(method -> isPublic(method.getModifiers()))
                .filter(method -> method.getReturnType().equals(type.type()))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> this.mappings.isPrimitiveType(method.getParameterTypes()[0]))
                .collect(toList());
    }

}
