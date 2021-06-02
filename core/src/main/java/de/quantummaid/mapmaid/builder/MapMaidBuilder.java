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
import de.quantummaid.mapmaid.builder.builder.*;
import de.quantummaid.mapmaid.builder.builder.customobjects.CustomObjectsBuilder;
import de.quantummaid.mapmaid.builder.builder.customobjects.DeserializationOnlyBuilder;
import de.quantummaid.mapmaid.builder.builder.customobjects.DuplexBuilder;
import de.quantummaid.mapmaid.builder.builder.customobjects.SerializationOnlyBuilder;
import de.quantummaid.mapmaid.builder.conventional.ConventionalDetectors;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.duplex.Builder00;
import de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only.SerializationOnlySerializedObject;
import de.quantummaid.mapmaid.builder.detection.SimpleDetector;
import de.quantummaid.mapmaid.builder.injection.FixedInjector;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.builder.resolving.MapMaidOnCollectionError;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.CollectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.Processor;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirementReasons;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Detector;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Resolver;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.Reason;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.*;
import de.quantummaid.mapmaid.mapper.injector.InjectorFactory;
import de.quantummaid.mapmaid.mapper.marshalling.registry.Marshallers;
import de.quantummaid.mapmaid.mapper.marshalling.registry.UnmarshallerRegistry;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.MapMaid.mapMaid;
import static de.quantummaid.mapmaid.builder.AdvancedBuilder.advancedBuilder;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Builder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only.SerializationOnlySerializedObject.serializationOnlySerializedObject;
import static de.quantummaid.mapmaid.builder.injection.InjectionSerializer.injectionSerializer;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidDetector.mapMaidDetector;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidOnCollectionError.mapMaidOnCollectionError;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidResolver.mapMaidResolver;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.DESERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.SERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.disambiguationResult;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.Context.emptyContext;
import static de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.AddReasonSignal.addReason;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.debug.Reason.manuallyAdded;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.serializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicDeserializer.polymorphicDeserializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicSerializer.polymorphicSerializer;
import static de.quantummaid.mapmaid.polymorphy.PolymorphicUtils.nameToIdentifier;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1200")
public final class MapMaidBuilder implements
        DetectedTypesBuilder,
        InjectingBuilder,
        ProgrammaticTypeBuilder,
        CustomTypesBuilder,
        CustomPrimitivesBuilder,
        CustomObjectsBuilder,
        CustomCollectionBuilder {
    private final ReflectMaid reflectMaid;
    private final List<ManuallyAddedState> manuallyAddedStates = new ArrayList<>();
    private final SimpleDetector detector = ConventionalDetectors.conventionalDetector();
    private final List<TypeIdentifier> injectionTypes = new ArrayList<>();
    private final AdvancedBuilder advancedBuilder;
    private final List<Recipe> recipes = smallList();
    private final ValidationMappings validationMappings = ValidationMappings.empty();
    private final ValidationErrorsMapping validationErrorsMapping = validationErrors -> {
        throw AggregatedValidationException.fromList(validationErrors);
    };

    public static MapMaidBuilder mapMaidBuilder(final ReflectMaid reflectMaid) {
        final AdvancedBuilder advancedBuilder = advancedBuilder(reflectMaid);
        return new MapMaidBuilder(reflectMaid, advancedBuilder);
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "rawtypes", "unchecked"})
    public final <T> MapMaidBuilder serializingSubtypes(final Class<T> superType,
                                                        final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return serializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <T> MapMaidBuilder serializingSubtypes(final GenericType<T> superType,
                                                        final GenericType<? extends T>... subTypes) {
        final ResolvedType resolvedSupertype = reflectMaid.resolve(superType);
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(resolvedSupertype);
        final ResolvedType[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(reflectMaid::resolve)
                .toArray(ResolvedType[]::new);
        return serializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder serializingSubtypes(final TypeIdentifier superType,
                                              final ResolvedType... subTypes) {
        return withSubtypes(serialization(), superType, asList(subTypes));
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "rawtypes", "unchecked"})
    public final <T> MapMaidBuilder deserializingSubtypes(final Class<T> superType,
                                                          final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return deserializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <T> MapMaidBuilder deserializingSubtypes(final GenericType<T> superType,
                                                          final GenericType<? extends T>... subTypes) {
        final ResolvedType resolvedSuperType = reflectMaid.resolve(superType);
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(resolvedSuperType);
        final ResolvedType[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(reflectMaid::resolve)
                .toArray(ResolvedType[]::new);
        return deserializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder deserializingSubtypes(final TypeIdentifier superType,
                                                final ResolvedType... subTypes) {
        return withSubtypes(deserialization(), superType, asList(subTypes));
    }

    @SafeVarargs
    @SuppressWarnings({"varargs", "rawtypes", "unchecked"})
    public final <T> MapMaidBuilder serializingAndDeserializingSubtypes(final Class<T> superType,
                                                                        final Class<? extends T>... subTypes) {
        final GenericType<T> genericSuperType = genericType(superType);
        final GenericType[] genericSubTypes = Arrays.stream(subTypes)
                .map(GenericType::genericType)
                .toArray(GenericType[]::new);
        return serializingAndDeserializingSubtypes(genericSuperType, genericSubTypes);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <T> MapMaidBuilder serializingAndDeserializingSubtypes(final GenericType<T> superType,
                                                                        final GenericType<? extends T>... subTypes) {
        final ResolvedType resolvedSuperType = reflectMaid.resolve(superType);
        final TypeIdentifier superTypeIdentifier = typeIdentifierFor(resolvedSuperType);
        final ResolvedType[] subTypeIdentifiers = Arrays.stream(subTypes)
                .map(reflectMaid::resolve)
                .toArray(ResolvedType[]::new);
        return serializingAndDeserializingSubtypes(superTypeIdentifier, subTypeIdentifiers);
    }

    public MapMaidBuilder serializingAndDeserializingSubtypes(final TypeIdentifier superType,
                                                              final ResolvedType... subTypes) {
        return withSubtypes(duplex(), superType, asList(subTypes));
    }

    public MapMaidBuilder withSubtypes(final RequiredCapabilities capabilities,
                                       final TypeIdentifier superType,
                                       final List<ResolvedType> subTypes) {
        manuallyAddedStates.add((configuration, processor) -> {
            final List<TypeIdentifier> subTypeIdentifiers = subTypes.stream()
                    .map(TypeIdentifier::typeIdentifierFor)
                    .collect(toList());
            final BiMap<String, TypeIdentifier> nameToType = nameToIdentifier(subTypeIdentifiers, configuration);
            final String typeIdentifierKey = configuration.getTypeIdentifierKey();
            final TypeSerializer serializer;
            if (capabilities.hasSerialization()) {
                serializer = polymorphicSerializer(superType, subTypes, nameToType, typeIdentifierKey);
                processor.dispatch(addReason(SERIALIZATION, superType, manuallyAdded()));
            } else {
                serializer = null;
            }
            final TypeDeserializer deserializer;
            if (capabilities.hasDeserialization()) {
                deserializer = polymorphicDeserializer(superType, nameToType, typeIdentifierKey);
                processor.dispatch(addReason(DESERIALIZATION, superType, manuallyAdded()));
            } else {
                deserializer = null;
            }
            final Context<DisambiguationResult> context = emptyContext(processor::dispatch, superType);
            context.setManuallyConfiguredResult(disambiguationResult(serializer, deserializer));
            final StatefulDefinition<DisambiguationResult> statefulDefinition = unreasoned(context);
            processor.addState(statefulDefinition);
        });
        return this;
    }

    @Override
    public MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final TypeDeserializer deserializer) {
        injectionTypes.add(typeIdentifier);
        manuallyAddedStates.add((configuration, processor) -> {
            final Context<DisambiguationResult> context = emptyContext(processor::dispatch, typeIdentifier);
            final TypeSerializer serializer = injectionSerializer(typeIdentifier);
            context.setManuallyConfiguredResult(duplexResult(serializer, deserializer));
            final StatefulDefinition<DisambiguationResult> statefulDefinition = unreasoned(context);
            processor.addState(statefulDefinition);
            processor.dispatch(addReason(SERIALIZATION, typeIdentifier, manuallyAdded()));
            processor.dispatch(addReason(DESERIALIZATION, typeIdentifier, manuallyAdded()));
        });
        return this;
    }

    @Override
    public MapMaidBuilder injecting(final GenericType<?> genericType) {
        final ResolvedType resolvedType = reflectMaid.resolve(genericType);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return injecting(typeIdentifier);
    }

    @Override
    public <T> MapMaidBuilder injecting(final GenericType<T> genericType, final FixedInjector<T> injector) {
        final ResolvedType resolvedType = reflectMaid.resolve(genericType);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return injecting(typeIdentifier, injector);
    }

    @Override
    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities) {
        return withType(type, capabilities, manuallyAdded());
    }

    @Override
    public MapMaidBuilder withType(final GenericType<?> type,
                                   final RequiredCapabilities capabilities,
                                   final Reason reason) {
        validateNotNull(type, "type");
        validateNotNull(capabilities, "capabilities");
        validateNotNull(reason, "reason");
        manuallyAddedStates.add((configuration, processor) -> {
            final ResolvedType resolvedType = reflectMaid.resolve(type);
            final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
            if (capabilities.hasSerialization()) {
                processor.dispatch(addReason(SERIALIZATION, typeIdentifier, reason));
            }
            if (capabilities.hasDeserialization()) {
                processor.dispatch(addReason(DESERIALIZATION, typeIdentifier, reason));
            }
        });
        return this;
    }

    @Override
    public <T> MapMaidBuilder withCustomType(final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        validateNotNull(capabilities, "capabilities");
        validateNotNull(customType, "customType");
        manuallyAddedStates.add((configuration, processor) -> {
            final TypeIdentifier typeIdentifier = customType.type();
            final Context<DisambiguationResult> context = emptyContext(processor::dispatch, typeIdentifier);
            final StatefulDefinition<DisambiguationResult> statefulDefinition = unreasoned(context);
            processor.addState(statefulDefinition);
            final TypeSerializer serializer;
            if (capabilities.hasSerialization()) {
                serializer = customType.serializer()
                        .orElseThrow(() -> new IllegalArgumentException(format(
                                "serializer is missing for type '%s'", typeIdentifier.description())));
                processor.dispatch(addReason(SERIALIZATION, typeIdentifier, manuallyAdded()));
            } else {
                serializer = null;
            }
            final TypeDeserializer deserializer;
            if (capabilities.hasDeserialization()) {
                deserializer = customType.deserializer()
                        .orElseThrow(() -> new IllegalArgumentException(format(
                                "deserializer is missing for type '%s'", typeIdentifier.description())));
                processor.dispatch(addReason(DESERIALIZATION, typeIdentifier, manuallyAdded()));
            } else {
                deserializer = null;
            }
            context.setManuallyConfiguredResult(disambiguationResult(serializer, deserializer));
        });
        return this;
    }

    @Override
    public <T, B> MapMaidBuilder serializingCustomPrimitive(final GenericType<T> type,
                                                            final Class<B> baseType,
                                                            final CustomCustomPrimitiveSerializer<T, B> serializer) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return serializingCustomPrimitive(typeIdentifier, baseType, serializer);
    }

    public <T, B> MapMaidBuilder serializingCustomPrimitive(final TypeIdentifier typeIdentifier,
                                                            final Class<B> baseType,
                                                            final CustomCustomPrimitiveSerializer<T, B> serializer) {
        final SerializationOnlyType<T> serializationOnlyType = SerializationOnlyType.createCustomPrimitive(typeIdentifier, serializer, baseType);
        return serializing(serializationOnlyType);
    }

    @Override
    public <T, B> MapMaidBuilder deserializingCustomPrimitive(final GenericType<T> type,
                                                              final Class<B> baseType,
                                                              final CustomCustomPrimitiveDeserializer<T, B> deserializer) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return deserializingCustomPrimitive(typeIdentifier, baseType, deserializer);
    }

    public <T, B> MapMaidBuilder deserializingCustomPrimitive(final TypeIdentifier typeIdentifier,
                                                              final Class<B> baseType,
                                                              final CustomCustomPrimitiveDeserializer<T, B> deserializer) {
        final DeserializationOnlyType<T> deserializationOnlyType = DeserializationOnlyType.createCustomPrimitive(typeIdentifier, deserializer, baseType);
        return deserializing(deserializationOnlyType);
    }

    @Override
    public <T, B> MapMaidBuilder serializingAndDeserializingCustomPrimitive(final GenericType<T> type,
                                                                            final Class<B> baseType,
                                                                            final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                                            final CustomCustomPrimitiveDeserializer<T, B> deserializer) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return serializingAndDeserializingCustomPrimitive(typeIdentifier, baseType, serializer, deserializer);
    }

    public <T, B> MapMaidBuilder serializingAndDeserializingCustomPrimitive(final TypeIdentifier typeIdentifier,
                                                                            final Class<B> baseType,
                                                                            final CustomCustomPrimitiveSerializer<T, B> serializer,
                                                                            final CustomCustomPrimitiveDeserializer<T, B> deserializer) {
        final DuplexType<?> duplexType = DuplexType.createCustomPrimitive(typeIdentifier, serializer, deserializer, baseType);
        return serializingAndDeserializing(duplexType);
    }

    @Override
    public <T> MapMaidBuilder serializingCustomObject(final GenericType<T> type,
                                                      final SerializationOnlyBuilder<T> builder) {
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        final SerializationOnlySerializedObject<T> serializationOnlyType = serializationOnlySerializedObject(reflectMaid, typeIdentifier);
        builder.build(serializationOnlyType);
        return serializing(serializationOnlyType);
    }

    @Override
    public <T> MapMaidBuilder deserializingCustomObject(final GenericType<T> type,
                                                        final DeserializationOnlyBuilder<T> builder) {
        final DeserializationOnlyType<T> deserializationOnlyType = builder.build(serializedObjectBuilder00(reflectMaid, type));
        return deserializing(deserializationOnlyType);
    }

    @Override
    public <T> MapMaidBuilder serializingAndDeserializingCustomObject(final GenericType<T> type,
                                                                      final DuplexBuilder<T> builder) {
        final DuplexType<T> duplexType = builder.build(Builder00.serializedObjectBuilder00(reflectMaid, type));
        return serializingAndDeserializing(duplexType);
    }

    @Override
    public <C, T> MapMaidBuilder serializingInlinedCollection(final GenericType<C> collectionType,
                                                              final GenericType<T> contentType,
                                                              final InlinedCollectionListExtractor<C, T> listExtractor) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(collectionType));
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(contentType));
        return serializingInlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor);
    }

    public <C, T> MapMaidBuilder serializingInlinedCollection(final TypeIdentifier collectionType,
                                                              final TypeIdentifier contentType,
                                                              final InlinedCollectionListExtractor<C, T> listExtractor) {
        final SerializationOnlyType<Object> serializationOnlyType = SerializationOnlyType.inlinedCollection(collectionType, contentType, listExtractor);
        return serializing(serializationOnlyType);
    }

    @Override
    public <C, T> MapMaidBuilder deserializingInlinedCollection(final GenericType<C> collectionType,
                                                                final GenericType<T> contentType,
                                                                final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(collectionType));
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(contentType));
        return deserializingInlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, collectionFactory);
    }

    public <C, T> MapMaidBuilder deserializingInlinedCollection(final TypeIdentifier collectionType,
                                                                final TypeIdentifier contentType,
                                                                final InlinedCollectionFactory<C, T> collectionFactory) {
        final DeserializationOnlyType<Object> deserializationOnlyType = DeserializationOnlyType.inlinedCollection(collectionType, contentType, collectionFactory);
        return deserializing(deserializationOnlyType);
    }

    @Override
    public <C, T> MapMaidBuilder serializingAndDeserializingInlinedCollection(final GenericType<C> collectionType,
                                                                              final GenericType<T> contentType,
                                                                              final InlinedCollectionListExtractor<C, T> listExtractor,
                                                                              final InlinedCollectionFactory<C, T> collectionFactory) {
        final TypeIdentifier collectionTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(collectionType));
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(reflectMaid.resolve(contentType));
        return serializingAndDeserializingInlinedCollection(collectionTypeIdentifier, contentTypeIdentifier, listExtractor, collectionFactory);
    }

    public <C, T> MapMaidBuilder serializingAndDeserializingInlinedCollection(final TypeIdentifier collectionType,
                                                                              final TypeIdentifier contentType,
                                                                              final InlinedCollectionListExtractor<C, T> listExtractor,
                                                                              final InlinedCollectionFactory<C, T> collectionFactory) {
        final DuplexType<?> duplexType = DuplexType.inlinedCollection(collectionType, contentType, listExtractor, collectionFactory);
        return serializingAndDeserializing(duplexType);
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

    public ReflectMaid reflectMaid() {
        return reflectMaid;
    }

    public MapMaid build() {
        this.recipes.forEach(Recipe::init);
        this.recipes.forEach(recipe -> recipe.cook(this));

        final MapMaidConfiguration mapMaidConfiguration = advancedBuilder.mapMaidConfiguration();
        final Processor<DisambiguationResult> processor = advancedBuilder.processor();
        manuallyAddedStates.forEach(manuallyAddedState -> manuallyAddedState.addState(mapMaidConfiguration, processor));

        final Disambiguators disambiguators = this.advancedBuilder.buildDisambiguators();
        final Detector<DisambiguationResult> detector = mapMaidDetector(this.detector, disambiguators, injectionTypes);
        final Resolver<DisambiguationResult> resolver = mapMaidResolver();
        final MapMaidOnCollectionError onError = mapMaidOnCollectionError(reflectMaid);
        final Map<TypeIdentifier, CollectionResult<DisambiguationResult>> result = processor.collect(detector, resolver, onError);

        final Map<TypeIdentifier, Definition> definitionsMap = new HashMap<>(result.size());
        result.forEach((type, collectionResult) -> {
            final DetectionRequirementReasons requirements = collectionResult.detectionRequirements();
            final TypeSerializer serializer;
            if (requirements.requires(SERIALIZATION)) {
                serializer = collectionResult.definition().serializer();
            } else {
                serializer = null;
            }

            final TypeDeserializer deserializer;
            if (requirements.requires(DESERIALIZATION)) {
                deserializer = collectionResult.definition().deserializer();
            } else {
                deserializer = null;
            }
            final Definition definition = generalDefinition(type, serializer, deserializer);
            definitionsMap.put(type, definition);
        });

        final DebugInformation debugInformation = debugInformation(result, processor.log(), reflectMaid);
        final Definitions definitions = definitions(definitionsMap, debugInformation);

        final Marshallers marshallers = advancedBuilder.buildMarshallers();
        final Serializer serializer = serializer(
                marshallers,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                debugInformation
        );

        final UnmarshallerRegistry unmarshallerRegistry = advancedBuilder.buildUnmarshallerRegistry();
        final InjectorFactory injectorFactory = InjectorFactory.emptyInjectorFactory(reflectMaid);
        final Deserializer deserializer = theDeserializer(
                unmarshallerRegistry,
                definitions,
                CUSTOM_PRIMITIVE_MAPPINGS,
                this.validationMappings,
                this.validationErrorsMapping,
                debugInformation,
                injectorFactory
        );
        return mapMaid(reflectMaid, serializer, deserializer, debugInformation);
    }
}
