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
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.Executor;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer.createDescription;
import static de.quantummaid.mapmaid.builder.resolving.framework.identifier.RealTypeIdentifier.realTypeIdentifier;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodSerializedObjectDeserializer implements SerializedObjectDeserializer {
    private final DeserializationFields fields;
    private final ResolvedMethod factoryMethod;
    private final Executor executor;
    private final List<String> parameterNames;

    public static SerializedObjectDeserializer methodDeserializer(final ClassType type,
                                                                  final ResolvedMethod deserializationMethod) {
        validateDeserializerModifiers(type, deserializationMethod);
        return verifiedDeserializationDTOMethod(deserializationMethod);
    }

    private static MethodSerializedObjectDeserializer verifiedDeserializationDTOMethod(final ResolvedMethod factoryMethod) {
        final List<ResolvedParameter> parameters = factoryMethod.getParameters();
        final List<String> parameterNames = parameters.stream()
                .map(ResolvedParameter::getParameter)
                .map(Parameter::getName)
                .collect(toList());
        final Map<String, TypeIdentifier> parameterFields = parameters.stream()
                .collect(Collectors.toMap(
                        ResolvedParameter::name,
                        resolvedParameter -> realTypeIdentifier(resolvedParameter.getType())
                ));
        final Executor executor = factoryMethod.createExecutor();
        return new MethodSerializedObjectDeserializer(deserializationFields(parameterFields), factoryMethod, executor, parameterNames);
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) throws Exception {
        return Util.deserialize(elements, parameterNames, executor);
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
        final int deserializationMethodModifiers = deserializationMethod.getMethod().getModifiers();

        if (!isStatic(deserializationMethodModifiers)) {
            throw mapMaidException(format("The deserialization method %s configured for the object " + // NOSONAR
                    "of type %s must be static", deserializationMethod, type));
        }
        if (isAbstract(deserializationMethodModifiers)) {
            throw mapMaidException(format("The deserialization method %s configured for the object " + // NOSONAR
                    "of type %s must not be abstract", deserializationMethod, type));
        }
        if (!deserializationMethod.returnType().map(type::equals).orElse(false)) {
            throw mapMaidException(format("The deserialization method %s configured for the object " + // NOSONAR
                    "of type %s must return the DTO", deserializationMethod, type));
        }
    }
}
