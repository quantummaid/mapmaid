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
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.mapper.injector.InjectorLambda.noop;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.reflectmaid.GenericType.genericType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"java:S1448", "java:S1192"})
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
        validateNotNull(object, "object");
        final Class<?> type = object.getClass();
        return serializeToJson(object, type);
    }

    public String serializeToJson(final Object object,
                                  final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return serializeToJson(object, genericType);
    }

    public String serializeToJson(final Object object,
                                  final GenericType<?> type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializeToJson(object, typeIdentifier);
    }

    public String serializeToJson(final Object object,
                                  final TypeIdentifier type) {
        return serializeTo(object, JSON, type);
    }

    public String serializeToYaml(final Object object) {
        validateNotNull(object, "object");
        final Class<?> type = object.getClass();
        return serializeToYaml(object, type);
    }

    public String serializeToYaml(final Object object,
                                  final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return serializeToYaml(object, genericType);
    }

    public String serializeToYaml(final Object object,
                                  final GenericType<?> type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializeToYaml(object, typeIdentifier);
    }

    public String serializeToYaml(final Object object,
                                  final TypeIdentifier type) {
        return serializeTo(object, MarshallingType.YAML, type);
    }

    public String serializeToXml(final Object object) {
        validateNotNull(object, "object");
        final Class<?> type = object.getClass();
        return serializeToXml(object, type);
    }

    public String serializeToXml(final Object object,
                                 final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return serializeToXml(object, genericType);
    }

    public String serializeToXml(final Object object,
                                 final GenericType<?> type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializeToXml(object, typeIdentifier);
    }

    public String serializeToXml(final Object object,
                                 final TypeIdentifier type) {
        return serializeTo(object, MarshallingType.XML, type);
    }

    public <T> T serializeTo(final Object object,
                             final MarshallingType<T> marshallingType) {
        validateNotNull(object, "object");
        final Class<?> type = object.getClass();
        return serializeTo(object, marshallingType, type);
    }

    public <T> T serializeTo(final Object object,
                             final MarshallingType<T> marshallingType,
                             final Class<?> type) {
        final GenericType<?> genericType = genericType(type);
        return serializeTo(object, marshallingType, genericType);
    }

    public <T> T serializeTo(final Object object,
                             final MarshallingType<T> marshallingType,
                             final GenericType<?> type) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializeTo(object, marshallingType, typeIdentifier);
    }

    public <T> T serializeTo(final Object object,
                             final MarshallingType<T> marshallingType,
                             final TypeIdentifier type) {
        return this.serializer.serialize(object, type, marshallingType, input -> input);
    }

    public <T> T deserializeJson(final String json,
                                 final Class<T> targetType) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeJson(json, genericType);
    }

    public <T> T deserializeJson(final String json,
                                 final GenericType<T> targetType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeJson(json, typeIdentifier);
    }

    public <T> T deserializeJson(final String json,
                                 final TypeIdentifier targetType) {
        return this.deserializeJson(json, targetType, noop());
    }

    public <T> T deserializeJson(final String json,
                                 final Class<T> targetType,
                                 final InjectorLambda injector) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeJson(json, genericType, injector);
    }

    public <T> T deserializeJson(final String json,
                                 final GenericType<T> targetType,
                                 final InjectorLambda injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeJson(json, typeIdentifier, injector);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeJson(final String json,
                                 final TypeIdentifier targetType,
                                 final InjectorLambda injector) {
        return (T) this.deserialize(json, targetType, JSON, injector);
    }

    public <T> T deserializeYaml(final String yaml,
                                 final Class<T> targetType) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeYaml(yaml, genericType);
    }

    public <T> T deserializeYaml(final String yaml,
                                 final GenericType<T> targetType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeYaml(yaml, typeIdentifier);
    }

    public <T> T deserializeYaml(final String yaml,
                                 final TypeIdentifier targetType) {
        return this.deserializeYaml(yaml, targetType, noop());
    }

    public <T> T deserializeYaml(final String yaml,
                                 final Class<T> targetType,
                                 final InjectorLambda injector) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeYaml(yaml, genericType, injector);
    }

    public <T> T deserializeYaml(final String yaml,
                                 final GenericType<T> targetType,
                                 final InjectorLambda injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeYaml(yaml, typeIdentifier, injector);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeYaml(final String yaml,
                                 final TypeIdentifier targetType,
                                 final InjectorLambda injector) {
        return (T) this.deserialize(yaml, targetType, MarshallingType.YAML, injector);
    }

    public <T> T deserializeXml(final String xml,
                                final Class<T> targetType) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeXml(xml, genericType);
    }

    public <T> T deserializeXml(final String xml,
                                final GenericType<T> targetType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeXml(xml, typeIdentifier);
    }

    public <T> T deserializeXml(final String xml,
                                final TypeIdentifier targetType) {
        return this.deserializeXml(xml, targetType, noop());
    }

    public <T> T deserializeXml(final String xml,
                                final Class<T> targetType,
                                final InjectorLambda injector) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserializeXml(xml, genericType, injector);
    }

    public <T> T deserializeXml(final String xml,
                                final GenericType<T> targetType,
                                final InjectorLambda injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserializeXml(xml, typeIdentifier, injector);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializeXml(final String xml,
                                final TypeIdentifier targetType,
                                final InjectorLambda injector) {
        return (T) this.deserialize(xml, targetType, MarshallingType.XML, injector);
    }

    public <T, M> T deserialize(final M input,
                                final Class<T> targetType,
                                final MarshallingType<M> marshallingType) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserialize(input, genericType, marshallingType);
    }

    public <T, M> T deserialize(final M input,
                                final GenericType<T> targetType,
                                final MarshallingType<M> marshallingType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return this.deserialize(input, typeIdentifier, marshallingType);
    }

    public <T, M> T deserialize(final M input,
                                final TypeIdentifier targetType,
                                final MarshallingType<M> marshallingType) {
        return this.deserialize(input, targetType, marshallingType, noop());
    }

    public <T, M> T deserialize(final M input,
                                final Class<T> targetType,
                                final MarshallingType<M> marshallingType,
                                final InjectorLambda injector) {
        final GenericType<T> genericType = genericType(targetType);
        return this.deserialize(input, genericType, marshallingType, injector);
    }

    public <T, M> T deserialize(final M input,
                                final GenericType<T> targetType,
                                final MarshallingType<M> marshallingType,
                                final InjectorLambda injector) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(targetType);
        return deserialize(input, typeIdentifier, marshallingType, injector);
    }

    @SuppressWarnings("unchecked")
    public <T, M> T deserialize(final M input,
                                final TypeIdentifier targetType,
                                final MarshallingType<M> marshallingType,
                                final InjectorLambda injector) {
        return (T) this.deserializer.deserialize(input, targetType, marshallingType, injector);
    }

    public DebugInformation debugInformation() {
        return this.debugInformation;
    }

    public <M> M deserializationSchemaFor(final Class<?> type,
                                          final MarshallingType<M> marshallingType) {
        final GenericType<?> genericType = genericType(type);
        return deserializationSchemaFor(genericType, marshallingType);
    }

    public <M> M deserializationSchemaFor(final GenericType<?> type,
                                          final MarshallingType<M> marshallingType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return deserializationSchemaFor(typeIdentifier, marshallingType);
    }

    public <M> M deserializationSchemaFor(final TypeIdentifier type,
                                          final MarshallingType<M> marshallingType) {
        final Universal schema = this.deserializer.schema(type);
        return serializer.marshalFromUniversalObject(schema.toNativeJava(), marshallingType);
    }

    public <M> M serializationSchemaFor(final Class<?> type,
                                        final MarshallingType<M> marshallingType) {
        final GenericType<?> genericType = genericType(type);
        return serializationSchemaFor(genericType, marshallingType);
    }

    public <M> M serializationSchemaFor(final GenericType<?> type,
                                        final MarshallingType<M> marshallingType) {
        final TypeIdentifier typeIdentifier = typeIdentifierFor(type);
        return serializationSchemaFor(typeIdentifier, marshallingType);
    }

    public <M> M serializationSchemaFor(final TypeIdentifier type,
                                        final MarshallingType<M> marshallingType) {
        final Universal schema = this.serializer.schema(type);
        return serializer.marshalFromUniversalObject(schema.toNativeJava(), marshallingType);
    }
}
