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

package de.quantummaid.mapmaid.exceptions;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import de.quantummaid.reflectmaid.typescanner.states.StatefulDefinition;
import de.quantummaid.reflectmaid.typescanner.states.detected.Unreasoned;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.serializationOnlyResult;
import static de.quantummaid.mapmaid.exceptions.ThrowableSerializer.throwableSerializer;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowableStateFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final TypeIdentifier stackTraceType;

    public static ThrowableStateFactory throwableStateFactory(final ReflectMaid reflectMaid) {
        final ResolvedType stackTraceResolvedType = reflectMaid.resolve(StackTraceElement[].class);
        final TypeIdentifier stackTraceType = typeIdentifierFor(stackTraceResolvedType);
        return new ThrowableStateFactory(stackTraceType);
    }

    @Override
    public StatefulDefinition<MapMaidTypeScannerResult> create(final TypeIdentifier type,
                                                               final Context<MapMaidTypeScannerResult> context) {
        if (type.isVirtual()) {
            return null;
        }
        final Class<?> assignableType = type.realType().assignableType();
        if (!Throwable.class.isAssignableFrom(assignableType)) {
            return null;
        }
        final ThrowableSerializer serializer = throwableSerializer(stackTraceType);
        context.setManuallyConfiguredResult(result(serializationOnlyResult(serializer), type));
        return new Unreasoned<>(context);
    }
}
