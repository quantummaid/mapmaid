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

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.shared.types.ArrayType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.fixed.resolving.FixedResolvingDuplex.fixedResolvingDuplex;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ArrayCollectionDeserializer.arrayDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ArrayCollectionSerializer.arraySerializer;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayCollectionDefinitionFactory implements StateFactory {

    public static ArrayCollectionDefinitionFactory arrayFactory() {
        return new ArrayCollectionDefinitionFactory();
    }

    @Override
    public Optional<StatefulDefinition> create(final ResolvedType type, final Context context) {
        if (!(type instanceof ArrayType)) {
            return empty();
        }
        final ResolvedType genericType = ((ArrayType) type).componentType();
        context.setDeserializer(arrayDeserializer(genericType));
        context.setSerializer(arraySerializer(genericType));
        return Optional.of(fixedResolvingDuplex(context)); // TODO unreasoned?
    }
}
