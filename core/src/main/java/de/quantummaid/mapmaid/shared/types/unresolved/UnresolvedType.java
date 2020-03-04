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

package de.quantummaid.mapmaid.shared.types.unresolved;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.TypeVariableName;
import de.quantummaid.mapmaid.shared.types.unresolved.breaking.TypeVariableResolvers;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithGenerics;
import static de.quantummaid.mapmaid.shared.types.TypeVariableName.typeVariableNamesOf;
import static de.quantummaid.mapmaid.shared.types.unresolved.breaking.TypeVariableResolvers.resolversFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Arrays.asList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UnresolvedType {
    private final Class<?> type;
    private final List<TypeVariableName> variables;
    private final TypeVariableResolvers resolvers;

    public static UnresolvedType unresolvedType(final Class<?> type) {
        validateNotNull(type, "type");
        final TypeVariableResolvers resolvers = resolversFor(type);
        return new UnresolvedType(type, typeVariableNamesOf(type), resolvers);
    }

    public ResolvedType resolve(final ResolvedType... values) {
        return resolve(asList(values));
    }

    public ResolvedType resolve(final List<ResolvedType> values) {
        if (values.size() != this.variables.size()) {
            throw new IllegalArgumentException();
        }
        final Map<TypeVariableName, ResolvedType> resolvedParameters = new HashMap<>(values.size());
        for (int i = 0; i < this.variables.size(); ++i) {
            final TypeVariableName name = this.variables.get(i);
            final ResolvedType value = values.get(i);
            resolvedParameters.put(name, value);
        }
        if(resolvedParameters.isEmpty()) {
            return ResolvedType.resolvedType(this.type);
        } else {
            return fromClassWithGenerics(this.type, resolvedParameters);
        }
    }

    public ResolvedType resolveFromObject(final Object object) {
        final List<ResolvedType> typeList = this.resolvers.resolve(object);
        return resolve(typeList.toArray(ResolvedType[]::new));
    }
}
