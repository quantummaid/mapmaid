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
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer.createDescription;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.IncompatibleSerializedObjectException.incompatibleSerializedObjectException;
import static de.quantummaid.mapmaid.shared.identifier.RealTypeIdentifier.realTypeIdentifier;
import static java.lang.reflect.Modifier.isAbstract;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstructorSerializedObjectDeserializer implements SerializedObjectDeserializer {
    private final DeserializationFields fields;
    private final ResolvedConstructor factoryConstructor;
    private final List<String> parameterNames;

    public static SerializedObjectDeserializer createDeserializer(final ClassType type,
                                                                  final ResolvedConstructor deserializationConstructor) {
        validateDeserializerModifiers(type, deserializationConstructor);
        return verifiedDeserializationDTOConstructor(deserializationConstructor);
    }

    private static ConstructorSerializedObjectDeserializer verifiedDeserializationDTOConstructor(
            final ResolvedConstructor factoryConstructor) {
        final List<ResolvedParameter> parameters = factoryConstructor.parameters();
        final List<String> parameterNames = parameters.stream()
                .map(ResolvedParameter::parameter)
                .map(Parameter::getName)
                .collect(toList());
        final Map<String, TypeIdentifier> parameterFields = parameters.stream()
                .collect(toMap(
                        ResolvedParameter::name,
                        parameter -> realTypeIdentifier(parameter.type())
                ));
        return new ConstructorSerializedObjectDeserializer(deserializationFields(parameterFields), factoryConstructor, parameterNames);
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) throws Exception {
        final Object[] arguments = new Object[this.parameterNames.size()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = elements.get(this.parameterNames.get(i));
        }
        return this.factoryConstructor.constructor().newInstance(arguments);
    }

    @Override
    public DeserializationFields fields() {
        return this.fields;
    }

    private static void validateDeserializerModifiers(final ClassType type, final ResolvedConstructor deserializationConstructor) {
        final int deserializationMethodModifiers = deserializationConstructor.constructor().getModifiers();
        if (isAbstract(deserializationMethodModifiers)) {
            throw incompatibleSerializedObjectException(
                    "The deserialization constructor %s configured for the SerializedObject of type %s must not be abstract",
                    deserializationConstructor, type);
        }
        if (deserializationConstructor.constructor().getDeclaringClass() != type.assignableType()) {
            throw incompatibleSerializedObjectException(
                    "The deserialization constructor %s configured for the SerializedObject of type %s must return the DTO",
                    deserializationConstructor, type);
        }
    }

    @Override
    public String description() {
        return createDescription(this.factoryConstructor.describe());
    }

    public ResolvedConstructor constructor() {
        return this.factoryConstructor;
    }
}
