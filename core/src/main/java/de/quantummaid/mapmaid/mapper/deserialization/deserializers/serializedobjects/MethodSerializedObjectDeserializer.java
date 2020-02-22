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
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedField;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer.createDescription;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.IncompatibleSerializedObjectException.incompatibleSerializedObjectException;
import static java.lang.reflect.Modifier.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodSerializedObjectDeserializer implements SerializedObjectDeserializer {
    private final DeserializationFields fields;
    private final ResolvedMethod factoryMethod;
    private final List<String> parameterNames;

    public static TypeDeserializer methodNameDeserializer(final ClassType type,
                                                          final String methodName,
                                                          final List<ResolvedField> requiredFields) {
        final List<ResolvedType> parameterTypes = requiredFields.stream()
                .map(ResolvedField::type)
                .collect(toList());
        final List<ResolvedMethod> resolvedMethods = ResolvedMethod.resolvePublicMethodsWithResolvableTypeVariables(type);
        final ResolvedMethod method = resolvedMethods.stream()
                .filter(resolvedMethod -> resolvedMethod.method().getName().equals(methodName))
                .filter(resolvedMethod -> resolvedMethod.hasParameters(parameterTypes))
                .findFirst()
                .orElseThrow(() -> incompatibleSerializedObjectException(
                        "Could not find method %s with parameters of types %s", methodName, parameterTypes.stream()
                                .map(ResolvedType::description)
                                .collect(joining(","))));
        return methodDeserializer(type, method);
    }

    public static SerializedObjectDeserializer methodDeserializer(final ClassType type,
                                                      final ResolvedMethod deserializationMethod) {
        validateDeserializerModifiers(type, deserializationMethod);
        return verifiedDeserializationDTOMethod(deserializationMethod);
    }

    private static MethodSerializedObjectDeserializer verifiedDeserializationDTOMethod(final ResolvedMethod factoryMethod) {
        final List<ResolvedParameter> parameters = factoryMethod.parameters();
        final List<String> parameterNames = parameters.stream()
                .map(ResolvedParameter::parameter)
                .map(Parameter::getName)
                .collect(toList());
        final Map<String, ResolvedType> parameterFields = parameters.stream()
                .collect(Collectors.toMap(
                        resolvedParameter -> resolvedParameter.parameter().getName(),
                        ResolvedParameter::type
                ));
        return new MethodSerializedObjectDeserializer(deserializationFields(parameterFields), factoryMethod, parameterNames);
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) throws Exception {
        final Object[] arguments = new Object[this.parameterNames.size()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = elements.get(this.parameterNames.get(i));
        }
        return this.factoryMethod.method().invoke(null, arguments);
    }

    @Override
    public DeserializationFields fields() {
        return this.fields;
    }

    @Override
    public String description() {
        return createDescription(this.factoryMethod.describe());
    }

    public ResolvedMethod method() {
        return this.factoryMethod;
    }

    private static void validateDeserializerModifiers(final ClassType type, final ResolvedMethod deserializationMethod) {
        final int deserializationMethodModifiers = deserializationMethod.method().getModifiers();

        if (!isPublic(deserializationMethodModifiers)) {
            throw incompatibleSerializedObjectException(
                    "The deserialization method %s configured for the SerializedObject of type %s must be public",
                    deserializationMethod, type);
        }
        if (!isStatic(deserializationMethodModifiers)) {
            throw incompatibleSerializedObjectException(
                    "The deserialization method %s configured for the SerializedObject of type %s must be static",
                    deserializationMethod, type);
        }
        if (isAbstract(deserializationMethodModifiers)) {
            throw incompatibleSerializedObjectException(
                    "The deserialization method %s configured for the SerializedObject of type %s must not be abstract",
                    deserializationMethod, type);
        }
        if (!deserializationMethod.returnType().map(type::equals).orElse(false)) {
            throw incompatibleSerializedObjectException(
                    "The deserialization method %s configured for the SerializedObject of type %s must return the DTO",
                    deserializationMethod, type);
        }
    }
}
