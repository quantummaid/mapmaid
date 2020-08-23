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

package de.quantummaid.mapmaid.customtypes.serializedobject;

import de.quantummaid.mapmaid.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.GenericType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.customtypes.serializedobject.CustomDeserializationField.deserializationField;
import static de.quantummaid.reflectmaid.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Builder {
    private final TypeIdentifier type;
    private final List<CustomDeserializationField> deserializationFields;
    private final List<CustomSerializationField> serializationFields;
    private InvocableDeserializer<?> deserializer;

    public static Builder emptyBuilder(final TypeIdentifier type) {
        return new Builder(type, new ArrayList<>(), new ArrayList<>());
    }

    public void addDuplexField(final GenericType<?> type, final String name, final Query<Object, Object> query) {
        with(type, name);
        addSerializationField(type, name, query);
    }

    public void with(final GenericType<?> type, final String name) {
        validateNotNull(type, "type");
        validateNotNull(name, "name");
        final CustomDeserializationField deserializationField = deserializationField(type, name);
        this.deserializationFields.add(deserializationField);
    }

    public void addSerializationField(final GenericType<?> type, final String name, final Query<Object, Object> query) {
        validateNotNull(type, "type");
        validateNotNull(name, "name");
        final CustomSerializationField serializationField = CustomSerializationField.serializationField(type, name, query);
        this.serializationFields.add(serializationField);
    }

    public void setDeserializer(final InvocableDeserializer<?> deserializer) {
        validateNotNull(deserializer, "deserializer");
        this.deserializer = deserializer;
    }

    public TypeIdentifier getType() {
        return this.type;
    }

    /*
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
     */
}
