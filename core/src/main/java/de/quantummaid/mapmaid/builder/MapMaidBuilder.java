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
import de.quantummaid.mapmaid.builder.injection.FixedInjector;
import de.quantummaid.mapmaid.builder.injection.InjectionDeserializer;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.processing.Processor;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
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
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.AdvancedBuilder.advancedBuilder;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.injection.FixedInjectionDeserializer.diDeserializer;
import static de.quantummaid.mapmaid.builder.injection.InjectionDeserializer.injectionDeserializer;
import static de.quantummaid.mapmaid.builder.injection.InjectionSerializer.injectionSerializer;
import static de.quantummaid.mapmaid.builder.resolving.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.processing.Processor.processor;
import static de.quantummaid.mapmaid.builder.resolving.processing.Signal.addDeserialization;
import static de.quantummaid.mapmaid.builder.resolving.processing.Signal.addSerialization;
import static de.quantummaid.mapmaid.builder.resolving.states.fixed.unreasoned.FixedUnreasoned.fixedUnreasoned;
import static de.quantummaid.mapmaid.builder.resolving.states.injecting.InjectedDefinition.injectedDefinition;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.debug.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.debug.Reason.reason;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.theSerializer;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class MapMaidBuilder {
    private final SimpleDetector detector = ConventionalDetectors.conventionalDetector();
    private final Processor processor = processor();
    private final AdvancedBuilder advancedBuilder = advancedBuilder();
    private final List<Recipe> recipes = smallList();
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

    public MapMaidBuilder serializing(final GenericType<?> genericType) {
        return withType(genericType, serialization());
    }

    public <T> MapMaidBuilder serializing(final SerializationOnlyType<T> customType) {
        return serializing((CustomType<T>) customType);
    }

    public <T> MapMaidBuilder serializing(final CustomType<T> customType) {
        return withCustomType(serialization(), customType);
    }

    public MapMaidBuilder deserializing(final Class<?> type) {
        return deserializing(genericType(type));
    }

    public MapMaidBuilder deserializing(final GenericType<?> genericType) {
        return withType(genericType, deserialization());
    }

    public <T> MapMaidBuilder deserializing(final DeserializationOnlyType<T> customType) {
        return deserializing((CustomType<T>) customType);
    }

    public <T> MapMaidBuilder deserializing(final CustomType<T> customType) {
        return withCustomType(deserialization(), customType);
    }

    public MapMaidBuilder serializingAndDeserializing(final Class<?> type) {
        return serializingAndDeserializing(genericType(type));
    }

    public MapMaidBuilder serializingAndDeserializing(final GenericType<?> genericType) {
        return withType(genericType, duplex());
    }

    public <T> MapMaidBuilder serializingAndDeserializing(final DuplexType<T> customType) {
        return serializingAndDeserializing((CustomType<T>) customType);
    }

    public <T> MapMaidBuilder serializingAndDeserializing(final CustomType<T> customType) {
        return withCustomType(duplex(), customType);
    }

    public MapMaidBuilder injecting(final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return injecting(genericType);
    }

    public MapMaidBuilder injecting(final GenericType<?> genericType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(genericType);
        return injecting(typeIdentifier);
    }

    public MapMaidBuilder injecting(final TypeIdentifier typeIdentifier) {
        final InjectionDeserializer deserializer = injectionDeserializer(typeIdentifier);
        return injecting(typeIdentifier, deserializer);
    }

    public <T> MapMaidBuilder injecting(final Class<T> type, final FixedInjector<T> injector) {
        final GenericType<T> genericType = genericType(type);
        return injecting(genericType, injector);
    }

    public <T> MapMaidBuilder injecting(final GenericType<T> genericType, final FixedInjector<T> injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(genericType);
        return injecting(typeIdentifier, injector);
    }

    public MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final FixedInjector<?> injector) {
        final TypeDeserializer deserializer = diDeserializer(injector);
        return injecting(typeIdentifier, deserializer);
    }

    private MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final TypeDeserializer deserializer) {
        final Context context = emptyContext(this.processor::dispatch, typeIdentifier);
        final TypeSerializer serializer = injectionSerializer(typeIdentifier);
        context.setSerializer(serializer);
        context.setDeserializer(deserializer);
        final StatefulDefinition statefulDefinition = injectedDefinition(context);
        this.processor.addState(statefulDefinition);
        this.processor.dispatch(addSerialization(typeIdentifier, manuallyAdded()));
        this.processor.dispatch(addDeserialization(typeIdentifier, manuallyAdded()));
        return this;
    }

    public MapMaidBuilder withType(final Class<?> type,
                                   final RequiredCapabilities capabilities) {
        return withType(genericType(type), capabilities);
    }

    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities) {
        return withType(type, capabilities, manuallyAdded());
    }

    public MapMaidBuilder withType(final Class<?> type,
                                   final RequiredCapabilities capabilities,
                                   final String reason) {
        return withType(type, capabilities, reason(reason));
    }

    public MapMaidBuilder withType(final Class<?> type,
                                   final RequiredCapabilities capabilities,
                                   final Reason reason) {
        return withType(genericType(type), capabilities, reason);
    }

    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities,
                                   final String reason) {
        return withType(type, capabilities, reason(reason));
    }

    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities,
                                   final Reason reason) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        validateNotNull(reason, "reason");
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        if (capabilities.hasSerialization()) {
            this.processor.dispatch(addSerialization(typeIdentifier, reason));
        }
        if (capabilities.hasDeserialization()) {
            this.processor.dispatch(addDeserialization(typeIdentifier, reason));
        }
        return this;
    }

    public <T> MapMaidBuilder withCustomType(final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        validateNotNull(capabilities, "capabilities");
        validateNotNull(customType, "customType");
        final TypeIdentifier typeIdentifier = customType.type();
        final Optional<TypeSerializer> serializer = customType.serializer();
        if (capabilities.hasSerialization() && serializer.isEmpty()) {
            throw new IllegalArgumentException(format(
                    "serializer is missing for type '%s'", typeIdentifier.description()));
        }
        final Optional<TypeDeserializer> deserializer = customType.deserializer();
        if (capabilities.hasDeserialization() && deserializer.isEmpty()) {
            throw new IllegalArgumentException(format("deserializer is missing for type '%s'",
                    typeIdentifier.description()));
        }
        final Context context = emptyContext(this.processor::dispatch, typeIdentifier);
        serializer.ifPresent(context::setSerializer);
        deserializer.ifPresent(context::setDeserializer);
        final StatefulDefinition statefulDefinition = fixedUnreasoned(context);
        this.processor.addState(statefulDefinition);
        if (capabilities.hasSerialization()) {
            this.processor.dispatch(addSerialization(typeIdentifier, manuallyAdded()));
        }
        if (capabilities.hasDeserialization()) {
            this.processor.dispatch(addDeserialization(typeIdentifier, manuallyAdded()));
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
        final Map<TypeIdentifier, CollectionResult> result = this.processor.collect(this.detector, disambiguators);

        final Map<TypeIdentifier, Definition> definitionsMap = new HashMap<>(result.size());
        final Map<TypeIdentifier, ScanInformationBuilder> scanInformationMap = new HashMap<>(result.size());
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

