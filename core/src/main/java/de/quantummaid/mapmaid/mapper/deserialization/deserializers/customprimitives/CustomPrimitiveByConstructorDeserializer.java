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

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer.createDescription;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.IncompatibleCustomPrimitiveException.incompatibleCustomPrimitiveException;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializationMethodCallException.customPrimitiveSerializationMethodCallException;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveByConstructorDeserializer implements CustomPrimitiveDeserializer {
    private final ResolvedType baseType;
    private final ResolvedConstructor constructor;

    public static TypeDeserializer createDeserializer(final ResolvedType type,
                                                      final ResolvedConstructor constructor) {
        final int modifiers = constructor.constructor().getModifiers();
        if (isAbstract(modifiers)) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must not be abstract",
                    constructor, type.description());
        }
        final List<ResolvedParameter> parameterTypes = constructor.parameters();
        if (parameterTypes.size() != 1) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must " +
                            "only have one parameter",
                    constructor, type.description());
        }
        if (constructor.constructor().getDeclaringClass() != type.assignableType()) {
            throw incompatibleCustomPrimitiveException(
                    "The deserialization constructor %s configured for the custom primitive of type %s must return " +
                            "the custom primitive", constructor, type.description());
        }

        final ResolvedType baseType = parameterTypes.get(0).type();
        return new CustomPrimitiveByConstructorDeserializer(baseType, constructor);
    }

    @Override
    public Class<?> baseType() {
        return this.baseType.assignableType();
    }

    @Override
    public Object deserialize(final Object value) throws Exception {
        try {
            return this.constructor.constructor().newInstance(value);
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
    public String description() {
        return createDescription(this, this.constructor.describe());
    }

    public ResolvedConstructor constructor() {
        return this.constructor;
    }
}
