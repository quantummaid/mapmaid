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
import static de.quantummaid.mapmaid.builder.detection.priority.Priority.PRIVILEGED_FACTORY;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization.Common.detectDeserializerMethods;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer.methodDeserializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.toList;

// TODO
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NamedMethodDeserializationDetector implements SerializedObjectDeserializationDetector {
    private final String deserializationMethodName;

    public static SerializedObjectDeserializationDetector namedMethodBased(final String deserializationMethodName) {
        validateNotNull(deserializationMethodName, "deserializationMethodName");
        return new NamedMethodDeserializationDetector(deserializationMethodName);
    }

    @Override
    public List<Prioritized<TypeDeserializer>> detect(final ResolvedType type) {
        return detectDeserializerMethods(type).stream()
                .filter(method -> method.method().getName().equals(this.deserializationMethodName))
                .map(method -> methodDeserializer((ClassType) type, method))
                .map(deserializer -> prioritized(deserializer, PRIVILEGED_FACTORY))
                .collect(toList());
    }
}
