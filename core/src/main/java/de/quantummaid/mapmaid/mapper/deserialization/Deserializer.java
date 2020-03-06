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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationErrorsMapping;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationMappings;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.injector.InjectorFactory;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.deserialization.InternalDeserializer.internalDeserializer;
import static de.quantummaid.mapmaid.mapper.deserialization.Unmarshallers.unmarshallers;
import static de.quantummaid.mapmaid.mapper.injector.InjectorLambda.noop;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.shared.identifier.RealTypeIdentifier.realTypeIdentifier;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Deserializer {
    private final Definitions definitions;
    private final ValidationMappings validationMappings;
    private final Unmarshallers unmarshallers;
    private final InternalDeserializer internalDeserializer;
    private final DebugInformation debugInformation;
    private final InjectorFactory injectorFactory = InjectorFactory.emptyInjectorFactory();

    public static Deserializer theDeserializer(final MarshallerRegistry<Unmarshaller> unmarshallerRegistry,
                                               final Definitions definitions,
                                               final CustomPrimitiveMappings customPrimitiveMappings,
                                               final ValidationMappings exceptionMapping,
                                               final ValidationErrorsMapping onValidationErrors,
                                               final DebugInformation debugInformation) {
        validateNotNull(unmarshallerRegistry, "unmarshallerRegistry");
        validateNotNull(definitions, "definitions");
        validateNotNull(customPrimitiveMappings, "customPrimitiveMappings");
        validateNotNull(exceptionMapping, "validationMappings");
        validateNotNull(onValidationErrors, "onValidationErrors");
        validateNotNull(debugInformation, "debugInformation");
        final Unmarshallers unmarshallers = unmarshallers(unmarshallerRegistry);
        final InternalDeserializer internalDeserializer = internalDeserializer(definitions, customPrimitiveMappings, onValidationErrors);
        return new Deserializer(definitions, exceptionMapping, unmarshallers, internalDeserializer, debugInformation);
    }

    public <T> T deserializeFromMap(final Map<String, Object> input,
                                    final Class<T> targetType) {
        return deserializeFromMap(input, targetType, noop());
    }

    public <T> T deserializeFromMap(final Map<String, Object> input,
                                    final ResolvedType targetType) {
        return deserializeFromMap(input, targetType, noop());
    }

    public <T> T deserializeFromMap(final Map<String, Object> input,
                                    final Class<T> targetType,
                                    final InjectorLambda injectorProducer) {
        return deserializeFromMap(input, ClassType.fromClassWithoutGenerics(targetType), injectorProducer);
    }

    public <T> T deserializeFromMap(final Map<String, Object> input,
                                    final ResolvedType targetType,
                                    final InjectorLambda injectorProducer) {
        final UniversalObject universalObject = UniversalObject.universalObjectFromNativeMap(input);
        final TypeIdentifier typeIdentifier = realTypeIdentifier(targetType);
        return deserialize(universalObject, typeIdentifier, injectorProducer);
    }

    public Map<String, Object> deserializeToMap(final String input,
                                                final MarshallingType type) {
        return this.unmarshallers.unmarshalToMap(input, type);
    }

    public <T> T deserializeJson(final String json,
                                 final Class<T> targetType) {
        return deserialize(json, targetType, json());
    }

    public <T> T deserializeJson(final String json,
                                 final Class<T> targetType,
                                 final InjectorLambda injectorLambda) {
        return deserialize(json, targetType, json(), injectorLambda);
    }

    public <T> T deserialize(final String input,
                             final Class<T> targetType,
                             final MarshallingType marshallingType) {
        return deserialize(input, targetType, marshallingType, noop());
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(final String input,
                             final ResolvedType targetType,
                             final MarshallingType marshallingType) {
        return (T) deserialize(input, targetType, marshallingType, noop());
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(final String input,
                             final Class<T> targetType,
                             final MarshallingType marshallingType,
                             final InjectorLambda injectorProducer) {
        validateNotNull(input, "input");
        final ClassType resolvedType = ClassType.fromClassWithoutGenerics(targetType);
        return (T) deserialize(input, resolvedType, marshallingType, injectorProducer);
    }

    public Object deserialize(final String input,
                              final ResolvedType targetType,
                              final MarshallingType marshallingType,
                              final InjectorLambda injectorProducer) {
        final TypeIdentifier typeIdentifier = realTypeIdentifier(targetType);
        return deserialize(input, typeIdentifier, marshallingType, injectorProducer);
    }

    public Object deserialize(final String input,
                              final TypeIdentifier targetType,
                              final MarshallingType marshallingType,
                              final InjectorLambda injectorProducer) {
        final Definition definition = this.definitions.getDefinitionForType(targetType);
        final Class<? extends Universal> universalRequirement = definition
                .deserializer()
                .orElseThrow(() -> {
                    final ScanInformation scanInformation = this.debugInformation.scanInformationFor(definition.type());
                    return mapMaidException(
                            format("No deserializer registered for type '%s'", targetType.description()), scanInformation);
                })
                .universalRequirement();
        try {
            final Universal unmarshalled = this.unmarshallers.unmarshalTo(universalRequirement, input, marshallingType);
            return deserialize(unmarshalled, targetType, injectorProducer);
        } catch (final UnmarshallingException e) {
            final ScanInformation scanInformation = this.debugInformation.scanInformationFor(targetType);
            throw mapMaidException(format(
                    "Error during unmarshalling for type '%s' with input '%s'",
                    targetType.description(), input), e, scanInformation);
        }
    }

    private <T> T deserialize(final Universal input,
                              final TypeIdentifier targetType,
                              final InjectorLambda injectorProducer) {
        validateNotNull(input, "input");
        validateNotNull(targetType, "targetType");
        validateNotNull(injectorProducer, "jsonInjector");
        final ExceptionTracker exceptionTracker = ExceptionTracker.emptyTracker(input, this.validationMappings);
        final Injector injector = this.injectorFactory.create();
        injectorProducer.setupInjector(injector);
        return this.internalDeserializer.deserialize(input, targetType, exceptionTracker, injector, this.debugInformation);
    }

    public Set<MarshallingType> supportedMarshallingTypes() {
        return this.unmarshallers.supportedMarshallingTypes();
    }

    public Definitions getDefinitions() {
        return this.definitions;
    }
}
