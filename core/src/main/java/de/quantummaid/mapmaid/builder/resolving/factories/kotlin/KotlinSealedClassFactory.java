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

package de.quantummaid.mapmaid.builder.resolving.factories.kotlin;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer;
import de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicUtils.nameToIdentifier;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class KotlinSealedClassFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final MapMaidConfiguration mapMaidConfiguration;

    public static KotlinSealedClassFactory kotlinSealedClassFactory(final MapMaidConfiguration mapMaidConfiguration) {
        return new KotlinSealedClassFactory(mapMaidConfiguration);
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        if (type.isVirtual()) {
            return false;
        }
        final ResolvedType resolvedType = type.realType();
        final List<ResolvedType> sealedSubclasses = resolvedType.sealedSubclasses();
        return !sealedSubclasses.isEmpty();
    }

    @Override
    public void create(final TypeIdentifier type,
                       final Context<MapMaidTypeScannerResult> context) {
        final ResolvedType resolvedType = type.realType();
        final List<ResolvedType> sealedSubclasses = resolvedType.sealedSubclasses();
        final List<TypeIdentifier> subtypes = sealedSubclasses.stream()
                .map(TypeIdentifier::typeIdentifierFor)
                .collect(toList());
        final BiMap<String, TypeIdentifier> nameToType = nameToIdentifier(subtypes, mapMaidConfiguration);

        final PolymorphicSerializer serializer = polymorphicSerializer(type, sealedSubclasses, nameToType, "type");
        final PolymorphicDeserializer deserializer = polymorphicDeserializer(type, nameToType, "type");
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), type));
    }
}
