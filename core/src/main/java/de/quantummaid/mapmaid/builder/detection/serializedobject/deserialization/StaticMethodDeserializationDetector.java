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

package de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization;

import de.quantummaid.mapmaid.builder.detection.priority.Prioritized;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.detection.priority.Prioritized.prioritized;
import static de.quantummaid.mapmaid.builder.detection.priority.Priority.FACTORY;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer.methodDeserializer;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StaticMethodDeserializationDetector implements SerializedObjectDeserializationDetector {

    public static SerializedObjectDeserializationDetector staticMethodBased() {
        return new StaticMethodDeserializationDetector();
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final ResolvedType type) {
        return detectDeserializerMethods(type).stream()
                .map(method -> methodDeserializer((ClassType) type, method))
                .map(deserializer -> prioritized(deserializer, FACTORY))
                .collect(toList());
    }

    private static List<ResolvedMethod> detectDeserializerMethods(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        final ClassType classType = (ClassType) type;
        return classType.publicMethods().stream()
                .filter(resolvedMethod -> isStatic(resolvedMethod.method().getModifiers()))
                .filter(resolvedMethod -> {
                    final Optional<ResolvedType> resolvedType = resolvedMethod.returnType();
                    final Optional<Boolean> optional = resolvedType.map(type::equals);
                    return optional.orElse(false);
                })
                .filter(resolvedMethod -> resolvedMethod.parameters().size() > 0)
                .collect(toList());
    }

}
