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
import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public AsStage mapMaidDeserializes(final String input) {
        return marshallingType -> type ->
                doDeserialization(() -> this.mapMaid.deserialize(input, type, marshallingType));
    }

    public AsStage mapMaidDeserializesWithInjection(final String input, final InjectorLambda injector) {
        return marshallingType -> type ->
                doDeserialization(() -> this.mapMaid.deserializer().deserialize(input, type, marshallingType, injector));
    }

    @SuppressWarnings("unchecked")
    public ToStage mapMaidDeserializesTheMap(final String jsonMap) {
        return type -> {
            final Map<String, Object> map = new Gson().fromJson(jsonMap, Map.class);
            return doDeserialization(() -> this.mapMaid.deserializer().deserializeFromUniversalObject(map, type));
        };
    }

    public WithMarshallingType mapMaidSerializes(final Object object) {
        return marshallingType -> {
            try {
                final String serialized = this.mapMaid.serializeTo(object, marshallingType);
                return then(this.thenData.withSerializationResult(serialized));
            } catch (final Exception e) {
                return then(this.thenData.withException(e));
            }
        };
    }

    public WithMarshallingType mapMaidSerializes(final Object object, final GenericType<?> type) {
        return marshallingType -> {
            try {
                final String serialized = this.mapMaid.serializeTo(object, marshallingType, type);
                return then(this.thenData.withSerializationResult(serialized));
            } catch (final Exception e) {
                return then(this.thenData.withException(e));
            }
        };
    }

    public WithMarshallingType mapMaidSerializesWithInjector(final Object object,
                                                             final Function<Map<String, Object>, Map<String, Object>> injector) {
        return marshallingType -> {
            try {
                final String serialized = this.mapMaid.serializer().serialize(object, marshallingType, injector);
                return then(this.thenData.withSerializationResult(serialized));
            } catch (final Exception e) {
                return then(this.thenData.withException(e));
            }
        };
    }

    public Then theDefinitionsAreQueried() {
        final DebugInformation debugInformation = this.mapMaid.debugInformation();
        return then(this.thenData.withDebugInformation(debugInformation));
    }

    public Then mapMaidIsInstantiated() {
        return then(this.thenData);
    }
}
