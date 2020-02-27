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
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.builder.resolving.StatefulDefinition;
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
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.AdvancedBuilder.advancedBuilder;
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependency;
import static de.quantummaid.mapmaid.builder.DependencyRegistry.dependencyRegistry;
import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.builder.resolving.fixed.unreasoned.FixedUnreasoned.fixedUnreasoned;
import static de.quantummaid.mapmaid.builder.resolving.processing.Processor.processor;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.addDeserialization;
import static de.quantummaid.mapmaid.builder.resolving.signals.Signal.addSerialization;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.theSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Arrays.stream;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidBuilder {
    private final SimpleDetector detector = ConventionalDetectors.conventionalDetector();
    private final DependencyRegistry dependencyRegistry = dependencyRegistry(
            dependency(SimpleDetector.class, () -> this.detector)
    );

    private final Processor processor = processor();
    private final AdvancedBuilder advancedBuilder = advancedBuilder();

    private final List<Recipe> recipes = new ArrayList<>(10);

    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };

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

    public MapMaidBuilder serializingType(final Class<?> type) {
        return serializingType(genericType(type));
    }

    public MapMaidBuilder serializingType(final GenericType genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, serialization());
    }

    public MapMaidBuilder deserializingType(final Class<?> type) {
        return deserializingType(genericType(type));
    }

    public MapMaidBuilder deserializingType(final GenericType genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, deserialization());
    }

    public MapMaidBuilder serializingAndDeserializingType(final Class<?> type) {
        return serializingAndDeserializingType(genericType(type));
    }

    public MapMaidBuilder serializingAndDeserializingType(final GenericType genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, duplex());
    }

    public MapMaidBuilder mapping(final Class<?> type) {
        return mapping(fromClassWithoutGenerics(type));
    }

    public MapMaidBuilder mapping(final Class<?> type,
                                  final RequiredCapabilities capabilities) {
        return mapping(fromClassWithoutGenerics(type), capabilities);
    }

    public MapMaidBuilder mapping(final ResolvedType type,
                                  final RequiredCapabilities capabilities) {
        return mapping(type, capabilities, manuallyAdded());
    }

    public MapMaidBuilder mapping(final ResolvedType type,
                                  final RequiredCapabilities capabilities,
                                  final String reason) {
        return mapping(type, capabilities, Reason.reason(reason));
    }

    public MapMaidBuilder mapping(final Class<?> type,
                                  final RequiredCapabilities capabilities,
                                  final Reason reason) {
        return mapping(fromClassWithoutGenerics(type), capabilities, reason);
    }

    public MapMaidBuilder mapping(final ResolvedType type,
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

    public MapMaidBuilder mapping(final ResolvedType type) {
        return mapping(type, duplex());
    }

    // TODO umbenennen nach mapping
    public MapMaidBuilder withManuallyAddedTypes(final Class<?>... type) {
        validateNotNull(type, "type");
        stream(type).forEach(this::mapping);
        return this;
    }

    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition) {
        validateNotNull(definition, "definition");
        return withManuallyAddedDefinition(definition, fromDefinition(definition));
    }

    // TODO "definition"
    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition,
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

    // TODO merge with other
    @SuppressWarnings("unchecked")
    public <T extends Throwable> MapMaidBuilder withExceptionIndicatingMultipleValidationErrors(
            final Class<T> exceptionType,
            final ExceptionMappingList<T> mapping) {
        validateNotNull(exceptionType, "exceptionType");
        validateNotNull(mapping, "mapping");
        this.validationMappings.putOneToMany(exceptionType, (ExceptionMappingList<Throwable>) mapping);
        return this;
    }

    public MapMaidBuilder withAdvancedSettings(final Consumer<AdvancedBuilder> configurator) {
        configurator.accept(this.advancedBuilder);
        return this;
    }

    public MapMaid build() {
        this.recipes.forEach(recipe -> recipe.init(this.dependencyRegistry));
        this.recipes.forEach(recipe -> recipe.cook(this, this.dependencyRegistry));

        final Disambiguators disambiguators = this.advancedBuilder.buildDisambiguators();
        final Map<ResolvedType, CollectionResult> result = this.processor.collect(this.detector, disambiguators);

        final Map<ResolvedType, Definition> definitionsMap = new HashMap<>(result.size());
        final Map<ResolvedType, ScanInformation> scanInformationMap = new HashMap<>(result.size());
        result.forEach((type, collectionResult) -> {
            definitionsMap.put(type, collectionResult.definition());
            scanInformationMap.put(type, collectionResult.scanInformation());
        });

        final DebugInformation debugInformation = debugInformation(scanInformationMap);
        final Definitions definitions = definitions(definitionsMap, debugInformation);

        final MarshallerRegistry<Marshaller> marshallerRegistry = this.advancedBuilder.buildMarshallerRegistry();
        final Serializer serializer = theSerializer(marshallerRegistry, definitions, CUSTOM_PRIMITIVE_MAPPINGS, debugInformation);

        final MarshallerRegistry<Unmarshaller> unmarshallerRegistry = this.advancedBuilder.buildUnmarshallerRegistry();
        final Deserializer deserializer = theDeserializer(
                unmarshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                this.validationMappings,
                this.validationErrorsMapping,
                debugInformation
        );
        return mapMaid(serializer, deserializer, debugInformation);
    }
}

