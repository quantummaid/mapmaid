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

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.serializationOnlyResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.exceptions.ThrowableSerializer.throwableSerializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static java.util.Optional.empty;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowableStateFactory implements StateFactory<DisambiguationResult> {
    private final TypeIdentifier stackTraceType;

    public static ThrowableStateFactory throwableStateFactory(final ReflectMaid reflectMaid) {
        final ResolvedType stackTraceResolvedType = reflectMaid.resolve(StackTraceElement[].class);
        final TypeIdentifier stackTraceType = typeIdentifierFor(stackTraceResolvedType);
        return new ThrowableStateFactory(stackTraceType);
    }

    @Override
    public Optional<StateFactoryResult<DisambiguationResult>> create(final ReflectMaid reflectMaid,
                                                                     final TypeIdentifier type,
                                                                     final Context<DisambiguationResult> context,
                                                                     final MapMaidConfiguration mapMaidConfiguration) {
        if (type.isVirtual()) {
            return empty();
        }
        final Class<?> assignableType = type.getRealType().assignableType();
        if (!Throwable.class.isAssignableFrom(assignableType)) {
            return empty();
        }
        final ThrowableSerializer serializer = throwableSerializer(stackTraceType);
        context.setManuallyConfiguredResult(serializationOnlyResult(serializer));
        return Optional.of(stateFactoryResult(unreasoned(context)));
    }
}
