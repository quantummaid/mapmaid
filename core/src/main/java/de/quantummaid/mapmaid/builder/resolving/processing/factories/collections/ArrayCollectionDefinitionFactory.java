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

package de.quantummaid.mapmaid.builder.resolving.processing.factories.collections;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolvedtype.ArrayType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.ReflectMaid;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualDeserializerSignal.addManualDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualSerializerSignal.addManualSerializer;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ArrayCollectionDeserializer.arrayDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ArrayCollectionSerializer.arraySerializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayCollectionDefinitionFactory implements StateFactory {

    public static ArrayCollectionDefinitionFactory arrayFactory() {
        return new ArrayCollectionDefinitionFactory();
    }

    @Override
    public Optional<StateFactoryResult> create(final ReflectMaid reflectMaid,
                                               final TypeIdentifier typeIdentifier,
                                               final Context context,
                                               final MapMaidConfiguration configuration) {
        if (typeIdentifier.isVirtual()) {
            return empty();
        }
        final ResolvedType type = typeIdentifier.getRealType();

        if (!(type instanceof ArrayType)) {
            return empty();
        }
        final ResolvedType componentType = ((ArrayType) type).componentType();
        final TypeIdentifier componentTypeIdentifier = typeIdentifierFor(componentType);
        return Optional.of(stateFactoryResult(unreasoned(context), List.of(
                addManualSerializer(typeIdentifier, arraySerializer(componentTypeIdentifier)),
                addManualDeserializer(typeIdentifier, arrayDeserializer(componentTypeIdentifier, componentType))
        )));
    }
}
