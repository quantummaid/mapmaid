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

package de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodCustomPrimitiveSerializer implements CustomPrimitiveSerializer {
    private final ResolvedType baseType;
    private final ResolvedMethod serializationMethod;

    public static CustomPrimitiveSerializer createSerializer(final ResolvedType type,
                                                             final ResolvedMethod serializationMethod) {
        final int serializationMethodModifiers = serializationMethod.method().getModifiers();
        if (!Modifier.isPublic(serializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The serialization method %s configured for the custom primitive of type %s must be public",
                    serializationMethod,
                    type.description()
            );
        }
        if (Modifier.isStatic(serializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The serialization method %s configured for the custom primitive of type %s must not be static",
                    serializationMethod,
                    type.description()
            );
        }
        if (Modifier.isAbstract(serializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The serialization method %s configured for the custom primitive of type %s must not be abstract",
                    serializationMethod,
                    type.description()
            );
        }
        if (serializationMethod.parameters().size() > 0) {
            throw incompatibleCustomPrimitiveException(
                    "The serialization method %s configured for the custom primitive of type %s must " +
                            "not accept any parameters",
                    serializationMethod,
                    type.description()
            );
        }
        final ResolvedType baseType = serializationMethod.returnType().get();
        return new MethodCustomPrimitiveSerializer(baseType, serializationMethod);
    }

    @Override
    public Object serialize(final Object object) {
        try {
            return this.serializationMethod.method().invoke(object);
        } catch (final IllegalAccessException e) {
            throw CustomPrimitiveSerializationMethodCallException.customPrimitiveSerializationMethodCallException(format(
                    "This should never happen. Called serialization method %s for custom type %s on instance %s",
                    this.serializationMethod, object.getClass(), object), e);
        } catch (final InvocationTargetException e) {
            throw CustomPrimitiveSerializationMethodCallException.customPrimitiveSerializationMethodCallException(format(
                    "Got exception calling serialization method %s for custom type %s on instance %s",
                    this.serializationMethod, object.getClass(), object), e);
        }
    }

    @Override
    public Class<?> baseType() {
        return this.baseType.assignableType();
    }

    public Method method() {
        return this.serializationMethod.method();
    }

    @Override
    public String description() {
        return format("as custom primitive using %s", this.serializationMethod.describe());
    }
}
