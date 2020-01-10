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

package de.quantummaid.mapmaid.builder.recipes.scanner;

import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.Detector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedMethod;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedParameter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serializationOnly;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassScannerRecipe implements Recipe {
    private static final List<String> OBJECT_METHODS = stream(Object.class.getMethods())
            .map(Method::getName)
            .collect(toList());

    private final List<Class<?>> classes;

    public static ClassScannerRecipe addAllReferencedClassesIn(final Class<?>... classes) {
        validateNotNull(classes, "classes");
        return new ClassScannerRecipe(asList(classes));
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        final Detector detector = dependencyRegistry.getDependency(Detector.class);
        this.classes.forEach(clazz -> addReferencesIn(clazz, mapMaidBuilder, detector));
    }

    private static void addReferencesIn(final Class<?> clazz, final MapMaidBuilder builder, final Detector detector) {
        final ClassType fullType = fromClassWithoutGenerics(clazz);
        final BuildContextLog contextLog = builder.contextLog().stepInto(ClassScannerRecipe.class);
        final List<ResolvedMethod> publicMethods = fullType.publicMethods();

        for (final ResolvedMethod method : publicMethods) {
            if (!OBJECT_METHODS.contains(method.method().getName())) {
                final List<ResolvedType> offenders = new LinkedList<>();
                final List<? extends Definition> parameterDefinitions = method.parameters().stream()
                        .map(ResolvedParameter::type)
                        .collect(toList()).stream()
                        .map(type -> toDefinition(type, deserializationOnly(), detector, offenders, contextLog))
                        .flatMap(Optional::stream)
                        .collect(toList());
                final Optional<? extends Definition> returnDefinition = method.returnType()
                        .flatMap(type -> toDefinition(type, serializationOnly(), detector, offenders, contextLog));

                if (offenders.isEmpty()) {
                    returnDefinition.ifPresent(definition -> {
                        contextLog.log(definition.type(), "added because return type of method " + method.method().toString());
                        builder.withManuallyAddedDefinition(definition);
                    });
                    parameterDefinitions.forEach(definition -> {
                        contextLog.log(definition.type(), "added because parameter type of method " + method.method().toString());
                        builder.withManuallyAddedDefinition(definition);
                    });
                } else {
                    final String offendersString = offendersString(offenders);
                    returnDefinition.ifPresent(definition -> contextLog.log(definition.type(), format(
                            "not added as return type of method %s because types not supported: %s",
                            method.method().toString(), offendersString)));

                    parameterDefinitions.forEach(definition -> contextLog.log(definition.type(), format(
                            "not added as parameter type of method %stypes not supported: %s",
                            method.method().toString(), offendersString)));
                }
            }
        }
    }

    private static Optional<? extends Definition> toDefinition(final ResolvedType type,
                                                               final RequiredCapabilities requiredCapabilities,
                                                               final Detector detector,
                                                               final List<ResolvedType> offenders,
                                                               final BuildContextLog contextLog) {
        final Optional<? extends Definition> definition = detector.detect(type, requiredCapabilities, contextLog);
        if (definition.isEmpty()) {
            offenders.add(type);
        }
        return definition;
    }

    private static String offendersString(final List<ResolvedType> offenders) {
        return offenders.stream()
                .map(ResolvedType::description)
                .collect(joining(", ", "[", "]"));
    }
}
