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

package de.quantummaid.mapmaid.builder.resolving.factories.collections;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.factories.collections.CollectionInformation.collectionInformations;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ListCollectionDeserializer.listDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ListCollectionSerializer.listSerializer;
import static de.quantummaid.reflectmaid.TypeVariableName.typeVariableName;
import static java.lang.String.format;

public final class NativeJavaCollectionDefinitionFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final Map<Class<?>, CollectionInformation> collectionInformations = collectionInformations();

    public static NativeJavaCollectionDefinitionFactory nativeJavaCollectionsFactory() {
        return new NativeJavaCollectionDefinitionFactory();
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        if (type.isVirtual()) {
            return false;
        }
        final ResolvedType resolvedType = type.realType();
        final Class<?> assignableType = resolvedType.assignableType();
        return collectionInformations.containsKey(assignableType);
    }

    @Override
    public void create(final TypeIdentifier typeIdentifier,
                       final Context<MapMaidTypeScannerResult> context) {
        final ResolvedType type = typeIdentifier.realType();
        if (type.typeParameters().size() != 1) {
            throw new UnsupportedOperationException(format(
                    "This should never happen. A collection of type '%s' has more than one type parameter",
                    type.description()));
        }
        final ResolvedType genericType = ((ClassType) type).typeParameter(typeVariableName("E"));
        final CollectionInformation collectionInformation = collectionInformations.get(type.assignableType());
        context.setManuallyConfiguredResult(result(duplexResult(
                listSerializer(genericType),
                listDeserializer(genericType, collectionInformation.mapper())
        ), typeIdentifier));
    }
}
