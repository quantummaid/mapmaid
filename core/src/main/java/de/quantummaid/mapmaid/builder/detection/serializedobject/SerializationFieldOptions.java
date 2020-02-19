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

package de.quantummaid.mapmaid.builder.detection.serializedobject;

import de.quantummaid.mapmaid.builder.resolving.hints.Mirror;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields.serializationFields;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationFieldOptions {
    private final Map<String, List<SerializationField>> options;

    public static SerializationFieldOptions serializationFieldOptions() {
        return new SerializationFieldOptions(new HashMap<>());
    }

    public boolean isEmpty() {
        return this.options.isEmpty();
    }

    public void add(final SerializationField field) {
        validateNotNull(field, "field");
        final String name = field.name();
        if (!this.options.containsKey(name)) {
            this.options.put(name, new ArrayList<>(2));
        }
        this.options.get(name).add(field);
    }

    public boolean contains(final String name, final ResolvedType type) {
        if (!this.options.containsKey(name)) {
            return false;
        }
        return this.options.get(name).stream()
                .map(SerializationField::type)
                .anyMatch(type::equals);
    }

    // TODO
    public TypeSerializer instantiateAll() {
        final List<SerializationField> fields = new ArrayList<>(this.options.size());
        this.options.forEach((name, serializationFields) -> {
            if (serializationFields.size() > 1) {
                throw new UnsupportedOperationException(); // TODO
            }
            fields.add(serializationFields.get(0));
        });
        return serializedObjectSerializer(serializationFields(fields));
    }

    public Optional<TypeSerializer> instantiate(final Map<String, ResolvedType> fields) {
        if (fields.isEmpty()) {
            return empty(); // TODO
        }
        final List<SerializationField> serializationFields = new ArrayList<>(fields.size());
        for (final Map.Entry<String, ResolvedType> entry : fields.entrySet()) {
            final String name = entry.getKey();
            if (!this.options.containsKey(name)) {
                return empty();
            }
            final List<SerializationField> options = this.options.get(name);
            final ResolvedType type = entry.getValue();
            final Optional<SerializationField> selection = select(type, options);
            if (selection.isEmpty()) {
                return empty();
            }
            serializationFields.add(selection.get());
        }

        return of(serializedObjectSerializer(serializationFields(serializationFields)));
    }


    private Optional<SerializationField> select(final ResolvedType type,
                                                final List<SerializationField> options) {
        return options.stream()
                .filter(field -> Mirror.mirrors(field.type(), type))
                .findFirst();
    }
}
