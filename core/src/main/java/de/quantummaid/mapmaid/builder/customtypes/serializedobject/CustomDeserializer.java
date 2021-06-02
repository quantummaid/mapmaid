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

import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields.deserializationFields;
import static de.quantummaid.mapmaid.builder.resolving.framework.identifier.RealTypeIdentifier.realTypeIdentifier;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomDeserializer implements SerializedObjectDeserializer {
    private final InvocableDeserializer<?> invocableDeserializer;
    private final List<CustomDeserializationField> fields;
    private final DeserializationFields deserializationFields;

    public static CustomDeserializer userProvidedDeserializer(final InvocableDeserializer<?> invocableDeserializer,
                                                              final List<CustomDeserializationField> fields) {
        final Map<String, TypeIdentifier> fieldMap = fields.stream()
                .collect(toMap(
                        CustomDeserializationField::name,
                        customDeserializationField -> realTypeIdentifier(customDeserializationField.type()))
                );
        final DeserializationFields deserializationFields = deserializationFields(fieldMap);
        return new CustomDeserializer(invocableDeserializer, fields, deserializationFields);
    }

    @Override
    public DeserializationFields fields() {
        return this.deserializationFields;
    }

    @Override
    public Object deserialize(final Map<String, Object> elements) {
        final Object[] arguments = new Object[this.fields.size()];
        int i = 0;
        for (final CustomDeserializationField field : this.fields) {
            final String name = field.name();
            arguments[i] = elements.get(name);
            ++i;
        }
        return this.invocableDeserializer.invoke(arguments);
    }

    @Override
    public String description() {
        return "user provided";
    }
}
