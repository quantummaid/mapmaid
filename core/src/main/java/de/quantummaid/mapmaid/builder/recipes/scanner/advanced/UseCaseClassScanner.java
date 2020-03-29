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

package de.quantummaid.mapmaid.builder.recipes.scanner.advanced;

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.recipes.scanner.advanced.deserialization_wrappers.MethodParameterDeserializationWrapper;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ClassType;
import de.quantummaid.reflectmaid.ResolvedType;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;
import de.quantummaid.reflectmaid.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.GenericType.fromResolvedType;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType.deserializationOnlyType;
import static de.quantummaid.mapmaid.builder.recipes.scanner.advanced.VirtualDeserializer.virtualDeserializerFor;
import static de.quantummaid.mapmaid.builder.recipes.scanner.advanced.deserialization_wrappers.MultipleParametersDeserializationWrapper.multipleParamters;
import static de.quantummaid.mapmaid.builder.recipes.scanner.advanced.deserialization_wrappers.SingleParameterDeserializationWrapper.singleParameter;
import static de.quantummaid.mapmaid.builder.recipes.scanner.advanced.deserialization_wrappers.ZeroParametersDeserializationWrapper.zeroParameters;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.virtualTypeIdentifier;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.ClassType.fromClassWithoutGenerics;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UseCaseClassScanner {
    private static final List<String> OBJECT_METHODS = stream(Object.class.getMethods())
            .map(Method::getName)
            .collect(toList());

    private final Collection<Class<?>> classes;

    public static Map<Class<?>, MethodParameterDeserializationWrapper> addAllReferencedClassesIn(final MapMaidBuilder builder,
                                                                                                 final Class<?>... useCaseClasses) {
        validateNotNull(builder, "builder");
        validateNotNull(useCaseClasses, "useCaseClasses");
        return addAllReferencedClassesIn(asList(useCaseClasses), builder);
    }

    public static Map<Class<?>, MethodParameterDeserializationWrapper> addAllReferencedClassesIn(final Collection<Class<?>> useCaseClasses,
                                                                                                 final MapMaidBuilder builder) {
        validateNotNull(useCaseClasses, "useCaseClasses");
        validateNotNull(builder, "builder");
        final Map<Class<?>, MethodParameterDeserializationWrapper> deserializationWrappers = new HashMap<>(useCaseClasses.size());
        useCaseClasses.forEach(useCaseClass -> {
            final MethodParameterDeserializationWrapper deserializationWrapper = addReferencesIn(useCaseClass, builder);
            deserializationWrappers.put(useCaseClass, deserializationWrapper);
        });
        return deserializationWrappers;
    }

    private static MethodParameterDeserializationWrapper addReferencesIn(final Class<?> useCaseClass,
                                                                         final MapMaidBuilder builder) {
        final ClassType fullType = fromClassWithoutGenerics(useCaseClass);
        final List<ResolvedMethod> relevantMethods = fullType.methods().stream()
                .filter(ResolvedMethod::isPublic)
                .filter(method -> !OBJECT_METHODS.contains(method.name()))
                .collect(toUnmodifiableList());
        if (relevantMethods.size() != 1) {
            throw new UnsupportedOperationException(format(
                    "Unable to to determine the single use case method of '%s'", fullType.description())
            );
        }
        final ResolvedMethod useCaseMethod = relevantMethods.get(0);
        return addMethod(useCaseMethod, builder);
    }

    private static MethodParameterDeserializationWrapper addMethod(final ResolvedMethod method,
                                                                   final MapMaidBuilder builder) {
        final List<ResolvedParameter> parameters = method.parameters();
        parameters.stream()
                .map(ResolvedParameter::type)
                .map(GenericType::fromResolvedType)
                .forEach(type -> builder.withType(
                        type, deserialization(), format("because parameter type of method %s", method.describe())));

        method.returnType().ifPresent(type -> {
            final GenericType<?> genericType = fromResolvedType(type);
            builder.withType(
                    genericType,
                    serialization(),
                    format("because return type of method %s", method.describe()));
        });

        if (parameters.isEmpty()) {
            return zeroParameters();
        } else if (parameters.size() == 1) {
            final ResolvedParameter parameter = parameters.get(0);
            final String name = parameter.name();
            final ResolvedType type = parameter.type();
            return singleParameter(name, type);
        } else {
            final DeserializationOnlyType<?> virtualType = createVirtualObjectFor(method);
            builder.deserializing(virtualType);
            return multipleParamters(virtualType.type());
        }
    }

    private static DeserializationOnlyType<?> createVirtualObjectFor(final ResolvedMethod method) {
        final TypeIdentifier typeIdentifier = virtualTypeIdentifier(method.describe());
        final TypeDeserializer deserializer = virtualDeserializerFor(method);
        return deserializationOnlyType(typeIdentifier, deserializer);
    }
}
