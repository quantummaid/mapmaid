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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.detection.priority.Prioritized.prioritized;
import static de.quantummaid.mapmaid.builder.detection.priority.Priority.CONSTRUCTOR;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.ConstructorSerializedObjectDeserializer.createDeserializer;
import static de.quantummaid.mapmaid.shared.types.resolver.ResolvedConstructor.resolvePublicConstructors;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstructorBasedDeserializationDetector implements SerializedObjectDeserializationDetector {

    public static SerializedObjectDeserializationDetector constructorBased() {
        return new ConstructorBasedDeserializationDetector();
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        final ClassType classType = (ClassType) type;
        return resolvePublicConstructors(classType).stream()
                .map(constructor -> createDeserializer(classType, constructor))
                .map(deserializer -> prioritized(deserializer, CONSTRUCTOR))
                .collect(toList());
    }
}
