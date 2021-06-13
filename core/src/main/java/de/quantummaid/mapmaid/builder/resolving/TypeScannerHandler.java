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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.Lingo;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.supertypes.SupertypeSerializers;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.Processor;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidDetector.mapMaidDetector;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidOnCollectionError.mapMaidOnCollectionError;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidResolver.mapMaidResolver;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.DESERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.SERIALIZATION;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.mapper.definitions.Definition.definition;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;

public final class TypeScannerHandler {

    private TypeScannerHandler() {
    }

    public static Definitions handleStateMachine(final Processor<MapMaidTypeScannerResult> processor,
                                                 final Disambiguators disambiguators,
                                                 final List<TypeIdentifier> injectionTypes,
                                                 final List<Signal<MapMaidTypeScannerResult>> signals,
                                                 final SupertypeSerializers supertypeSerializers,
                                                 final ReflectMaid reflectMaid) {
        signals.forEach(processor::dispatch);
        final MapMaidDetector detector = mapMaidDetector(disambiguators, injectionTypes, supertypeSerializers);
        final MapMaidResolver resolver = mapMaidResolver(supertypeSerializers);
        final MapMaidOnCollectionError onError = mapMaidOnCollectionError(reflectMaid);
        final Map<TypeIdentifier, Map<Scope, CollectionResult<MapMaidTypeScannerResult>>> result =
                processor.collect(detector, resolver, onError, Lingo::mode);
        final Map<TypeIdentifier, Definition> definitionsMap = buildDefinitionsMap(result, supertypeSerializers);
        final DebugInformation debugInformation = debugInformation(result, processor.log(), reflectMaid);
        return definitions(definitionsMap, debugInformation);
    }

    private static Map<TypeIdentifier, Definition> buildDefinitionsMap(
            final Map<TypeIdentifier, Map<Scope, CollectionResult<MapMaidTypeScannerResult>>> result,
            final SupertypeSerializers supertypeSerializers
    ) {
        final Map<TypeIdentifier, Definition> definitionsMap = new HashMap<>(result.size());
        result.forEach((type, collectionResultByScope) -> {
            final CollectionResult<MapMaidTypeScannerResult> collectionResult = collectionResultByScope.get(rootScope());
            final DetectionRequirements requirements = collectionResult.getDetectionRequirements();
            final TypeSerializer serializer;
            if (requirements.requires(SERIALIZATION)) {
                serializer = collectionResult.getDefinition().disambiguationResult().serializer();
            } else {
                serializer = null;
            }

            final TypeDeserializer deserializer;
            if (requirements.requires(DESERIALIZATION)) {
                deserializer = collectionResult.getDefinition().disambiguationResult().deserializer();
            } else {
                deserializer = null;
            }
            final List<TypeIdentifier> superTypeSerializers = supertypeSerializers.detectSuperTypeSerializersFor(type);
            final Definition definition = definition(type, serializer, deserializer, superTypeSerializers);
            definitionsMap.put(type, definition);
        });
        return definitionsMap;
    }
}
