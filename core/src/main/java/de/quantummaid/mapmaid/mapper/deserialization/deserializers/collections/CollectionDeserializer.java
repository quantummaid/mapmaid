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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.schema.SchemaCallback;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalCollection;
import de.quantummaid.mapmaid.mapper.universal.UniversalNull;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer.castSafely;
import static de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObject;
import static de.quantummaid.mapmaid.mapper.universal.UniversalString.universalString;
import static java.util.Collections.singletonList;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface CollectionDeserializer extends TypeDeserializer {

    TypeIdentifier contentType();

    @Override
    default List<TypeIdentifier> requiredTypes() {
        return singletonList(contentType());
    }

    Object listToCollection(List<Object> deserializedElements);

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
        final UniversalCollection universalCollection = castSafely(input, UniversalCollection.class, exceptionTracker, typeIdentifier, debugInformation);
        final List<Object> deserializedList = new ArrayList<>(10);
        final TypeIdentifier contentType = contentType();
        int index = 0;
        for (final Universal element : universalCollection.content()) {
            final Object deserialized = callback.deserializeRecursive(element, contentType, exceptionTracker.stepIntoArray(index), injector, debugInformation);
            deserializedList.add(deserialized);
            index = index + 1;
        }
        return (T) listToCollection(deserializedList);
    }

    @Override
    default Universal schema(final SchemaCallback schemaCallback) {
        final Map<String, Universal> map = new LinkedHashMap<>();
        map.put("type", universalString("array"));
        final TypeIdentifier contentType = contentType();
        final Universal contentTypeSchema = schemaCallback.schema(contentType);
        map.put("items", contentTypeSchema);
        return universalObject(map);
    }
}
