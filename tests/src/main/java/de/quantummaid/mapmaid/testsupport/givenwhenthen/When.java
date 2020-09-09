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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static de.quantummaid.mapmaid.mapper.injector.InjectorLambda.noop;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.YAML;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Then.then;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.ThenData.thenData;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class When {
    private final MapMaid mapMaid;
    private final ThenData thenData;

    static When aWhen(final Supplier<MapMaid> mapMaidSupplier) {
        final ThenData thenData = thenData();
        MapMaid mapMaid;
        try {
            mapMaid = mapMaidSupplier.get();
        } catch (final Exception e) {
            thenData.withException(e);
            mapMaid = null;
        }
        return new When(mapMaid, thenData);
    }

    private Then doDeserialization(final Supplier<Object> deserializer) {
        try {
            final Object result = deserializer.get();
            return then(this.thenData.withDeserializationResult(result));
        } catch (final Exception e) {
            return then(this.thenData.withException(e));
        }
    }

    @SuppressWarnings({"unchecked", "CastToConcreteClass"})
    public AsStage mapMaidDeserializes(final String input) {
        return marshallingType -> type ->
                doDeserialization(() -> mapMaid.deserialize(input, type, (MarshallingType<String>) marshallingType));
    }

    @SuppressWarnings({"unchecked", "CastToConcreteClass"})
    public AsStage mapMaidDeserializesWithInjection(final String input, final InjectorLambda injector) {
        return marshallingType -> type ->
                doDeserialization(() -> mapMaid.deserialize(input, type, (MarshallingType<String>) marshallingType, injector));
    }

    @SuppressWarnings("unchecked")
    public ToStage mapMaidDeserializesTheMap(final String jsonMap) {
        final Map<String, Object> map = new Gson().fromJson(jsonMap, Map.class);
        return mapMaidDeserializesTheMap(map);
    }

    public ToStage mapMaidDeserializesTheMap(final Map<String, Object> map) {
        return type -> doDeserialization(() -> this.mapMaid.deserializer().deserializeFromUniversalObject(map, type, noop()));
    }

    public WithMarshallingType mapMaidSerializes(final Object object) {
        return marshallingType -> {
            try {
                final Object serialized = mapMaid.serializeTo(object, marshallingType);
                return then(this.thenData.withSerializationResult(serialized));
            } catch (final Exception e) {
                return then(this.thenData.withException(e));
            }
        };
    }

    public WithMarshallingType mapMaidSerializes(final Object object, final Class<?> type) {
        final GenericType<?> genericType = GenericType.genericType(type);
        return mapMaidSerializes(object, genericType);
    }

    public WithMarshallingType mapMaidSerializes(final Object object, final GenericType<?> type) {
        return marshallingType -> {
            final Object serialized = mapMaid.serializeTo(object, marshallingType, type);
            return then(this.thenData.withSerializationResult(serialized));
        };
    }

    public Then mapMaidSerializesToUniversalObject(final Object object,
                                                   final TypeIdentifier typeIdentifier) {
        final Object serialized = this.mapMaid.serializer().serializeToUniversalObject(object, typeIdentifier);
        return then(this.thenData.withSerializationResult(serialized));
    }

    public Then mapMaidMarshalsFromUniversalObject(final Object universalObject,
                                                   final MarshallingType<String> marshallingType) {
        final Object serialized = mapMaid.serializer().marshalFromUniversalObject(universalObject, marshallingType);
        return then(this.thenData.withSerializationResult(serialized));
    }

    public Then mapMaidUnmarshalsToUniversalObject(final String input,
                                                   final MarshallingType<String> marshallingType) {
        final Object deserialized = this.mapMaid.deserializer().deserializeToUniversalObject(input, marshallingType);
        return then(this.thenData.withDeserializationResult(deserialized));
    }

    public WithMarshallingType mapMaidSerializesWithInjector(final Object object,
                                                             final UnaryOperator<Map<String, Object>> injector) {
        return marshallingType -> {
            final TypeIdentifier typeIdentifier = typeIdentifierFor(object.getClass());
            final Object serialized = mapMaid.serializer().serialize(object, typeIdentifier, marshallingType, injector);
            return then(thenData.withSerializationResult(serialized));
        };
    }

    public Then theDeserializationSchemaIsQueriedFor(final Class<?> type) {
        final String schema = mapMaid.deserializationSchemaFor(type, YAML);
        return then(thenData.withSchema(schema));
    }

    public Then theSerializationSchemaIsQueriedFor(final Class<?> type) {
        final String schema = mapMaid.serializationSchemaFor(type, YAML);
        return then(thenData.withSchema(schema));
    }

    public Then theDefinitionsAreQueried() {
        final DebugInformation debugInformation = mapMaid.debugInformation();
        return then(thenData.withDebugInformation(debugInformation));
    }

    public Then theSupportedMarshallingTypesAreQueried() {
        final Set<MarshallingType<?>> marshallingTypes = this.mapMaid.serializer().supportedMarshallingTypes();
        final Set<MarshallingType<?>> unmarshallingTypes = this.mapMaid.deserializer().supportedMarshallingTypes();
        return then(this.thenData.withSupportedMarshallingTypes(marshallingTypes, unmarshallingTypes));
    }

    public Then mapMaidIsInstantiated() {
        return then(this.thenData);
    }
}
