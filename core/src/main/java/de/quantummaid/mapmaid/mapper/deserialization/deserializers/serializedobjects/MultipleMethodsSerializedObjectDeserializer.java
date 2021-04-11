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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects;

import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.shared.identifier.RealTypeIdentifier;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import de.quantummaid.reflectmaid.Executor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultipleMethodsSerializedObjectDeserializer implements SerializedObjectDeserializer {
    private final DeserializationFields deserializationFields;
    private final ResolvedConstructor constructor;
    private final Executor constructorExecutor;
    private final Map<String, ResolvedMethod> methods;
    private final Map<String, Executor> methodExecutors;

    public static SerializedObjectDeserializer multipleMethodsSerializedObjectDeserializer(final ResolvedConstructor constructor,
                                                                                           final Map<String, ResolvedMethod> methods) {
        NotNullValidator.validateNotNull(constructor, "constructor");
        NotNullValidator.validateNotNull(methods, "methods");
        final Map<String, TypeIdentifier> fieldMap = methods.entrySet()
                .stream().collect(
                        toMap(
                                Entry::getKey,
                                e -> RealTypeIdentifier.realTypeIdentifier(e.getValue().getParameters().get(0).getType())));
        final DeserializationFields deserializationFields = deserializationFields(fieldMap);
        final Map<String, Executor> methodExecutors = new LinkedHashMap<>(methods.size());
        methods.forEach((name, method) -> methodExecutors.put(name, method.createExecutor()));
        final Executor constructorExecutor = constructor.createExecutor();
        return new MultipleMethodsSerializedObjectDeserializer(deserializationFields, constructor, constructorExecutor, methods, methodExecutors);
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) throws Exception {
        final Object instance = constructorExecutor.execute(null, List.of());
        for (final Entry<String, Object> entry : elements.entrySet()) {
            final Executor method = this.methodExecutors.get(entry.getKey());
            method.execute(instance, List.of(entry.getValue()));
        }
        return instance;
    }

    @Override
    public DeserializationFields fields() {
        return this.deserializationFields;
    }

    @Override
    public String description() {
        final String joinedMethods = methods.values().stream()
                .map(ResolvedMethod::describe)
                .collect(joining(", ", "[", "]"));
        return "as serialized object by calling the constructor '" +
                constructor.describe() + "' and then calling the methods: " + joinedMethods;
    }
}
