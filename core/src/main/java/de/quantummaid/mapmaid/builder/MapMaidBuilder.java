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
import de.quantummaid.mapmaid.builder.resolving.MapMaidDetector;
import de.quantummaid.mapmaid.builder.resolving.MapMaidOnCollectionError;
import de.quantummaid.mapmaid.builder.resolving.MapMaidResolver;
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.Lingo;
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
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.CollectionResult;
import de.quantummaid.reflectmaid.typescanner.Processor;
import de.quantummaid.reflectmaid.typescanner.Reason;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.requirements.DetectionRequirements;
import de.quantummaid.reflectmaid.typescanner.scopes.Scope;
import de.quantummaid.reflectmaid.typescanner.signals.Signal;
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
import static de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType.createCustomPrimitive;
import static de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType.createCustomPrimitive;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.deserialization_only.Builder00.serializedObjectBuilder00;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.serialization_only.SerializationOnlySerializedObject.serializationOnlySerializedObject;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidDetector.mapMaidDetector;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidOnCollectionError.mapMaidOnCollectionError;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidResolver.mapMaidResolver;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.DESERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.Requirements.SERIALIZATION;
import static de.quantummaid.mapmaid.builder.resolving.factories.customtype.CustomTypeFactory.customTypeFactory;
import static de.quantummaid.mapmaid.builder.resolving.factories.injecting.InjectingFactory.injectingFactory;
import static de.quantummaid.mapmaid.builder.resolving.factories.polymorphy.PolymorphicFactory.polymorphicFactory;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.debug.DebugInformation.debugInformation;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.Deserializer.theDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.Serializer.serializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static de.quantummaid.reflectmaid.typescanner.Reason.manuallyAdded;
import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.typescanner.scopes.Scope.rootScope;
import static de.quantummaid.reflectmaid.typescanner.signals.AddReasonSignal.addReasonSignal;
import static java.util.Arrays.asList;

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
    private final List<Signal<MapMaidTypeScannerResult>> signals = new ArrayList<>();
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
        advancedBuilder.withStateFactory(polymorphicFactory(superType, subTypes, advancedBuilder.mapMaidConfiguration(), capabilities));
        return withType(capabilities, superType, manuallyAdded());
    }

    @Override
    public MapMaidBuilder injecting(final TypeIdentifier typeIdentifier, final TypeDeserializer deserializer) {
        injectionTypes.add(typeIdentifier);
        advancedBuilder.withStateFactory(injectingFactory(typeIdentifier, deserializer));
        return withType(duplex(), typeIdentifier, manuallyAdded());
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
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final TypeIdentifier typeIdentifier = typeIdentifierFor(resolvedType);
        return withType(capabilities, typeIdentifier, reason);
    }

    private MapMaidBuilder withType(final RequiredCapabilities capabilities, final TypeIdentifier typeIdentifier, final Reason reason) {
        if (capabilities.hasSerialization()) {
            signals.add(addReasonSignal(typeIdentifier, SERIALIZATION, reason));
        }
        if (capabilities.hasDeserialization()) {
            signals.add(addReasonSignal(typeIdentifier, DESERIALIZATION, reason));
        }
        return this;
    }

    @Override
    public <T> MapMaidBuilder withCustomType(final RequiredCapabilities capabilities,
                                             final CustomType<T> customType) {
        validateNotNull(capabilities, "capabilities");
        validateNotNull(customType, "customType");
        advancedBuilder.withStateFactory(customTypeFactory(customType, capabilities));
        return withType(capabilities, customType.type(), manuallyAdded());
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
        final SerializationOnlyType<T> serializationOnlyType = createCustomPrimitive(typeIdentifier, serializer, baseType);
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
        final DeserializationOnlyType<T> deserializationOnlyType = createCustomPrimitive(typeIdentifier, deserializer, baseType);
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
        recipes.forEach(Recipe::init);
        recipes.forEach(recipe -> recipe.cook(this));

        final Processor<MapMaidTypeScannerResult> processor = advancedBuilder.processor();
        signals.forEach(processor::dispatch);

        final Disambiguators disambiguators = advancedBuilder.buildDisambiguators();
        final MapMaidDetector detector = mapMaidDetector(this.detector, disambiguators, injectionTypes);
        final MapMaidResolver resolver = mapMaidResolver();
        final MapMaidOnCollectionError onError = mapMaidOnCollectionError(reflectMaid);
        final Map<TypeIdentifier, Map<Scope, CollectionResult<MapMaidTypeScannerResult>>> result =
                processor.collect(detector, resolver, onError, Lingo::mode);

        final Map<TypeIdentifier, Definition> definitionsMap = new HashMap<>(result.size());
        result.forEach((type, collectionResultByScope) -> {
            final CollectionResult<MapMaidTypeScannerResult> collectionResult = collectionResultByScope.get(rootScope());
            final DetectionRequirements requirements = collectionResult.getDetectionRequirements();
            final TypeSerializer serializer;
            if (requirements.requires(SERIALIZATION)) {
                serializer = collectionResult.getDefinition().disambiguationResult().serializer();
            } else {
                serializer = null;
            }

            final TypeDeserializer deserializer;
            if (requirements.requires(DESERIALIZATION)) {
                deserializer = collectionResult.getDefinition().disambiguationResult().deserializer();
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
