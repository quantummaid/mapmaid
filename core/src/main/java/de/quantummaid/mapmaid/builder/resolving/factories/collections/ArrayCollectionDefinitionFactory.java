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

import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolvedtype.ArrayType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ArrayCollectionDeserializer.arrayDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ArrayCollectionSerializer.arraySerializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayCollectionDefinitionFactory implements StateFactory<DisambiguationResult> {

    public static ArrayCollectionDefinitionFactory arrayFactory() {
        return new ArrayCollectionDefinitionFactory();
    }

    @Override
    public Optional<StateFactoryResult<DisambiguationResult>> create(final TypeIdentifier typeIdentifier,
                                                                     final Context<DisambiguationResult> context) {
        if (typeIdentifier.isVirtual()) {
            return empty();
        }
        final ResolvedType type = typeIdentifier.getRealType();

        if (!(type instanceof ArrayType)) {
            return empty();
        }
        final ResolvedType componentType = ((ArrayType) type).componentType();
        final TypeIdentifier componentTypeIdentifier = typeIdentifierFor(componentType);
        context.setManuallyConfiguredResult(duplexResult(
                arraySerializer(componentTypeIdentifier),
                arrayDeserializer(componentTypeIdentifier, componentType)
        ));
        return Optional.of(stateFactoryResult(unreasoned(context)));
    }
}
