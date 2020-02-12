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

package de.quantummaid.mapmaid.builder.resolving.disambiguator;

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveAsEnumDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry.SymmetryBuilder.symmetryBuilder;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultDisambiguator implements Disambiguator {

    public static DefaultDisambiguator defaultDisambiguator() {
        return new DefaultDisambiguator();
    }

    @Override
    public DetectionResult<DisambiguationResult> disambiguate(final ResolvedType type,
                                                              final SerializersAndDeserializers serializersAndDeserializers,
                                                              final ScanInformationBuilder scanInformationBuilder) {
        System.out.println("type.description() = " + type.description());
        if (serializersAndDeserializers.serializationOnly()) {
            System.out.println("serialization-only");
            final List<TypeSerializer> serializers = serializersAndDeserializers.serializers();
            final DetectionResult<TypeSerializer> serializer = pickSerializer(serializers);
            if (!serializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(),
                        format("less priority than %s", serializer.result().description()));
            }
            return serializer
                    .map(serializer1 -> { // TODO
                        if(!(serializer1 instanceof SerializationFieldOptions)) {
                            return serializer1;
                        }
                        return ((SerializationFieldOptions) serializer1).instantiateAll();
                    })
                    .map(DisambiguationResult::serializationOnlyResult);
        }

        if (serializersAndDeserializers.deserializationOnly()) {
            final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(serializersAndDeserializers.deserializers());
            if (!deserializer.isFailure()) {
                scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(),
                        format("less priority than %s", deserializer.result().description()));
            }
            return deserializer.map(DisambiguationResult::deserializationOnlyResult);
        }

        final List<String> stops = List.of("de.quantummaid.mapmaid.testsupport.domain.valid.AComplexTypeWithListButArrayConstructor");
        // TODO
        if (stops.contains(type.description())) {
            System.out.println("type = " + type);
        }

        final SymmetryBuilder symmetryBuilder = symmetryBuilder();
        final List<TypeDeserializer> deserializers = serializersAndDeserializers.deserializers();
        deserializers.forEach(symmetryBuilder::addDeserializer);
        final List<TypeSerializer> serializers = serializersAndDeserializers.serializers();
        serializers.forEach(symmetryBuilder::addSerializer);

        final DetectionResult<SerializersAndDeserializers> symmetric = symmetryBuilder.determineGreatestCommonFields();

        if (symmetric.isFailure()) {
            scanInformationBuilder.ignoreAllSerializers("no symmetric match");
            scanInformationBuilder.ignoreAllDeserializers("no symmetric match");
            return failure(format("Failed to detect %s: %s", type.description(), symmetric.reasonForFailure()));
        }

        final SerializersAndDeserializers symmetricResult = symmetric.result();

        final List<TypeSerializer> symmetricSerializers = symmetricResult.serializers();
        final DetectionResult<TypeSerializer> serializer = pickSerializer(symmetricSerializers);
        if (!serializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherSerializers(serializer.result(), format("most symmetry in %s", serializer.result().description()));
        }

        final List<TypeDeserializer> symmetricDeserializers = symmetricResult.deserializers();
        final DetectionResult<TypeDeserializer> deserializer = pickDeserializer(symmetricDeserializers);
        if (!deserializer.isFailure()) {
            scanInformationBuilder.ignoreAllOtherDeserializers(deserializer.result(), format("most symmetry in %s", deserializer.result().description()));
        }

        return combine(serializer, deserializer, DisambiguationResult::duplexResult);
    }

    private DetectionResult<TypeSerializer> pickSerializer(final List<TypeSerializer> serializers) {
        final List<TypeSerializer> customPrimitives = subTypesOf(CustomPrimitiveSerializer.class, serializers);
        final Optional<DetectionResult<TypeSerializer>> customPrimitive = oneOrNone(customPrimitives, TypeSerializer::description);
        if (customPrimitive.isPresent()) {
            return customPrimitive.get();
        }

        final List<TypeSerializer> serializedObjects = subTypesOf(SerializationFieldOptions.class, serializers);
        final Optional<DetectionResult<TypeSerializer>> serializedObject = oneOrNone(serializedObjects, TypeSerializer::description);
        if (serializedObject.isPresent()) {
            return serializedObject.get();
        }

        // TODO
        final List<TypeSerializer> serializedObjects2 = subTypesOf(SerializedObjectSerializer.class, serializers);
        final Optional<DetectionResult<TypeSerializer>> serializedObject2 = oneOrNone(serializedObjects2, TypeSerializer::description);
        if (serializedObject2.isPresent()) {
            return serializedObject2.get();
        }

        return failure("No serializers to choose from");
    }

    private DetectionResult<TypeDeserializer> pickDeserializer(final List<TypeDeserializer> deserializers) {
        final List<TypeDeserializer> enums = subTypesOf(CustomPrimitiveAsEnumDeserializer.class, deserializers);
        final Optional<DetectionResult<TypeDeserializer>> theEnum = oneOrNone(enums, TypeDeserializer::description);
        if (theEnum.isPresent()) {
            return theEnum.get();
        }

        final List<TypeDeserializer> customPrimitives = subTypesOf(CustomPrimitiveDeserializer.class, deserializers);
        final Optional<DetectionResult<TypeDeserializer>> customPrimitive = oneOrNone(customPrimitives, TypeDeserializer::description);
        if (customPrimitive.isPresent()) {
            return customPrimitive.get();
        }

        final List<TypeDeserializer> serializedObjects = subTypesOf(SerializedObjectDeserializer.class, deserializers);
        final Optional<DetectionResult<TypeDeserializer>> serializedObject = oneOrNone(serializedObjects, TypeDeserializer::description);
        if (serializedObject.isPresent()) {
            return serializedObject.get();
        }

        return failure("No deserializers to choose from");
    }

    private static <T> List<T> subTypesOf(final Class<? extends T> type,
                                          final List<T> elements) {
        return elements.stream()
                .filter(type::isInstance)
                .collect(toList());
    }

    private static <T> Optional<DetectionResult<T>> oneOrNone(final List<T> list, final Function<T, String> describer) {
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
