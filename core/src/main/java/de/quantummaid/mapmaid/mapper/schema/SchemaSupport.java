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

package de.quantummaid.mapmaid.mapper.schema;

import de.quantummaid.mapmaid.collections.BiMap;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;

import java.util.*;

import static de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObject;
import static de.quantummaid.mapmaid.mapper.universal.UniversalString.universalString;

public final class SchemaSupport {
    private static final String PROPERTIES = "properties";

    private SchemaSupport() {
    }

    public static Map<String, Object> stringConstant(final String value) {
        return Map.of(
                "type", "string",
                "pattern", value);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "CastToConcreteClass"})
    public static Universal schemaForPolymorphicParent(final BiMap<String, TypeIdentifier> nameToType,
                                                       final String typeField,
                                                       final SchemaCallback schemaCallback) {
        final List<Map<String, Object>> schemas = new ArrayList<>(nameToType.size());
        nameToType.forEach((name, implementation) -> {
            final UniversalObject rawImplementationSchema = (UniversalObject) schemaCallback.schema(implementation);
            final Map<String, Object> implementationSchemaMap = (Map) rawImplementationSchema.toNativeJava();
            final Map<String, Object> updated = addProperty(typeField, stringConstant(name), implementationSchemaMap);
            schemas.add(updated);
        });
        return UniversalObject.universalObjectFromNativeMap(Map.of("oneOf", schemas));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> addProperty(final String key,
                                                   final Map<String, Object> childSchema,
                                                   final Map<String, Object> objectSchema) {
        final Map<String, Object> properties = new LinkedHashMap<>((Map<String, Object>) objectSchema.get(PROPERTIES));
        properties.put(key, childSchema);
        final Map<String, Object> copy = new LinkedHashMap<>(objectSchema);
        copy.put(PROPERTIES, properties);
        return copy;
    }

    public static Universal schemaForObject(final Map<String, TypeIdentifier> fields,
                                            final SchemaCallback schemaCallback) {
        final Map<String, Universal> properties = new LinkedHashMap<>(fields.size());
        fields.forEach((key, typeIdentifier) -> {
            final Universal childSchema = schemaCallback.schema(typeIdentifier);
            properties.put(key, childSchema);
        });

        final Map<String, Universal> map = new HashMap<>();
        map.put("type", universalString("object"));
        map.put(PROPERTIES, universalObject(properties));
        return universalObject(map);
    }

    public static Universal schemaForCollection(final TypeIdentifier contentType,
                                                final SchemaCallback schemaCallback) {
        final Map<String, Universal> map = new LinkedHashMap<>();
        map.put("type", universalString("array"));
        final Universal contentTypeSchema = schemaCallback.schema(contentType);
        map.put("items", contentTypeSchema);
        return universalObject(map);
    }
}
