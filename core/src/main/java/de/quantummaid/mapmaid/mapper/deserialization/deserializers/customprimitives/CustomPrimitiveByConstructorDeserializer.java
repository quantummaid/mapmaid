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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializationMethodCallException.customPrimitiveSerializationMethodCallException;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isPublic;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveByConstructorDeserializer implements CustomPrimitiveDeserializer {
    private final Class<?> baseType;
    private final Constructor<?> constructor;

    public static CustomPrimitiveDeserializer createDeserializer(final Class<?> type,
                                                                 final Constructor<?> constructor) {
        final int modifiers = constructor.getModifiers();
        if (!isPublic(modifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must be public",
                    constructor, type);
        }
        if (isAbstract(modifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must not be abstract",
                    constructor, type);
        }
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must " +
                            "only have one parameter",
                    constructor, type);
        }
        if (constructor.getDeclaringClass() != type) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must return " +
                            "the custom primitive", constructor, type);
        }

        final Class<?> baseType = constructor.getParameterTypes()[0];
        return new CustomPrimitiveByConstructorDeserializer(baseType, constructor);
    }

    @Override
    public Class<?> baseType() {
        return this.baseType;
    }

    @Override
    public Object deserialize(final Object value) throws Exception {
        try {
            return this.constructor.newInstance(value);
        } catch (final IllegalAccessException | IllegalArgumentException e) {
            throw customPrimitiveSerializationMethodCallException(
                    format("Unexpected error invoking deserialization constructor %s for serialized custom primitive %s",
                            this.constructor, value), e);
        } catch (final InvocationTargetException e) {
            throw handleInvocationTargetException(e, (String) value);
        }
    }

    private Exception handleInvocationTargetException(final InvocationTargetException e, final String value) {
        final Throwable targetException = e.getTargetException();
        if (targetException instanceof Exception) {
            return (Exception) targetException;
        } else {
            throw customPrimitiveSerializationMethodCallException(
                    format("Unexpected error invoking deserialization constructor %s for serialized custom primitive %s",
                            this.constructor, value), e);
        }
    }

    @Override
    public String classification() {
        return "Custom Primitive";
    }
}
