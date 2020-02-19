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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.customprimitive;

import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers.serializersAndDeserializers;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitiveSymmetryBuilder {
    private final Map<Class<?>, List<TypeSerializer>> serializers;
    private final Map<Class<?>, List<TypeDeserializer>> deserializers;

    public static CustomPrimitiveSymmetryBuilder customPrimitiveSymmetryBuilder() {
        return new CustomPrimitiveSymmetryBuilder(new HashMap<>(10), new HashMap<>(10));
    }

    public void addDeserializer(final CustomPrimitiveDeserializer deserializer) {
        final Class<?> baseType = deserializer.baseType();
        ensureKeyIsPresent(baseType, this.deserializers);
        this.deserializers.get(baseType).add(deserializer);
    }

    public void addSerializer(final CustomPrimitiveSerializer serializer) {
        final Class<?> baseType = serializer.baseType();
        ensureKeyIsPresent(baseType, this.serializers);
        this.serializers.get(baseType).add(serializer);
    }

    public Optional<SerializersAndDeserializers> determineGreatestCommonFields() {
        // TODO from mappings
        return customPrimitivesClass(
                String.class,
                int.class, Integer.class,
                long.class, Long.class,
                double.class, Double.class,
                boolean.class, Boolean.class
        );
    }

    private static <T> void ensureKeyIsPresent(final Class<?> type, final Map<Class<?>, List<T>> map) {
        if (!map.containsKey(type)) {
            map.put(type, new ArrayList<>(3));
        }
    }

    private Optional<SerializersAndDeserializers> customPrimitivesClass(final Class<?>... typesInOrder) {
        for (final Class<?> baseType : typesInOrder) {
            if (this.serializers.containsKey(baseType) && this.deserializers.containsKey(baseType)) { // TODO
                final SerializersAndDeserializers serializersAndDeserializers = serializersAndDeserializers(
                        this.serializers.get(baseType),
                        this.deserializers.get(baseType)
                );
                return of(serializersAndDeserializers);
            }
        }
        return empty();
    }
}
