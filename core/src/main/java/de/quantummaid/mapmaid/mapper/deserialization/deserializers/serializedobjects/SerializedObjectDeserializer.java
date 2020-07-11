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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializationFields;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.schema.SchemaCallback;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalNull;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer.castSafely;
import static de.quantummaid.mapmaid.mapper.schema.SchemaSupport.schemaForObject;
import static de.quantummaid.mapmaid.mapper.universal.UniversalNull.universalNull;
import static java.lang.String.format;

public interface SerializedObjectDeserializer extends TypeDeserializer {

    static String createDescription(final String deserializer) {
        return format("as serialized object using %s", deserializer);
    }

    @Override
    default List<TypeIdentifier> requiredTypes() {
        return fields().referencedTypes();
    }

    DeserializationFields fields();

    Object deserialize(Map<String, Object> elements) throws Exception; // NOSONAR

    @SuppressWarnings("unchecked")
    @Override
    default <T> T deserialize(final Universal input,
                              final ExceptionTracker exceptionTracker,
                              final Injector injector,
                              final DeserializerCallback callback,
                              final CustomPrimitiveMappings customPrimitiveMappings,
                              final TypeIdentifier typeIdentifier,
                              final DebugInformation debugInformation) {
        if (input instanceof UniversalNull) {
            return null;
        }
        final UniversalObject universalObject = castSafely(input, UniversalObject.class, exceptionTracker, typeIdentifier, debugInformation);
        final DeserializationFields deserializationFields = fields();
        final Map<String, Object> elements = smallMap();
        for (final Entry<String, TypeIdentifier> entry : deserializationFields.fields().entrySet()) {
            final String elementName = entry.getKey();
            final TypeIdentifier elementType = entry.getValue();

            final Universal elementInput = universalObject.getField(elementName).orElse(universalNull());
            final Object elementObject = callback.deserializeRecursive(
                    elementInput,
                    elementType,
                    exceptionTracker.stepInto(elementName),
                    injector,
                    debugInformation);
            elements.put(elementName, elementObject);
        }

        if (exceptionTracker.validationResult().hasValidationErrors()) {
            return null;
        } else {
            try {
                return (T) deserialize(elements);
            } catch (final Exception e) {
                final String message = format("Exception calling deserialize(elements: %s) on deserializationMethod %s",
                        elements, this);
                exceptionTracker.track(e, message);
                return null;
            }
        }
    }

    @Override
    default Universal schema(final SchemaCallback schemaCallback) {
        final Map<String, TypeIdentifier> fields = fields().fields();
        return schemaForObject(fields, schemaCallback);
    }
}
