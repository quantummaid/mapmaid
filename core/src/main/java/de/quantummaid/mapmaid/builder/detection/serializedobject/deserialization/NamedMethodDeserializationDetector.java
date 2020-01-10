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

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.toList;

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
    public Optional<SerializedObjectDeserializer> detect(final ClassType type, final SerializationFields fields) {
        final List<ResolvedMethod> resolvedMethods = Common.detectDeserializerMethods(type);
        final List<ResolvedMethod> deserializerMethods = resolvedMethods.stream()
                .filter(method -> method.method().getName().equals(this.deserializationMethodName))
                .collect(toList());
        return Common.findMatchingMethod(fields.typesList(), deserializerMethods, ResolvedMethod::parameters)
                .map(method -> MethodSerializedObjectDeserializer.methodDeserializer(type, method));
    }
}
