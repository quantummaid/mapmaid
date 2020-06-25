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
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
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
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.deserialization.InternalDeserializer.internalDeserializer;
import static de.quantummaid.mapmaid.mapper.deserialization.Unmarshallers.unmarshallers;
import static de.quantummaid.mapmaid.mapper.universal.Universal.fromNativeJava;
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
        final InternalDeserializer internalDeserializer = internalDeserializer(
                definitions, customPrimitiveMappings, onValidationErrors);
        return new Deserializer(definitions, exceptionMapping, unmarshallers, internalDeserializer, debugInformation);
    }

    public <T> T deserializeFromUniversalObject(final Object input,
                                                final TypeIdentifier targetType,
                                                final InjectorLambda injectorProducer) {
        final Universal universal = fromNativeJava(input);
        return deserialize(universal, targetType, injectorProducer);
    }

    public Object deserializeToUniversalObject(final String input,
                                               final MarshallingType type) {
        final Universal universal = this.unmarshallers.unmarshall(input, type);
        return universal.toNativeJava();
    }

    public Object deserialize(final String input,
                              final TypeIdentifier targetType,
                              final MarshallingType marshallingType,
                              final InjectorLambda injectorProducer) {
        try {
            final Universal unmarshalled = this.unmarshallers.unmarshall(input, marshallingType);
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
        return this.internalDeserializer.deserialize(
                input,
                targetType,
                exceptionTracker,
                injector,
                this.debugInformation
        );
    }

    public Universal schema(final TypeIdentifier typeIdentifier) {
        validateNotNull(typeIdentifier, "typeIdentifier");
        final Definition definition = definitions.getDefinitionForType(typeIdentifier);
        final TypeDeserializer deserializer = definition.deserializer().orElseThrow();
        return deserializer.schema(this::schema);
    }

    public Set<MarshallingType> supportedMarshallingTypes() {
        return this.unmarshallers.supportedMarshallingTypes();
    }

    public Definitions getDefinitions() {
        return this.definitions;
    }
}
