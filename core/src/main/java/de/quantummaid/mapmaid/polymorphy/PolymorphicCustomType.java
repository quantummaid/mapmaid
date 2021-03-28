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

package de.quantummaid.mapmaid.polymorphy;

import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.ReflectMaid;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static de.quantummaid.mapmaid.collections.BiMap.biMap;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PolymorphicCustomType<T> implements CustomType<T> {
    private static final String DEFAULT_TYPE_FIELD = "type";

    private final ReflectMaid reflectMaid;
    private final TypeIdentifier typeIdentifier;
    private final BiMap<String, TypeIdentifier> nameToType;
    private final String typeField;

    public static <T> PolymorphicCustomType<T> fromKotlinSealedClass(final ReflectMaid reflectMaid,
                                                                     final KClass<T> kotlinClass) {
        final Class<T> javaClass = JvmClassMappingKt.getJavaClass(kotlinClass);
        final ResolvedType resolvedType = reflectMaid.resolve(javaClass);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        final List<TypeIdentifier> implementations = kotlinClass.getSealedSubclasses().stream()
                .map(JvmClassMappingKt::getJavaClass)
                .map(reflectMaid::resolve)
                .map(TypeIdentifier::typeIdentifierFor)
                .collect(toList());
        return polymorphicCustomType(reflectMaid, typeIdentifier, implementations, TypeIdentifier::description, DEFAULT_TYPE_FIELD);
    }

    public static <T> PolymorphicCustomType<T> polymorphicCustomType(final ReflectMaid reflectMaid,
                                                                     final TypeIdentifier typeIdentifier,
                                                                     final List<TypeIdentifier> implementations,
                                                                     final Function<TypeIdentifier, String> nameExtractor,
                                                                     final String typeField) {
        final Map<String, TypeIdentifier> nameToTypeMap = implementations.stream()
                .collect(toMap(nameExtractor, type -> type));
        return polymorphicCustomType(reflectMaid, typeIdentifier, biMap(nameToTypeMap), typeField);
    }

    public static <T> PolymorphicCustomType<T> polymorphicCustomType(final ReflectMaid reflectMaid,
                                                                     final TypeIdentifier typeIdentifier,
                                                                     final BiMap<String, TypeIdentifier> nameToType,
                                                                     final String typeField) {
        return new PolymorphicCustomType<>(reflectMaid, typeIdentifier, nameToType, typeField);
    }

    @Override
    public TypeIdentifier type() {
        return typeIdentifier;
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        final PolymorphicDeserializer deserializer = polymorphicDeserializer(
                typeIdentifier,
                nameToType,
                typeField
        );
        return Optional.of(deserializer);
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        final PolymorphicSerializer serializer = polymorphicSerializer(
                reflectMaid,
                typeIdentifier,
                nameToType,
                typeField
        );
        return Optional.of(serializer);
    }
}
