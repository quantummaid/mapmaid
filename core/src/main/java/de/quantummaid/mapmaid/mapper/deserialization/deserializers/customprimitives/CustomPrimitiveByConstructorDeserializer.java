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
import de.quantummaid.reflectmaid.Executor;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer.createDescription;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1192")
public final class CustomPrimitiveByConstructorDeserializer implements CustomPrimitiveDeserializer {
    private final ResolvedType baseType;
    private final ResolvedConstructor constructor;
    private final Executor executor;

    public static TypeDeserializer createDeserializer(final ResolvedType type,
                                                      final ResolvedConstructor constructor) {
        final int modifiers = constructor.getConstructor().getModifiers();
        if (isAbstract(modifiers)) {
            throw mapMaidException(format("The deserialization constructor %s configured for the custom primitive " +
                    "of type %s must not be abstract", constructor, type.description()));
        }
        final List<ResolvedParameter> parameterTypes = constructor.getParameters();
        if (parameterTypes.size() != 1) {
            throw mapMaidException(format("The deserialization constructor %s configured for the custom primitive " +
                    "of type %s must only have one parameter", constructor, type.description()));
        }
        if (constructor.getConstructor().getDeclaringClass() != type.assignableType()) {
            throw mapMaidException(format("The deserialization constructor %s configured for the custom primitive " +
                    "of type %s must return the custom primitive", constructor, type.description()));
        }

        final ResolvedType baseType = parameterTypes.get(0).getType();
        final Executor executor = constructor.createExecutor();
        return new CustomPrimitiveByConstructorDeserializer(baseType, constructor, executor);
    }

    @Override
    public Class<?> baseType() {
        return this.baseType.assignableType();
    }

    @Override
    public Object deserialize(final Object value) throws Exception {
        return executor.execute(null, List.of(value));
    }

    @Override
    public String description() {
        return createDescription(this, this.constructor.describe());
    }

    public ResolvedConstructor constructor() {
        return this.constructor;
    }
}
