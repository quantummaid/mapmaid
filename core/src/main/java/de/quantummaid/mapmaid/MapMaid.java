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

package de.quantummaid.mapmaid;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import de.quantummaid.mapmaid.mapper.injector.InjectorLambda;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.serialization.Serializer;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMaid {
    private final Serializer serializer;
    private final Deserializer deserializer;
    private final DebugInformation debugInformation;

    public static MapMaidBuilder aMapMaid() {
        return MapMaidBuilder.mapMaidBuilder();
    }

    public static MapMaid mapMaid(final Serializer serializer,
                                  final Deserializer deserializer,
                                  final DebugInformation debugInformation) {
        return new MapMaid(serializer, deserializer, debugInformation);
    }

    public Serializer serializer() {
        return this.serializer;
    }

    public Deserializer deserializer() {
        return this.deserializer;
    }

    public String serializeToJson(final Object object) {
        return this.serializer.serializeToJson(object);
    }

    public String serializeToYaml(final Object object) {
        return this.serializer.serialize(object, MarshallingType.yaml());
    }

    public String serializeToXml(final Object object) {
        return this.serializer.serialize(object, MarshallingType.xml());
    }

    public String serializeTo(final Object object, final MarshallingType marshallingType) {
        return this.serializer.serialize(object, marshallingType);
    }

    public String serializeTo(final Object object, final MarshallingType marshallingType, final GenericType<?> type) {
        final ResolvedType resolvedType = type.toResolvedType();
        return this.serializer.serialize(object, resolvedType, marshallingType, input -> input);
    }

    public <T> T deserializeJson(final String json, final Class<T> targetType) {
        return this.deserializer.deserializeJson(json, targetType);
    }

    public <T> T deserializeJson(final String json, final Class<T> targetType, final InjectorLambda injector) {
        return this.deserializer.deserializeJson(json, targetType, injector);
    }

    public <T> T deserializeYaml(final String yaml, final Class<T> targetType) {
        return this.deserializer.deserialize(yaml, targetType, MarshallingType.yaml());
    }

    public <T> T deserializeYaml(final String yaml, final Class<T> targetType, final InjectorLambda injector) {
        return this.deserializer.deserialize(yaml, targetType, MarshallingType.yaml(), injector);
    }

    public <T> T deserializeXml(final String xml, final Class<T> targetType) {
        return this.deserializer.deserialize(xml, targetType, MarshallingType.xml());
    }

    public <T> T deserializeXml(final String xml, final Class<T> targetType, final InjectorLambda injector) {
        return this.deserializer.deserialize(xml, targetType, MarshallingType.xml(), injector);
    }

    public <T> T deserialize(final String input, final Class<T> targetType, final MarshallingType marshallingType) {
        return this.deserializer.deserialize(input, targetType, marshallingType);
    }

    public <T> T deserialize(final String input, final ResolvedType targetType, final MarshallingType marshallingType) {
        return this.deserializer.deserialize(input, targetType, marshallingType);
    }

    public DebugInformation debugInformation() {
        return this.debugInformation;
    }
}
