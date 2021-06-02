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

package de.quantummaid.mapmaid.builder.resolving.processing.factories.kotlin;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer;
import de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicUtils.nameToIdentifier;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;

public final class KotlinSealedClassFactory implements StateFactory<DisambiguationResult> {

    public static KotlinSealedClassFactory kotlinSealedClassFactory() {
        return new KotlinSealedClassFactory();
    }

    @Override
    public Optional<StateFactoryResult<DisambiguationResult>> create(final ReflectMaid reflectMaid,
                                                                     final TypeIdentifier type,
                                                                     final Context<DisambiguationResult> context,
                                                                     final MapMaidConfiguration mapMaidConfiguration) {
        if (type.isVirtual()) {
            return empty();
        }
        final ResolvedType resolvedType = type.getRealType();
        final List<ResolvedType> sealedSubclasses = resolvedType.sealedSubclasses();
        if (sealedSubclasses.isEmpty()) {
            return empty();
        }

        final List<TypeIdentifier> subtypes = sealedSubclasses.stream()
                .map(TypeIdentifier::typeIdentifierFor)
                .collect(toList());
        final BiMap<String, TypeIdentifier> nameToType = nameToIdentifier(subtypes, mapMaidConfiguration);

        final PolymorphicSerializer serializer = polymorphicSerializer(type, sealedSubclasses, nameToType, "type");
        final PolymorphicDeserializer deserializer = polymorphicDeserializer(type, nameToType, "type");
        context.setManuallyConfiguredResult(duplexResult(serializer, deserializer));
        return Optional.of(stateFactoryResult(unreasoned(context)));
    }
}
