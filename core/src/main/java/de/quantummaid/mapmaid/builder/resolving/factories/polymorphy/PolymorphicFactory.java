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

package de.quantummaid.mapmaid.builder.resolving.factories.polymorphy;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import de.quantummaid.reflectmaid.typescanner.states.StatefulDefinition;
import de.quantummaid.reflectmaid.typescanner.states.detected.Unreasoned;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.disambiguationResult;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicUtils.nameToIdentifier;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PolymorphicFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final TypeIdentifier superType;
    private final List<ResolvedType> subTypes;
    private final MapMaidConfiguration configuration;
    private final RequiredCapabilities capabilities;

    public static PolymorphicFactory polymorphicFactory(final TypeIdentifier superType,
                                                        final List<ResolvedType> subTypes,
                                                        final MapMaidConfiguration configuration,
                                                        final RequiredCapabilities capabilities) {
        return new PolymorphicFactory(superType, subTypes, configuration, capabilities);
    }

    @Override
    public StatefulDefinition<MapMaidTypeScannerResult> create(final TypeIdentifier type,
                                                               final Context<MapMaidTypeScannerResult> context) {
        if (!superType.equals(type)) {
            return null;
        }
        final List<TypeIdentifier> subTypeIdentifiers = subTypes.stream()
                .map(TypeIdentifier::typeIdentifierFor)
                .collect(toList());
        final BiMap<String, TypeIdentifier> nameToType = nameToIdentifier(subTypeIdentifiers, configuration);
        final String typeIdentifierKey = configuration.getTypeIdentifierKey();
        final TypeSerializer serializer;
        if (capabilities.hasSerialization()) {
            serializer = polymorphicSerializer(superType, subTypes, nameToType, typeIdentifierKey);
        } else {
            serializer = null;
        }
        final TypeDeserializer deserializer;
        if (capabilities.hasDeserialization()) {
            deserializer = polymorphicDeserializer(superType, nameToType, typeIdentifierKey);
        } else {
            deserializer = null;
        }
        context.setManuallyConfiguredResult(MapMaidTypeScannerResult.result(disambiguationResult(serializer, deserializer), superType));
        return new Unreasoned<>(context);
    }
}
