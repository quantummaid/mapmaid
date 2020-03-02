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

package de.quantummaid.mapmaid.builder.detection.collection;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.CollectionDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.collections.CollectionSerializer;
import de.quantummaid.mapmaid.shared.types.ArrayType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ArrayCollectionDeserializer.arrayDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ArrayCollectionSerializer.arraySerializer;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayCollectionDefinitionFactory implements DefinitionFactory {

    public static DefinitionFactory arrayFactory() {
        return new ArrayCollectionDefinitionFactory();
    }

    @Override
    public Optional<Definition> analyze(final ResolvedType type,
                                        final RequiredCapabilities capabilities) {
        if (!(type instanceof ArrayType)) {
            return empty();
        }

        final ResolvedType genericType = ((ArrayType) type).componentType();

        final CollectionSerializer serializer = arraySerializer(genericType);
        final CollectionDeserializer deserializer = arrayDeserializer(genericType);
        return of(generalDefinition(type, serializer, deserializer));
    }
}
