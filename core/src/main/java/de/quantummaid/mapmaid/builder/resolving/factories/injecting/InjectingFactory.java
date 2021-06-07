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

package de.quantummaid.mapmaid.builder.resolving.factories.injecting;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static de.quantummaid.mapmaid.builder.injection.InjectionSerializer.injectionSerializer;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class InjectingFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final TypeIdentifier targetType;
    private final TypeDeserializer deserializer;

    public static InjectingFactory injectingFactory(final TypeIdentifier targetType, final TypeDeserializer deserializer) {
        return new InjectingFactory(targetType, deserializer);
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        return targetType.equals(type);
    }

    @Override
    public void create(@NotNull final TypeIdentifier type,
                       @NotNull final Context<MapMaidTypeScannerResult> context) {
        final TypeSerializer serializer = injectionSerializer(targetType);
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), targetType));
    }
}
