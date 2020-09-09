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
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.ResolvedType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.collections.CollectionInformation.collectionInformations;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualDeserializerSignal.addManualDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualSerializerSignal.addManualSerializer;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ListCollectionDeserializer.listDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ListCollectionSerializer.listSerializer;
import static de.quantummaid.reflectmaid.TypeVariableName.typeVariableName;
import static java.lang.String.format;
import static java.util.Optional.empty;

public final class NativeJavaCollectionDefinitionFactory implements StateFactory {
    private final Map<Class<?>, CollectionInformation> collectionInformations = collectionInformations();

    public static NativeJavaCollectionDefinitionFactory nativeJavaCollectionsFactory() {
        return new NativeJavaCollectionDefinitionFactory();
    }

    @Override
    public Optional<StateFactoryResult> create(final TypeIdentifier typeIdentifier,
                                               final Context context,
                                               final MapMaidConfiguration configuration) {
        if (typeIdentifier.isVirtual()) {
            return empty();
        }
        final ResolvedType type = typeIdentifier.getRealType();

        if (!this.collectionInformations.containsKey(type.assignableType())) {
            return empty();
        }
        if (type.typeParameters().size() != 1) {
            throw new UnsupportedOperationException(format(
                    "This should never happen. A collection of type '%s' has more than one type parameter",
                    type.description()));
        }
        final ResolvedType genericType = ((ClassType) type).typeParameter(typeVariableName("E"));
        final CollectionInformation collectionInformation = this.collectionInformations.get(type.assignableType());
        return Optional.of(stateFactoryResult(unreasoned(context), List.of(
                addManualSerializer(typeIdentifier, listSerializer(genericType)),
                addManualDeserializer(typeIdentifier, listDeserializer(genericType, collectionInformation.mapper()))
        )));
    }
}
