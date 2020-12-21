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

package de.quantummaid.mapmaid.mapper.serialization;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.registry.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.mapper.universal.UniversalNull.universalNull;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Objects.isNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
public final class Serializer implements SerializationCallback {
    private final MarshallerRegistry marshallers;
    private final Definitions definitions;
    private final CustomPrimitiveMappings customPrimitiveMappings;
    private final DebugInformation debugInformation;

    public static Serializer serializer(final MarshallerRegistry marshallers,
                                        final Definitions definitions,
                                        final CustomPrimitiveMappings customPrimitiveMappings,
                                        final DebugInformation debugInformation) {
        return new Serializer(marshallers, definitions, customPrimitiveMappings, debugInformation);
    }

    public Set<MarshallingType<?>> supportedMarshallingTypes() {
        return this.marshallers.supportedTypes();
    }

    @SuppressWarnings("unchecked")
    public <T> T serialize(final Object object,
                           final TypeIdentifier type,
                           final MarshallingType<T> marshallingType,
                           final UnaryOperator<Map<String, Object>> serializedPropertyInjector) {
        validateNotNull(object, "object");
        Object normalized = normalize(object, type);
        if (normalized instanceof Map) {
            normalized = serializedPropertyInjector.apply((Map<String, Object>) normalized);
        }
        final Marshaller<T> marshaller = marshallers.getForType(marshallingType);
        try {
            return marshaller.marshal(normalized);
        } catch (final Exception e) {
            throw UnexpectedExceptionThrownDuringMarshallingException.fromException(e, object);
        }
    }

    public <T> T marshalFromUniversalObject(final Object object,
                                            final MarshallingType<T> marshallingType) {
        final Marshaller<T> marshaller = marshallers.getForType(marshallingType);
        try {
            return marshaller.marshal(object);
        } catch (final Exception e) {
            throw UnexpectedExceptionThrownDuringMarshallingException.fromException(e, object);
        }
    }

    public Object serializeToUniversalObject(final Object object, final TypeIdentifier type) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        return normalize(object, type);
    }

    private Object normalize(final Object object, final TypeIdentifier type) {
        if (isNull(object)) {
            return null;
        }
        return serializeDefinition(type, object, SerializationTracker.serializationTracker()).toNativeJava();
    }

    @Override
    public Universal serializeDefinition(final TypeIdentifier type,
                                         final Object object,
                                         final SerializationTracker tracker) {
        if (isNull(object)) {
            return universalNull();
        }
        final SerializationTracker childTracker = tracker.trackToProhibitCyclicReferences(object);
        final Definition definition = definitions.getDefinitionForType(type);
        return definition.serializer()
                .orElseThrow(() -> {
                    final ScanInformation scanInformation = debugInformation.scanInformationFor(type);
                    return mapMaidException(
                            format("No serializer configured for type '%s'", definition.type().description()), scanInformation);
                })
                .serialize(object, this, childTracker, customPrimitiveMappings, debugInformation);
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public Universal schema(final TypeIdentifier typeIdentifier) {
        validateNotNull(typeIdentifier, "typeIdentifier");
        final Definition definition = definitions.getDefinitionForType(typeIdentifier);
        final TypeSerializer serializer = definition.serializer().orElseThrow();
        return serializer.schema(this::schema);
    }
}
