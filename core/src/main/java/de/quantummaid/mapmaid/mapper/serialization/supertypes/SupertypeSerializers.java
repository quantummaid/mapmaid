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

package de.quantummaid.mapmaid.mapper.serialization.supertypes;

import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SupertypeSerializers {
    private final Map<TypeIdentifier, TypeSerializer> superTypeSerializers;

    public static SupertypeSerializers superTypeSerializers(final Map<TypeIdentifier, TypeSerializer> superTypeSerializers) {
        validateNotNull(superTypeSerializers, "superTypeSerializers");
        return new SupertypeSerializers(superTypeSerializers);
    }

    public TypeSerializer superTypeSerializer(final TypeIdentifier typeIdentifier) {
        if (!superTypeSerializers.containsKey(typeIdentifier)) {
            throw mapMaidException("supertype " + typeIdentifier.description() + " not found - this should never happen");
        }
        return superTypeSerializers.get(typeIdentifier);
    }

    public boolean hasRegisteredSupertype(final ResolvedType resolvedType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        final List<TypeIdentifier> serializers = detectSuperTypeSerializersFor(typeIdentifier);
        return !serializers.isEmpty();
    }

    public List<TypeIdentifier> detectSuperTypeSerializersFor(final TypeIdentifier typeIdentifier) {
        if (typeIdentifier.isVirtual()) {
            return emptyList();
        }
        final ResolvedType resolvedType = typeIdentifier.realType();
        return resolvedType
                .allSupertypes()
                .stream()
                .map(TypeIdentifier::typeIdentifierFor)
                .filter(superTypeSerializers::containsKey)
                .collect(toList());
    }
}
