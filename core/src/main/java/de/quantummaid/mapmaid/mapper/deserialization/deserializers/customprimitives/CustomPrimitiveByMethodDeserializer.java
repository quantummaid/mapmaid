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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializationMethodCallException.customPrimitiveSerializationMethodCallException;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveByMethodDeserializer implements CustomPrimitiveDeserializer {
    private final Class<?> baseType;
    private final Method deserializationMethod;

    public static CustomPrimitiveDeserializer createDeserializer(final Class<?> type,
                                                                 final Method deserializationMethod) {
        final int deserializationMethodModifiers = deserializationMethod.getModifiers();
        if (!Modifier.isPublic(deserializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization method %s configured for the custom primitive of type %s must be public",
                    deserializationMethod, type);
        }
        if (!Modifier.isStatic(deserializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization method %s configured for the custom primitive of type %s must be static",
                    deserializationMethod, type);
        }
        if (Modifier.isAbstract(deserializationMethodModifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization method %s configured for the custom primitive of type %s must not be abstract",
                    deserializationMethod, type);
        }
        final Class<?>[] deserializationMethodParameterTypes = deserializationMethod.getParameterTypes();
        if (deserializationMethodParameterTypes.length != 1 ||
                !deserializationMethodParameterTypes[0].equals(String.class)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization method %s configured for the custom primitive of type %s must " +
                            "accept only one parameter of type String",
                    deserializationMethod, type);
        }
        if (deserializationMethod.getReturnType() != type) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization method %s configured for the custom primitive of type %s must return " +
                            "the custom primitive", deserializationMethod, type);
        }

        final Class<?> baseType = deserializationMethod.getParameterTypes()[0];
        return new CustomPrimitiveByMethodDeserializer(baseType, deserializationMethod);
    }

    @Override
    public Class<?> baseType() {
        return this.baseType;
    }

    @Override
    public Object deserialize(final Object value) throws Exception {
        try {
            return this.deserializationMethod.invoke(null, value);
        } catch (final IllegalAccessException e) {
            throw customPrimitiveSerializationMethodCallException(format(
                    "Unexpected error invoking deserialization method %s for serialized custom primitive %s",
                    this.deserializationMethod, value), e);
        } catch (final InvocationTargetException e) {
            throw handleInvocationTargetException(e, value);
        }
    }

    private Exception handleInvocationTargetException(final InvocationTargetException e, final Object value) {
        final Throwable targetException = e.getTargetException();
        if (targetException instanceof Exception) {
            return (Exception) targetException;
        } else {
            throw customPrimitiveSerializationMethodCallException(format(
                    "Unexpected error invoking deserialization method %s for serialized custom primitive %s",
                    this.deserializationMethod, value), e);
        }
    }
}
