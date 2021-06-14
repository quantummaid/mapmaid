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

package de.quantummaid.mapmaid.builder.recipes.throwablesupport;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.StackTraceStateFactory.stackTraceStateFactory;
import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.ThrowableSerializer.throwableSerializer;
import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.ThrowableStateFactory.throwableStateFactory;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowableSupport implements Recipe {
    private final int maxStackFrameCount;

    public static ThrowableSupport throwableSupport(final int maxStackFrameCount) {
        return new ThrowableSupport(maxStackFrameCount);
    }

    @Override
    public void apply(final MapMaidBuilder builder) {
        final ReflectMaid reflectMaid = builder.reflectMaid();
        final TypeIdentifier throwableType = resolve(reflectMaid, Throwable.class);
        final TypeIdentifier stackTraceType = resolve(reflectMaid, StackTraceElement[].class);
        final ThrowableSerializer serializer = throwableSerializer(throwableType, stackTraceType);
        builder.withAdvancedSettings(advancedBuilder -> {
            advancedBuilder.withSuperTypeSerializer(throwableType, serializer);
            advancedBuilder.withStateFactory(throwableStateFactory(throwableType, serializer));
            advancedBuilder.withStateFactory(stackTraceStateFactory(reflectMaid, maxStackFrameCount));
        });
    }

    private static TypeIdentifier resolve(final ReflectMaid reflectMaid, final Class<?> type) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        return typeIdentifierFor(resolvedType);
    }
}
