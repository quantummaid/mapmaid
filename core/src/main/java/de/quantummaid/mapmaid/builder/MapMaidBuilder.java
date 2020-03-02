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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories;
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.conventional.DetectorBuilder;
import de.quantummaid.mapmaid.builder.detection.Detector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.scanning.DefaultPackageScanner;
import de.quantummaid.mapmaid.builder.scanning.PackageScanner;
import de.quantummaid.mapmaid.builder.scanning.PackageScannerRecipe;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.*;
import de.quantummaid.mapmaid.mapper.injector.InjectorFactory;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.*;

import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.DefinitionsBuilder.definitionsBuilder;
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependency;
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependencyRegistry;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.all;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.injector.InjectorFactory.injectorFactory;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry.marshallerRegistry;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.theSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Arrays.stream;

public final class MapMaidBuilder {
    private final BuildContextLog contextLog = BuildContextLog.emptyLog();
    private final DependencyRegistry dependencyRegistry = dependencyRegistry(
            dependency(Detector.class, () -> this.detector)
    );
    private final List<Definition> addedDefinitions = new LinkedList<>();
    private final List<Recipe> recipes = new LinkedList<>();
    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };
    private Map<MarshallingType, Marshaller> marshallerMap = new HashMap<>(1);
    private Map<MarshallingType, Unmarshaller> unmarshallerMap = new HashMap<>(1);
    private volatile InjectorFactory injectorFactory = InjectorFactory.emptyInjectorFactory();
    private Detector detector = ConventionalDetectors.conventionalDetector();

    public static MapMaidBuilder mapMaidBuilder(final String... packageNames) {
        if (packageNames != null) {
            stream(packageNames).forEach(packageName -> validateNotNull(packageName, "packageName"));
        }
        final List<String> packageNameList = Optional.ofNullable(packageNames)
                .map(Arrays::asList)
                .orElse(new LinkedList<>());

        if (packageNameList.isEmpty()) {
            return mapMaidBuilder(List::of);
        } else {
            final PackageScanner packageScanner = DefaultPackageScanner.defaultPackageScanner(packageNameList);
            return mapMaidBuilder(packageScanner);
        }
    }

    public static MapMaidBuilder mapMaidBuilder(final PackageScanner packageScanner) {
        validateNotNull(packageScanner, "packageScanner");
        return new MapMaidBuilder().usingRecipe(PackageScannerRecipe.packageScannerRecipe(packageScanner));
    }

    public MapMaidBuilder withDetector(final DetectorBuilder detector) {
        return withDetector(detector.build());
    }

    public MapMaidBuilder withDetector(final Detector detector) {
        this.detector = detector;
        return this;
    }

    public MapMaidBuilder withManuallyAddedType(final Class<?> type) {
        return withManuallyAddedType(fromClassWithoutGenerics(type), this.contextLog);
    }

    public MapMaidBuilder withManuallyAddedType(final Class<?> type,
                                                final RequiredCapabilities capabilities) {
        return withManuallyAddedType(fromClassWithoutGenerics(type), capabilities);
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type,
                                                final RequiredCapabilities capabilities) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        final Definition definition = this.detector.detect(type, capabilities, this.contextLog).orElseThrow();
        return withManuallyAddedDefinition(definition);
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type, final BuildContextLog contextLog) {
        validateNotNull(type, "type");
        contextLog.stepInto(MapMaidBuilder.class).log(type, "added");
        return withManuallyAddedType(type);
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type) {
        return withManuallyAddedType(type, all());
    }

    public MapMaidBuilder withManuallyAddedTypes(final Class<?>... type) {
        validateNotNull(type, "type");
        stream(type).forEach(this::withManuallyAddedType);
        return this;
    }

    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition) {
        validateNotNull(definition, "definition");
        this.addedDefinitions.add(definition);
        return this;
    }

    public MapMaidBuilder usingJsonMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "jsonMarshaller");
        validateNotNull(unmarshaller, "jsonUnmarshaller");
        return usingMarshaller(MarshallingType.json(), marshaller, unmarshaller);
    }

    public MapMaidBuilder usingYamlMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "yamlMarshaller");
        validateNotNull(unmarshaller, "yamlUnmarshaller");
        return usingMarshaller(MarshallingType.yaml(), marshaller, unmarshaller);
    }

    public MapMaidBuilder usingXmlMarshaller(final Marshaller marshaller, final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "xmlMarshaller");
        validateNotNull(unmarshaller, "xmlUnmarshaller");
        return usingMarshaller(MarshallingType.xml(), marshaller, unmarshaller);
    }

    public MapMaidBuilder usingMarshaller(final MarshallingType marshallingType,
                                          final Marshaller marshaller,
                                          final Unmarshaller unmarshaller) {
        validateNotNull(marshaller, "marshaller");
        validateNotNull(unmarshaller, "unmarshaller");
        validateNotNull(marshallingType, "marshallingType");
        this.marshallerMap.put(marshallingType, marshaller);
        this.unmarshallerMap.put(marshallingType, unmarshaller);
        return this;
    }

    public MapMaidBuilder usingMarshaller(final Map<MarshallingType, Marshaller> marshallerMap,
                                          final Map<MarshallingType, Unmarshaller> unmarshallerMap) {
        this.marshallerMap = new HashMap<>(marshallerMap);
        this.unmarshallerMap = new HashMap<>(unmarshallerMap);
        return this;
    }

    public MapMaidBuilder usingInjectorFactory(final InjectorLambda factory) {
        this.injectorFactory = injectorFactory(factory);
        return this;
    }

    public MapMaidBuilder usingRecipe(final Recipe recipe) {
        this.recipes.add(recipe);
        return this;
    }

    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingValidationError(
            final Class<T> exceptionIndicatingValidationError) {
        return this.withExceptionIndicatingValidationError(
                exceptionIndicatingValidationError,
                (exception, propertyPath) -> new ValidationError(exception.getMessage(), propertyPath));
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingValidationError(
            final Class<T> exceptionIndicatingValidationError,
            final ExceptionMappingWithPropertyPath<T> exceptionMapping) {
        this.validationMappings.putOneToOne(exceptionIndicatingValidationError,
                (ExceptionMappingWithPropertyPath<Throwable>) exceptionMapping);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingMultipleValidationErrors(
            final Class<T> exceptionType,
            final ExceptionMappingList<T> mapping) {
        validateNotNull(exceptionType, "exceptionType");
        validateNotNull(mapping, "mapping");
        this.validationMappings.putOneToMany(exceptionType, (ExceptionMappingList<Throwable>) mapping);
        return this;
    }

    public BuildContextLog contextLog() {
        return this.contextLog;
    }

    public MapMaid build() {
        this.recipes.forEach(recipe -> recipe.init(this.dependencyRegistry));

        this.recipes.forEach(recipe -> {
            recipe.cook(this, this.dependencyRegistry);
        });

        final DefinitionsBuilder definitionsBuilder = definitionsBuilder(this.detector, this.contextLog);

        this.addedDefinitions.forEach(definition -> {
            final ResolvedType type = definition.type();
            definition.serializer().ifPresent(serializer -> definitionsBuilder.addSerializer(type, serializer));
            definition.deserializer().ifPresent(deserializer -> definitionsBuilder.addDeserializer(type, deserializer));
        });

        definitionsBuilder.resolveRecursively(this.detector);
        final Definitions definitions = definitionsBuilder.build();

        final MarshallerRegistry<Marshaller> marshallerRegistry = marshallerRegistry(this.marshallerMap);
        final Serializer serializer = theSerializer(marshallerRegistry, definitions, ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS);

        final MarshallerRegistry<Unmarshaller> unmarshallerRegistry = marshallerRegistry(this.unmarshallerMap);
        final Deserializer deserializer = theDeserializer(
                unmarshallerRegistry,
                definitions,
                ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS,
                this.validationMappings,
                this.validationErrorsMapping,
                this.injectorFactory
        );
        return mapMaid(serializer, deserializer);
    }
}

