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
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer.createDescription;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1192")
public final class CustomPrimitiveByMethodDeserializer implements CustomPrimitiveDeserializer {
    private final ResolvedType baseType;
    private final ResolvedMethod deserializationMethod;

    public static TypeDeserializer createDeserializer(final ResolvedType type,
                                                      final ResolvedMethod deserializationMethod) {
        final int deserializationMethodModifiers = deserializationMethod.getMethod().getModifiers();
        if (!Modifier.isStatic(deserializationMethodModifiers)) {
            throw mapMaidException(format("The deserialization method %s configured for the custom primitive " +
                    "of type %s must be static", deserializationMethod.describe(), type.description()));
        }
        if (Modifier.isAbstract(deserializationMethodModifiers)) {
            throw mapMaidException(format("The deserialization method %s configured for the custom primitive " +
                    "of type %s must not be abstract", deserializationMethod.describe(), type.description()));
        }
        final List<ResolvedParameter> parameters = deserializationMethod.getParameters();
        if (parameters.size() != 1) {
            throw mapMaidException(format("The deserialization method %s configured for the custom primitive " +
                    "of type %s must accept only one parameter", deserializationMethod.describe(), type.description()));
        }
        final boolean correctReturnType = deserializationMethod.returnType()
                .map(type::equals)
                .orElse(false);
        if (!correctReturnType) {
            throw mapMaidException(format("The deserialization method %s configured for the custom primitive " + // NOSONAR
                    "of type %s must return the custom primitive", deserializationMethod.describe(), type.description()));
        }

        final ResolvedType baseType = parameters.get(0).getType();
        return new CustomPrimitiveByMethodDeserializer(baseType, deserializationMethod);
    }

    @Override
    public Class<?> baseType() {
        return this.baseType.assignableType();
    }

    @Override
    public Object deserialize(final Object value) throws Exception {
        try {
            return this.deserializationMethod.getMethod().invoke(null, value);
        } catch (final IllegalAccessException e) {
            throw mapMaidException(format(
                    "Unexpected error invoking deserialization method %s for serialized custom primitive %s",
                    this.deserializationMethod, value), e);
        } catch (final InvocationTargetException e) {
            throw handleInvocationTargetException(e, value);
        }
    }

    public ResolvedMethod method() {
        return this.deserializationMethod;
    }

    @Override
    public String description() {
        return createDescription(this, this.deserializationMethod.describe());
    }

    private Exception handleInvocationTargetException(final InvocationTargetException e, final Object value) {
        final Throwable targetException = e.getTargetException();
        if (targetException instanceof Exception) {
            return (Exception) targetException;
        } else {
            throw mapMaidException(format("Unexpected error invoking deserialization method %s for serialized custom primitive %s",
                    this.deserializationMethod, value), e);
        }
    }
}
