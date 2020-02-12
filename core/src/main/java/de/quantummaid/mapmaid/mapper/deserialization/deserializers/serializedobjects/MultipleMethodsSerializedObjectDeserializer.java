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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects;

import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultipleMethodsSerializedObjectDeserializer implements SerializedObjectDeserializer {
    private final DeserializationFields deserializationFields;
    private final Constructor<?> constructor;
    private final Map<String, Method> methods;

    public static SerializedObjectDeserializer multipleMethodsSerializedObjectDeserializer(final Constructor<?> constructor,
                                                                                           final Map<String, ResolvedMethod> methods) {
        NotNullValidator.validateNotNull(constructor, "constructor");
        NotNullValidator.validateNotNull(methods, "methods");
        final Map<String, ResolvedType> fieldMap = methods.entrySet()
                .stream().collect(toMap(Entry::getKey, e -> e.getValue().parameters().get(0).type()));
        final DeserializationFields deserializationFields = deserializationFields(fieldMap);
        final Map<String, Method> methodMap = methods.entrySet()
                .stream().collect(toMap(Entry::getKey, e -> e.getValue().method()));
        return new MultipleMethodsSerializedObjectDeserializer(deserializationFields, constructor, methodMap);
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) throws Exception {
        final Object instance = this.constructor.newInstance();
        for (final Entry<String, Object> entry : elements.entrySet()) {
            final Method method = this.methods.get(entry.getKey());
            method.invoke(instance, entry.getValue());
        }
        return instance;
    }

    @Override
    public DeserializationFields fields() {
        return this.deserializationFields;
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException(); // TODO
    }
}
