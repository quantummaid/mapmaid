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

package de.quantummaid.mapmaid.builder.resolving.factories.primitives;

import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuiltInPrimitiveSerializer implements CustomPrimitiveSerializer {
    private final Class<?> baseType;
    private final List<TypeIdentifier> alsoRegister;

    public static CustomPrimitiveSerializer builtInPrimitiveSerializer(final Class<?> baseType,
                                                                       final List<TypeIdentifier> alsoRegister) {
        return new BuiltInPrimitiveSerializer(baseType, alsoRegister);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return alsoRegister;
    }

    @Override
    public Object serialize(final Object object) {
        return object;
    }

    @Override
    public String description() {
        return "toString()";
    }

    @Override
    public Class<?> baseType() {
        return baseType;
    }
}
