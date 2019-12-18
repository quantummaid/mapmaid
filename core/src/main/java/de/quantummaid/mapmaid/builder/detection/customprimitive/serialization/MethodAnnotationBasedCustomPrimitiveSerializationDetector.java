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

package de.quantummaid.mapmaid.builder.detection.customprimitive.serialization;

import de.quantummaid.mapmaid.builder.conventional.annotations.MapMaidPrimitiveSerializer;
import de.quantummaid.mapmaid.builder.detection.customprimitive.CachedReflectionType;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.customprimitive.IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodAnnotationBasedCustomPrimitiveSerializationDetector implements CustomPrimitiveSerializationDetector {
    private final Class<? extends Annotation> annotation;

    public static CustomPrimitiveSerializationDetector annotationBasedSerializer(final Class<? extends Annotation> annotation) {
        NotNullValidator.validateNotNull(annotation, "annotation");
        return new MethodAnnotationBasedCustomPrimitiveSerializationDetector(annotation);
    }

    @Override
    public Optional<CustomPrimitiveSerializer> detect(final CachedReflectionType type) {
        final Method[] typeMethods = type.methods();
        final List<Method> serializerMethods = stream(typeMethods)
                .filter(method -> method.getAnnotationsByType(MapMaidPrimitiveSerializer.class).length > 0)
                .collect(toList());

        if (serializerMethods.isEmpty()) {
            return empty();
        }
        if (serializerMethods.size() != 1) {
            throw incompatibleCustomPrimitiveException(
                    "When using %s annotation, it needs" +
                            "be used exactly on one method. Found %s annotations on type %s",
                    this.annotation.getName(),
                    serializerMethods.size(),
                    type
            );
        }
        final Method deserializationMethod = serializerMethods.get(0);
        return of(MethodCustomPrimitiveSerializer.createSerializer(type.type(), deserializationMethod));
    }
}
