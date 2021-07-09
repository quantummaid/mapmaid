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

package de.quantummaid.mapmaid.builder.resolving.factories.maps;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.factories.maps.MapDeserializer.mapDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.factories.maps.MapSerializer.mapSerializer;
import static de.quantummaid.reflectmaid.TypeVariableName.typeVariableName;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapDefinitionFactory implements StateFactory<MapMaidTypeScannerResult> {

    public static MapDefinitionFactory mapFactory() {
        return new MapDefinitionFactory();
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        if (type.isVirtual()) {
            return false;
        }
        final ResolvedType resolvedType = type.realType();
        return resolvedType.assignableType() == Map.class;
    }

    @Override
    public void create(final TypeIdentifier typeIdentifier,
                       final Context<MapMaidTypeScannerResult> context) {
        final ClassType type = (ClassType) typeIdentifier.realType();
        final ResolvedType keyType = type.resolveTypeVariable(typeVariableName("K"));
        final ResolvedType valueType = type.resolveTypeVariable(typeVariableName("V"));
        final TypeIdentifier keyTypeIdentifier = typeIdentifierFor(keyType);
        final TypeIdentifier valueTypeIdentifier = typeIdentifierFor(valueType);
        final MapSerializer serializer = mapSerializer(keyTypeIdentifier, valueTypeIdentifier);
        final MapDeserializer deserializer = mapDeserializer(keyTypeIdentifier, valueTypeIdentifier);
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), typeIdentifier));
    }
}
