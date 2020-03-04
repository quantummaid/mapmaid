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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject;

import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.nonNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EquivalenceClass {
    private final int size;
    private SerializationFieldInstantiation serializationFields;
    private final List<TypeDeserializer> deserializers;

    public static EquivalenceClass equivalenceClass(final int size) {
        return new EquivalenceClass(size, smallList());
    }

    public void setSerializationFields(final SerializationFieldInstantiation serializationFields) {
        validateNotNull(serializationFields, "serializer");
        if (this.serializationFields != null) {
            throw new UnsupportedOperationException("serialized fields can only be set once");
        }
        this.serializationFields = serializationFields;
    }

    public void addDeserializer(final TypeDeserializer deserializer) {
        validateNotNull(deserializer, "deserializer");
        this.deserializers.add(deserializer);
    }

    public boolean fullySupported() {
        return nonNull(this.serializationFields) && !this.deserializers.isEmpty();
    }

    public int size() {
        return this.size;
    }

    public SerializationFieldInstantiation serializationFields() {
        return this.serializationFields;
    }

    public List<TypeDeserializer> deserializers() {
        return this.deserializers;
    }

    public String describe() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fields:\n");
        stringBuilder.append(this.serializationFields.describe());
        stringBuilder.append("\n");

        stringBuilder.append("deserializers:\n");
        this.deserializers.forEach(deserializer -> {
            stringBuilder.append(deserializer.description());
            stringBuilder.append("\n");
        });
        return stringBuilder.toString();
    }
}
