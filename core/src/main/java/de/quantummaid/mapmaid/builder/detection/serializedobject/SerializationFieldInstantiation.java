/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.failure;
import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationFields.serializationFields;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializedObjectSerializer.serializedObjectSerializer;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationFieldInstantiation {
    private final Map<String, List<SerializationField>> fields;

    public static SerializationFieldInstantiation serializationFieldInstantiation(final Map<String, List<SerializationField>> fields) {
        return new SerializationFieldInstantiation(fields);
    }

    public DetectionResult<TypeSerializer> instantiate(final Preferences<SerializationField> preferences,
                                                       final ScanInformationBuilder scanInformationBuilder) {
        final List<SerializationField> serializationFieldList = new ArrayList<>(this.fields.size());
        final List<String> problems = smallList();
        this.fields.forEach((name, fields) -> {
            final List<SerializationField> preferredFields = preferences.preferred(fields, scanInformationBuilder::ignoreSerializationField);
            if (preferredFields.size() != 1) {
                final String fieldsString = preferredFields.stream()
                        .map(SerializationField::describe)
                        .collect(joining(", ", "[", "]"));
                problems.add(format("cannot decide between %s", fieldsString));
                return;
            }
            final SerializationField preferredField = preferredFields.get(0);
            serializationFieldList.add(preferredField);
        });

        if (!problems.isEmpty()) {
            return failure(problems);
        }

        final SerializationFields serializationFields = serializationFields(serializationFieldList);
        return success(serializedObjectSerializer(serializationFields));
    }

    public String describe() {
        final StringJoiner joiner = new StringJoiner("\n");
        this.fields.forEach((name, serializationFields) -> joiner.add(
                format("\t- '%s' with query options '%s'", name, describeFieldList(serializationFields))
        ));
        return joiner.toString();
    }

    private static String describeFieldList(final List<SerializationField> fieldsList) {
        return fieldsList.stream()
                .map(SerializationField::describe)
                .collect(joining(", ", "[", "]"));
    }
}
