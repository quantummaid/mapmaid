/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.shared.types.ArrayType;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.types.TypeVariableName.typeVariableName;

// TODO move
public final class Mirror {

    private Mirror() {
    }

    public static boolean mirrorsFields(final Map<String, ResolvedType> fieldsA,
                                        final Map<String, ResolvedType> fieldsB) {
        if (fieldsA.size() != fieldsB.size()) {
            return false;
        }
        for (final Map.Entry<String, ResolvedType> entry : fieldsA.entrySet()) {
            final String name = entry.getKey();
            if (!fieldsB.containsKey(name)) {
                return false;
            }
            if (!mirrors(entry.getValue(), fieldsB.get(name))) {
                return false;
            }
        }
        return true;
    }

    public static boolean mirrors(final ResolvedType typeA, final ResolvedType typeB) {
        final Optional<ResolvedType> componentA = collectionComponent(typeA);
        final Optional<ResolvedType> componentB = collectionComponent(typeB);
        if (componentA.isPresent() && componentB.isPresent()) {
            return componentA.get().equals(componentB.get());
        }
        return typeA.equals(typeB);
    }

    private static Optional<ResolvedType> collectionComponent(final ResolvedType type) {
        if (type instanceof ArrayType) {
            final ArrayType arrayType = (ArrayType) type;
            return Optional.of(arrayType.componentType());
        }
        final Class<?> assignableType = type.assignableType();
        if (Collection.class.isAssignableFrom(assignableType)) {
            final ClassType classType = (ClassType) type;
            return Optional.of(classType.typeParameter(typeVariableName("E")));
        }
        return Optional.empty();
    }
}
