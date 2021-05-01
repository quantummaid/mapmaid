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

package de.quantummaid.mapmaid.polymorphy.finiteresolver;

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.polymorphy.finiteresolver.MappedType.mappedType;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FiniteTypeResolver {
    private final List<MappedType> types;

    public static FiniteTypeResolver finiteTypeResolver(final List<ResolvedType> types) {
        final List<Class<?>> seenBaseTypes = new ArrayList<>(types.size());
        final List<MappedType> mappedTypes = new ArrayList<>(types.size());
        types.forEach(type -> {
            final Class<?> baseType = type.assignableType();
            if (seenBaseTypes.contains(baseType)) {
                final String typesString = types.stream()
                        .map(ResolvedType::description)
                        .collect(joining(", "));
                throw mapMaidException("it is not possible to reliably determine the type of an object" +
                        " from pool of possible types: [" + typesString + "]");
            }
            seenBaseTypes.add(baseType);
            final MappedType mappedType = mappedType(baseType, type);
            mappedTypes.add(mappedType);
        });
        return new FiniteTypeResolver(mappedTypes);
    }

    public ResolvedType determineType(final Object object) {
        validateNotNull(object, "object");
        final Class<?> queriedClass = object.getClass();
        final Optional<ResolvedType> type = types.stream()
                .filter(mappedType -> mappedType.matches(queriedClass))
                .map(MappedType::type)
                .findFirst();
        if (type.isEmpty()) {
            final String typesString = types.stream()
                    .map(MappedType::type)
                    .map(ResolvedType::description)
                    .collect(joining(", "));
            throw mapMaidException("class " + queriedClass.getName() + " is not part of possible classes [" + typesString + "]");
        }
        return type.get();
    }
}
