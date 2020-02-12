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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.conventional.NewDetectorBuilder;
import de.quantummaid.mapmaid.builder.detection.NewSimpleDetector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.processing.Processor;
import de.quantummaid.mapmaid.builder.scanning.DefaultPackageScanner;
import de.quantummaid.mapmaid.builder.scanning.PackageScanner;
import de.quantummaid.mapmaid.builder.scanning.PackageScannerRecipe;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
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
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependency;
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependencyRegistry;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.duplex;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.fromDefinition;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DefaultDisambiguator.defaultDisambiguator;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators.disambiguators;
import static de.quantummaid.mapmaid.builder.resolving.fixed.unreasoned.FixedUnreasoned.fixedUnreasoned;
import static de.quantummaid.mapmaid.builder.resolving.processing.Processor.processor;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.addDeserialization;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.addSerialization;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
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
            dependency(NewSimpleDetector.class, () -> this.detector)
    );

    private final Processor processor = processor();

    private final List<Recipe> recipes = new ArrayList<>(10);
    private final Map<ResolvedType, Disambiguator> specialDisambiguators = new HashMap<>(10);

    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };

    private Map<MarshallingType, Marshaller> marshallerMap = new HashMap<>(1);
    private Map<MarshallingType, Unmarshaller> unmarshallerMap = new HashMap<>(1);
    private volatile InjectorFactory injectorFactory = InjectorFactory.emptyInjectorFactory();

    private NewSimpleDetector detector = ConventionalDetectors.conventionalDetector();

    public static MapMaidBuilder mapMaidBuilder(final String... packageNames) {
        if (packageNames != null) {
            stream(packageNames).forEach(packageName -> validateNotNull(packageName, "packageName"));
        }
        final List<String> packageNameList = Optional.ofNullable(packageNames)
                .map(Arrays::asList)
                .orElse(new ArrayList<>(10));

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

    public MapMaidBuilder withDetector(final NewDetectorBuilder detector) {
        return withDetector(detector.build());
    }

    public MapMaidBuilder withDetector(final NewSimpleDetector detector) {
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
        return withManuallyAddedType(type, capabilities, manuallyAdded());
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type,
                                                final RequiredCapabilities capabilities,
                                                final String reason) {
        return withManuallyAddedType(type, capabilities, Reason.reason(reason));
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type,
                                                final RequiredCapabilities capabilities,
                                                final Reason reason) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        if (capabilities.hasSerialization()) {
            this.processor.dispatch(addSerialization(type, reason));
        }
        if (capabilities.hasDeserialization()) {
            this.processor.dispatch(addDeserialization(type, reason));
        }
        return this;
    }


    public MapMaidBuilder withManuallyAddedType(final ResolvedType type, final BuildContextLog contextLog) {
        validateNotNull(type, "type");
        contextLog.stepInto(MapMaidBuilder.class).log(type, "added");
        return withManuallyAddedType(type);
    }

    public MapMaidBuilder withManuallyAddedType(final ResolvedType type) {
        return withManuallyAddedType(type, duplex());
    }

    public MapMaidBuilder withManuallyAddedTypes(final Class<?>... type) {
        validateNotNull(type, "type");
        stream(type).forEach(this::withManuallyAddedType);
        return this;
    }

    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition) {
        validateNotNull(definition, "definition");
        return withManuallyAddedDefinition(definition, fromDefinition(definition));
    }

    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition, // TODO
                                                      final RequiredCapabilities capabilities) {
        validateNotNull(definition, "definition");
        validateNotNull(capabilities, "capabilities");
        final ResolvedType type = definition.type();
        final Context context = emptyContext(this.processor::dispatch, type);
        definition.serializer().ifPresent(context::setSerializer);
        definition.deserializer().ifPresent(context::setDeserializer);
        final StatefulDefinition statefulDefinition = fixedUnreasoned(context);
        this.processor.addState(statefulDefinition);
        if (capabilities.hasSerialization()) {
            this.processor.dispatch(addSerialization(type, manuallyAdded()));
        }
        if (capabilities.hasDeserialization()) {
            this.processor.dispatch(addDeserialization(type, manuallyAdded()));
        }
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

    public MapMaidBuilder withDisambiguatorFor(final Class<?> type, final Disambiguator disambiguator) {
        final ResolvedType resolvedType = fromClassWithoutGenerics(type);
        return withDisambiguatorFor(resolvedType, disambiguator);
    }

    public MapMaidBuilder withDisambiguatorFor(final ResolvedType type, final Disambiguator disambiguator) {
        this.specialDisambiguators.put(type, disambiguator);
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
        this.recipes.forEach(recipe -> recipe.cook(this, this.dependencyRegistry));

        final Disambiguators disambiguators = disambiguators(defaultDisambiguator(), this.specialDisambiguators);
        final Map<ResolvedType, CollectionResult> result = this.processor.collect(this.detector, this.contextLog, disambiguators);

        final Map<ResolvedType, Definition> definitionsMap = new HashMap<>(result.size());
        final Map<ResolvedType, ScanInformation> scanInformationMap = new HashMap<>(result.size());
        result.forEach((type, collectionResult) -> {
            definitionsMap.put(type, collectionResult.definition());
            scanInformationMap.put(type, collectionResult.scanInformation());
        });

        final DebugInformation debugInformation = debugInformation(scanInformationMap);
        final Definitions definitions = definitions(this.contextLog, definitionsMap, debugInformation);

        final MarshallerRegistry<Marshaller> marshallerRegistry = marshallerRegistry(this.marshallerMap);
        final Serializer serializer = theSerializer(marshallerRegistry, definitions, CUSTOM_PRIMITIVE_MAPPINGS, debugInformation);

        final MarshallerRegistry<Unmarshaller> unmarshallerRegistry = marshallerRegistry(this.unmarshallerMap);
        final Deserializer deserializer = theDeserializer(
                unmarshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                this.validationMappings,
                this.validationErrorsMapping,
                this.injectorFactory,
                debugInformation
        );
        return mapMaid(serializer, deserializer, debugInformation);
    }
}

