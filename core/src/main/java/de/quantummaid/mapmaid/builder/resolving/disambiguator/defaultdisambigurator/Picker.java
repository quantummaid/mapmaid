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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.SerializersAndDeserializers;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public final class Picker {

    private Picker() {
    }

    public static DetectionResult<TypeSerializer> pickSerializer(final SerializersAndDeserializers options) {
        final List<TypeSerializer> serializers = options.serializers();
        final Optional<DetectionResult<TypeSerializer>> preferredCustomPrimitive = oneOrNone(serializers, TypeSerializer::description);
        return preferredCustomPrimitive
                .orElseGet(() -> failure("No serializers to choose from"));
    }

    @SuppressWarnings("unchecked")
    public static DetectionResult<TypeDeserializer> pickDeserializer(final List<TypeDeserializer> deserializers) {
        final List<TypeDeserializer> customPrimitives = subTypesOf(CustomPrimitiveDeserializer.class, deserializers);
        final Optional<DetectionResult<TypeDeserializer>> customPrimitive = oneOrNone(customPrimitives, TypeDeserializer::description);
        if (customPrimitive.isPresent()) {
            return customPrimitive.get();
        }

        final List<TypeDeserializer> serializedObjects = subTypesOf(SerializedObjectDeserializer.class, deserializers);
        final List<TypeDeserializer> maxPreferred = maxDeserializers(serializedObjects);
        final Optional<DetectionResult<TypeDeserializer>> serializedObject = oneOrNone(maxPreferred, TypeDeserializer::description);
        if (serializedObject.isPresent()) {
            return serializedObject.get();
        }

        return failure("No deserializers to choose from");
    }

    private static List<TypeDeserializer> maxDeserializers(final List<TypeDeserializer> serializedObjectDeserializers) {
        final OptionalInt max = serializedObjectDeserializers.stream()
                .mapToInt(deserializer -> ((SerializedObjectDeserializer) deserializer).fields().fields().size())
                .max();
        if (max.isPresent()) {
            final List<TypeDeserializer> maxDeserializers = serializedObjectDeserializers.stream()
                    .filter(deserializer -> ((SerializedObjectDeserializer) deserializer).fields().fields().size() == max.getAsInt())
                    .collect(toList());
            return maxDeserializers;
        } else {
            return emptyList();
        }
    }

    private static <T> List<T> subTypesOf(final Class<? extends T> type,
                                          final List<T> elements) {
        return elements.stream()
                .filter(type::isInstance)
                .collect(toList());
    }

    // TODO
    public static <T> Optional<DetectionResult<T>> oneOrNone(final List<T> list, final Function<T, String> describer) {
        if (list.size() == 1) {
            return Optional.of(success(list.get(0)));
        }
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(failure(format("Cannot decide between %n%s", plotList(list, describer))));
    }

    private static <T> String plotList(final List<T> list, final Function<T, String> describer) {
        return list.stream()
                .map(describer)
                .map(s -> "- " + s)
                .collect(joining("\n"));
    }
}
