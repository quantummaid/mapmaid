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

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldInstantiation.serializationFieldInstantiation;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationFieldOptions {
    private final Map<String, List<SerializationField>> options;

    public static SerializationFieldOptions serializationFieldOptions() {
        return new SerializationFieldOptions(smallMap());
    }

    public List<SerializationField> allFields() {
        return this.options.values().stream()
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public SerializationFieldOptions filter(final Predicate<SerializationField> filter) {
        final Map<String, List<SerializationField>> filtered = new HashMap<>(this.options.size());
        this.options.forEach((name, serializationFields) -> {
            final List<SerializationField> filteredFields = serializationFields.stream()
                    .filter(filter)
                    .collect(toList());
            if (!filteredFields.isEmpty()) {
                filtered.put(name, filteredFields);
            }
        });
        return new SerializationFieldOptions(filtered);
    }

    public boolean isEmpty() {
        return this.options.isEmpty();
    }

    public void add(final SerializationField field) {
        validateNotNull(field, "field");
        final String name = field.name();
        if (!this.options.containsKey(name)) {
            this.options.put(name, smallList());
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

    public DetectionResult<SerializationFieldInstantiation> instantiateAll() {
        return success(serializationFieldInstantiation(this.options));
    }

    public DetectionResult<SerializationFieldInstantiation> instantiate(final Map<String, TypeIdentifier> fields) {
        final Map<String, List<SerializationField>> instantiableFields = new HashMap<>(fields.size());
        final List<String> problems = smallList();
        for (final Map.Entry<String, TypeIdentifier> entry : fields.entrySet()) {
            final String name = entry.getKey();
            if (!this.options.containsKey(name)) {
                problems.add(format("No field under the name '%s'", name));
                continue;
            }
            final List<SerializationField> fieldsByName = this.options.get(name);
            final TypeIdentifier type = entry.getValue();
            final List<SerializationField> mirroredFields = mirroredFields(type, fieldsByName);
            if (mirroredFields.isEmpty()) {
                problems.add(format("No field under name '%s' of a type similar to '%s'", name, type.description()));
            }
            instantiableFields.put(name, mirroredFields);
        }

        if (!problems.isEmpty()) {
            return failure(problems);
        }

        final SerializationFieldInstantiation instantiation = serializationFieldInstantiation(instantiableFields);
        return success(instantiation);
    }

    private static List<SerializationField> mirroredFields(final TypeIdentifier type,
                                                           final List<SerializationField> options) {
        validateNotNull(type, "type");
        validateNotNull(options, "options");
        return options.stream()
                .filter(field -> Mirror.mirrors(field.type(), type))
                .collect(toList());
    }
}
