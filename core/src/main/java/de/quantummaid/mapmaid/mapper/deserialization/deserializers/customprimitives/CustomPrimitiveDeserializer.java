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

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalPrimitive;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer.castSafely;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public interface CustomPrimitiveDeserializer extends TypeDeserializer {

    static CustomPrimitiveDeserializer constantDeserializer(final Object constant) {
        return new CustomPrimitiveDeserializer() {
            @Override
            public Object deserialize(final Object value) throws Exception {
                return constant;
            }

            @Override
            public String description() {
                return constant.toString();
            }
        };
    }

    static String createDescription(final CustomPrimitiveDeserializer customPrimitiveDeserializer,
                                    final String deserializer) {
        final Class<?> baseType = customPrimitiveDeserializer.baseType();
        return format("as custom primitive based on type '%s' using %s", baseType.getSimpleName(), deserializer);
    }

    @Override
    default List<ResolvedType> requiredTypes() {
        return emptyList();
    }

    default Class<?> baseType() {
        return String.class;
    }

    @Override
    default Class<? extends Universal> universalRequirement() {
        return UniversalPrimitive.class;
    }

    @Override
    default String classification() {
        return "Custom Primitive";
    }

    Object deserialize(Object value) throws Exception;

    @SuppressWarnings("unchecked")
    @Override
    default <T> T deserialize(final Universal input,
                              final ExceptionTracker exceptionTracker,
                              final Injector injector,
                              final DeserializerCallback callback,
                              final CustomPrimitiveMappings customPrimitiveMappings,
                              final ResolvedType resolvedType,
                              final DebugInformation debugInformation) {
        final UniversalPrimitive universalPrimitive = castSafely(input, UniversalPrimitive.class, exceptionTracker, resolvedType, debugInformation);
        try {
            final Class<?> baseType = baseType();
            final Object mapped = customPrimitiveMappings.fromUniversal(universalPrimitive, baseType);
            return (T) deserialize(mapped);
        } catch (final Exception e) {
            final String message = format("Exception calling deserialize(input: %s)", input.toNativeJava());
            exceptionTracker.track(e, message);
            return null;
        }
    }
}
