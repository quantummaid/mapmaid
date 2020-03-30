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
import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalNull;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.lang.String.format;
import static java.util.Objects.isNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Serializer implements SerializationCallback {
    private final MarshallerRegistry<Marshaller> marshallers;
    private final Definitions definitions;
    private final CustomPrimitiveMappings customPrimitiveMappings;
    private final DebugInformation debugInformation;

    public static Serializer theSerializer(final MarshallerRegistry<Marshaller> marshallers,
                                           final Definitions definitions,
                                           final CustomPrimitiveMappings customPrimitiveMappings,
                                           final DebugInformation debugInformation) {
        return new Serializer(marshallers, definitions, customPrimitiveMappings, debugInformation);
    }

    public Set<MarshallingType> supportedMarshallingTypes() {
        return this.marshallers.supportedTypes();
    }

    @SuppressWarnings("unchecked")
    public String serialize(final Object object,
                            final TypeIdentifier type,
                            final MarshallingType marshallingType,
                            final Function<Map<String, Object>, Map<String, Object>> serializedPropertyInjector) {
        NotNullValidator.validateNotNull(object, "object");
        Object normalized = normalize(object, type);
        if (normalized instanceof Map) {
            normalized = serializedPropertyInjector.apply((Map<String, Object>) normalized);
        }
        final Marshaller marshaller = this.marshallers.getForType(marshallingType);
        try {
            return marshaller.marshal(normalized);
        } catch (final Exception e) {
            throw new UnsupportedOperationException(format("Could not marshal normalization %s", normalized), e);
        }
    }

    public String serializeFromMap(final Map<String, Object> map,
                                   final MarshallingType marshallingType) {
        final Marshaller marshaller = this.marshallers.getForType(marshallingType);
        try {
            return marshaller.marshal(map);
        } catch (final Exception e) {
            throw new UnsupportedOperationException(format("Could not marshal from map %s", map), e);
        }
    }

    public String serializeFromUniversalObject(final Object object,
                                               final MarshallingType marshallingType) {
        final Marshaller marshaller = this.marshallers.getForType(marshallingType);
        try {
            return marshaller.marshal(object);
        } catch (final Exception e) {
            throw new UnsupportedOperationException(format("Could not marshal from object %s", object), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> serializeToMap(final Object object) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        final TypeIdentifier typeIdentifier = typeIdentifierFor(object.getClass());
        final Object normalized = normalize(object, typeIdentifier);
        if (!(normalized instanceof Map)) {
            throw new UnsupportedOperationException("Only serialized objects can be serialized to map");
        }
        return (Map<String, Object>) normalized;
    }

    public Object serializeToUniversalObject(final Object object) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        final Class<?> type = object.getClass();
        return serializeToUniversalObject(object, type);
    }

    public Object serializeToUniversalObject(final Object object, final Class<?> type) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        final GenericType<?> genericType = genericType(type);
        return serializeToUniversalObject(object, genericType);
    }

    public Object serializeToUniversalObject(final Object object, final GenericType<?> type) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializeToUniversalObject(typeIdentifier);
    }

    public Object serializeToUniversalObject(final Object object, final TypeIdentifier type) {
        if (isNull(object)) {
            return new HashMap<>(0);
        }
        final Object normalized = normalize(object, type);
        return normalized;
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
            return UniversalNull.universalNull();
        }
        final SerializationTracker childTracker = tracker.trackToProhibitCyclicReferences(object);
        final Definition definition = this.definitions.getDefinitionForType(type);
        return definition.serializer()
                .orElseThrow(() -> {
                    final ScanInformation scanInformation = this.debugInformation.scanInformationFor(type);
                    return mapMaidException(
                            format("No serializer configured for type '%s'", definition.type().description()), scanInformation);
                })
                .serialize(object, this, childTracker, this.customPrimitiveMappings, this.debugInformation);
    }

    public Definitions getDefinitions() {
        return this.definitions;
    }
}
