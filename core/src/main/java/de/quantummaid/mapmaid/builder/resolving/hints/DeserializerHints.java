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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.quantummaid.mapmaid.builder.resolving.hints.Hint.*;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public final class DeserializerHints {

    // TODO must be disambiguous, fail for ambiguous result
    public static Hint<TypeDeserializer> deserializeAsSerializedObjectUsingMethod(final Class<?> type,
                                                                                  final String methodName) {
        final ClassType classType = fromClassWithoutGenerics(type);
        return deserializeAsSerializedObjectUsingMethod(classType, methodName);
    }

    public static Hint<TypeDeserializer> deserializeAsSerializedObjectUsingMethod(final ClassType type,
                                                                                  final String methodName) {
        final List<TypeDeserializer> deserializers = type.publicMethods().stream()
                .filter(resolvedMethod -> resolvedMethod.method().getName().equals(methodName))
                .map(resolvedMethod -> MethodSerializedObjectDeserializer.methodDeserializer(type, resolvedMethod))
                .collect(toList());
        return deserializeUsing(type, deserializers);
    }

    // TODO fixed definition
    public static <T> Hint<TypeDeserializer> deserializeAsCustomPrimitiveUsing(final Class<T> type,
                                                                               final Function<String, T> deserializer) {
        final ClassType classType = fromClassWithoutGenerics(type);
        return deserializeAsCustomPrimitiveUsing(classType, (Function<String, Object>) deserializer);
    }

    public static Hint<TypeDeserializer> deserializeAsCustomPrimitiveUsing(final ResolvedType type,
                                                                           final Function<String, Object> deserializer) {
        throw new UnsupportedOperationException(); // TODO
        /*
        final CustomPrimitiveDeserializer customPrimitiveDeserializer = value -> deserializer.apply((String) value);
        return deserializeUsing(type, customPrimitiveDeserializer);
         */
    }

    // TODO fixed + refactor with di recipe
    public static <T> Hint<TypeDeserializer> deserializeUsing(final Class<T> type,
                                                              final Supplier<T> deserializer) {
        final ClassType classType = fromClassWithoutGenerics(type);
        return deserializeUsing(classType, (Supplier<Object>) deserializer);
    }

    public static Hint<TypeDeserializer> deserializeUsing(final ResolvedType type,
                                                          final Supplier<Object> deserializer) {
        return deserializeUsing(type, deserializer);
    }

    // TODO fixed definition
    public static Hint<TypeDeserializer> deserializeUsing(final ResolvedType type,
                                                          final TypeDeserializer deserializer) {
        return onlyFor(bucket -> bucket.id().equals(type), choosing(deserializer));
    }

    // TODO no
    public static Hint<TypeDeserializer> deserializeUsing(final ResolvedType type,
                                                          final List<TypeDeserializer> deserializers) {
        return onlyFor(bucket -> bucket.id().equals(type), bucket -> {
            bucket.strikeAll(format("overwritten by '%s'", deserializers));
            bucket.add(deserializers);
        });
    }

    public static Hint<TypeDeserializer> ignoreFactoriesNamed(final String... name) {
        return discriminatingType(CustomPrimitiveByMethodDeserializer.class, // TODO
                factory -> factory.method().getName().equals(name));
    }

    public static Hint<TypeDeserializer> enforceSerializedObjectFor(final ResolvedType type) {
        return onlyForType(type, onlyAllow("must be a serialized object", SerializedObjectDeserializer.class));
    }

    public static Hint<TypeDeserializer> onlyAllow(final String message, final Class<?>... types) {
        final List<Class<?>> typesList = asList(types);
        return bucket -> bucket.strike(deserializer -> {
            return typesList.stream().noneMatch(type -> type.isInstance(deserializer));
        }, message);
    }

    // TODO includeFields, excludeFields
    public static Hint<TypeDeserializer> enforceFieldsFor(final Class<?> type, final String... fields) {
        final ClassType classType = fromClassWithoutGenerics(type);
        return enforceFieldsFor(classType, fields);
    }

    public static Hint<TypeDeserializer> enforceFieldsFor(final ResolvedType type, final String... fields) {
        final List<String> fieldsList = asList(fields);
        return concat(enforceSerializedObjectFor(type),
                onlyForType(type, bucket -> bucket.strike(deserializer -> {
                    if(!(deserializer instanceof SerializedObjectDeserializer)) {
                        return false;
                    }
                    final Set<String> currentFields = ((SerializedObjectDeserializer) deserializer).fields().fields().keySet(); // TODO remove train wreck
                    return !sameContent(currentFields, fieldsList);
                }, format("needs to match '%s'", fieldsList))));
    }

    private static <T> boolean sameContent(final Collection<T> a, final Collection<T> b) {
        if (a.size() != b.size()) {
            return false;
        }
        return a.containsAll(b);
    }
}
