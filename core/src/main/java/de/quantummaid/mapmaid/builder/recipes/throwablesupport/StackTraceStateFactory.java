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

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.serializationOnlyResult;
import static de.quantummaid.mapmaid.builder.recipes.throwablesupport.StackTraceSerializer.stackTraceSerializer;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StackTraceStateFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final TypeIdentifier targetType;
    private final int maxStackFrameCount;

    public static StackTraceStateFactory stackTraceStateFactory(final ReflectMaid reflectMaid,
                                                                final int maxStackFrameCount) {
        final ResolvedType resolvedType = reflectMaid.resolve(StackTraceElement[].class);
        final TypeIdentifier stackTraceType = typeIdentifierFor(resolvedType);
        return new StackTraceStateFactory(stackTraceType, maxStackFrameCount);
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        return targetType.equals(type);
    }

    @Override
    public void create(final TypeIdentifier type,
                       final Context<MapMaidTypeScannerResult> context) {
        final StackTraceSerializer serializer = stackTraceSerializer(targetType, maxStackFrameCount);
        context.setManuallyConfiguredResult(result(serializationOnlyResult(serializer), type));
    }
}
