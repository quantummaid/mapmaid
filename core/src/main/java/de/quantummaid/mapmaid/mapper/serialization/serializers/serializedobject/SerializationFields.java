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

package de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject;

import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationFields {
    private final List<SerializationField> fields;

    public static SerializationFields serializationFields(final List<SerializationField> fields) {
        NotNullValidator.validateNotNull(fields, "fields");
        return new SerializationFields(fields);
    }

    public static SerializationFields empty() {
        return new SerializationFields(emptyList());
    }

    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    public List<SerializationField> fields() {
        return unmodifiableList(this.fields);
    }

    public List<TypeIdentifier> typesList() {
        return this.fields.stream()
                .map(SerializationField::type)
                .collect(toUnmodifiableList());
    }

    public String describe() {
        return this.fields.stream()
                .map(SerializationField::describe)
                .map(s -> "\t- " + s)
                .collect(joining("\n"));
    }
}
