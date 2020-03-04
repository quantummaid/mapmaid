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
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.Reason;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.processing.Processor;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.*;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.AdvancedBuilder.advancedBuilder;
import static de.quantummaid.mapmaid.builder.GenericType.genericType;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.builder.resolving.processing.Processor.processor;
import static de.quantummaid.mapmaid.builder.resolving.processing.Signal.addDeserialization;
import static de.quantummaid.mapmaid.builder.resolving.processing.Signal.addSerialization;
import static de.quantummaid.mapmaid.builder.resolving.states.fixed.unreasoned.FixedUnreasoned.fixedUnreasoned;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.theSerializer;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaidBuilder {
    private final SimpleDetector detector = ConventionalDetectors.conventionalDetector();
    private final Processor processor = processor();
    private final AdvancedBuilder advancedBuilder = advancedBuilder();
    private final List<Recipe> recipes = new ArrayList<>(10);
    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };

    public static MapMaidBuilder mapMaidBuilder() {
        return new MapMaidBuilder();
    }

    public MapMaidBuilder serializing(final Class<?> type) {
        return serializing(genericType(type));
    }

    public <T> MapMaidBuilder serializing(final Class<T> type, final SerializationOnlyType<T> customType) {
        return serializing(genericType(type), customType);
    }

    public MapMaidBuilder serializing(final GenericType<?> genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, serialization());
    }

    public <T> MapMaidBuilder serializing(final GenericType<T> genericType,
                                          final SerializationOnlyType<T> customType) {
        return withCustomType(genericType, serialization(), customType);
    }

    public MapMaidBuilder deserializing(final Class<?> type) {
        return deserializing(genericType(type));
    }

    public <T> MapMaidBuilder deserializing(final Class<T> type,
                                            final DeserializationOnlyType<T> customType) {
        return deserializing(genericType(type), customType);
    }

    public MapMaidBuilder deserializing(final GenericType<?> genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, deserialization());
    }

    public <T> MapMaidBuilder deserializing(final GenericType<T> genericType,
                                            final DeserializationOnlyType<T> customType) {
        return withCustomType(genericType, deserialization(), customType);
    }

    public MapMaidBuilder serializingAndDeserializing(final Class<?> type) {
        return serializingAndDeserializing(genericType(type));
    }

    public <T> MapMaidBuilder serializingAndDeserializing(final Class<T> type,
                                                          final DuplexType<T> customType) {
        return serializingAndDeserializing(genericType(type), customType);
    }

    public MapMaidBuilder serializingAndDeserializing(final GenericType<?> genericType) {
        final ResolvedType resolvedType = genericType.toResolvedType();
        return mapping(resolvedType, duplex());
    }

    public <T> MapMaidBuilder serializingAndDeserializing(final GenericType<T> genericType,
                                                          final DuplexType<T> customType) {
        return withCustomType(genericType, duplex(), customType);
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

    public MapMaidBuilder withManuallyAddedTypes(final Class<?>... type) {
        validateNotNull(type, "type");
        stream(type).forEach(this::mapping);
        return this;
    }

    public <T> MapMaidBuilder withCustomType(final Class<T> type,
                                             final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        return withCustomType(genericType(type), capabilities, customType);
    }

    public <T> MapMaidBuilder withCustomType(final GenericType<T> type,
                                             final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        validateNotNull(customType, "customType");
        final ResolvedType resolvedType = type.toResolvedType();
        final Optional<TypeSerializer> serializer = customType.serializer();
        if (capabilities.hasSerialization() && !serializer.isPresent()) {
            throw new IllegalArgumentException(format("serializer is missing for type '%s'", resolvedType.description()));
        }
        final Optional<TypeDeserializer> deserializer = customType.deserializer();
        if (capabilities.hasDeserialization() && !deserializer.isPresent()) {
            throw new IllegalArgumentException(format("deserializer is missing for type '%s'", resolvedType.description()));
        }
        final Context context = emptyContext(this.processor::dispatch, resolvedType);
        serializer.ifPresent(context::setSerializer);
        deserializer.ifPresent(context::setDeserializer);
        final StatefulDefinition statefulDefinition = fixedUnreasoned(context);
        this.processor.addState(statefulDefinition);
        if (capabilities.hasSerialization()) {
            this.processor.dispatch(addSerialization(resolvedType, manuallyAdded()));
        }
        if (capabilities.hasDeserialization()) {
            this.processor.dispatch(addDeserialization(resolvedType, manuallyAdded()));
        }
        return this;
    }

    public MapMaidBuilder withManuallyAddedDefinition(final Definition definition) {
        validateNotNull(definition, "definition");
        return withManuallyAddedDefinition(definition, fromDefinition(definition));
    }

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
        this.recipes.forEach(Recipe::init);
        this.recipes.forEach(recipe -> recipe.cook(this));

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
        final Serializer serializer = theSerializer(
                marshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                debugInformation
        );

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

