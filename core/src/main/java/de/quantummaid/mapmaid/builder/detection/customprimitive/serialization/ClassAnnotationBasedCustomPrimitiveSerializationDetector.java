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

import de.quantummaid.mapmaid.builder.detection.customprimitive.CachedReflectionType;
import de.quantummaid.mapmaid.builder.detection.customprimitive.IncompatibleCustomPrimitiveException;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassAnnotationBasedCustomPrimitiveSerializationDetector<T extends Annotation>
        implements CustomPrimitiveSerializationDetector {
    private final Class<T> annotationType;
    private final Function<T, String> methodName;

    public static <T extends Annotation> CustomPrimitiveSerializationDetector classAnnotationBasedSerializer(
            final Class<T> annotationType,
            final Function<T, String> methodName) {
        NotNullValidator.validateNotNull(annotationType, "annotationType");
        NotNullValidator.validateNotNull(methodName, "methodName");
        return new ClassAnnotationBasedCustomPrimitiveSerializationDetector<>(annotationType, methodName);
    }

    @Override
    public Optional<CustomPrimitiveSerializer> detect(final CachedReflectionType cachedReflectionType) {
        final Class<?> type = cachedReflectionType.type();
        final T[] annotations = type.getAnnotationsByType(this.annotationType);
        if (annotations.length == 1) {
            final T annotation = annotations[0];
            return this.findSerializerMethod(type, annotation)
                    .map(method -> MethodCustomPrimitiveSerializer.createSerializer(type, method));
        }
        return Optional.empty();
    }

    private Optional<Method> findSerializerMethod(final Class<?> type, final T annotation) {
        return readMethodName(annotation).map(methodName -> {
            try {
                return type.getMethod(methodName);
            } catch (final NoSuchMethodException e) {
                throw IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException(e,
                        "Could not find the serializer method with name %s in type %s mentioned in annotation %s",
                        methodName,
                        type,
                        annotation
                );
            }
        });
    }

    private Optional<String> readMethodName(final T annotation) {
        return ofNullable(this.methodName.apply(annotation));
    }
}
