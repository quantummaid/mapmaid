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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.CustomDeserializationField.deserializationField;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.CustomDeserializer.userProvidedDeserializer;
import static de.quantummaid.mapmaid.builder.customtypes.serializedobject.CustomSerializationField.customSerializationField;
import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField.serializationField;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields.serializationFields;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder {
    private final ReflectMaid reflectMaid;
    private final TypeIdentifier type;
    private final List<CustomDeserializationField> deserializationFields;
    private final List<CustomSerializationField> serializationFields;
    private InvocableDeserializer<?> deserializer;

    public static Builder emptyBuilder(final ReflectMaid reflectMaid,
                                       final TypeIdentifier type) {
        return new Builder(reflectMaid, type, smallList(), smallList());
    }

    public void addDuplexField(final GenericType<?> type, final String name, final Query<Object, Object> query) {
        with(type, name);
        addSerializationField(type, name, query);
    }

    public void with(final GenericType<?> type, final String name) {
        validateNotNull(type, "type");
        validateNotNull(name, "name");
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final CustomDeserializationField deserializationField = deserializationField(resolvedType, name);
        this.deserializationFields.add(deserializationField);
    }

    public void addSerializationField(final GenericType<?> type, final String name, final Query<Object, Object> query) {
        validateNotNull(type, "type");
        validateNotNull(name, "name");
        final ResolvedType resolvedType = reflectMaid.resolve(type);
        final CustomSerializationField serializationField = customSerializationField(resolvedType, name, query);
        this.serializationFields.add(serializationField);
    }

    public void setDeserializer(final InvocableDeserializer<?> deserializer) {
        validateNotNull(deserializer, "deserializer");
        this.deserializer = deserializer;
    }

    public TypeIdentifier getType() {
        return this.type;
    }

    public TypeDeserializer createDeserializer() {
        return userProvidedDeserializer(this.deserializer, this.deserializationFields);
    }

    public TypeSerializer createSerializer() {
        final List<SerializationField> serializationFieldList = this.serializationFields.stream()
                .map(field -> serializationField(field.type(), field.name(), field::query))
                .collect(toList());
        final SerializationFields fields = serializationFields(serializationFieldList);
        return serializedObjectSerializer(fields);
    }
}
