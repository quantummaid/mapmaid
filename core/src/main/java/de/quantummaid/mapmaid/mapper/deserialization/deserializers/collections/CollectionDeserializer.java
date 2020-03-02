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

import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalCollection;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer.castSafely;
import static java.util.Collections.singletonList;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface CollectionDeserializer extends TypeDeserializer {

    ResolvedType contentType();

    @Override
    default List<ResolvedType> requiredTypes() {
        return singletonList(contentType());
    }

    @Override
    default Class<? extends Universal> universalRequirement() {
        return UniversalCollection.class;
    }

    @Override
    default String classification() {
        return "Collection";
    }

    Object deserialize(List<Object> deserializedElements);

    @Override
    default <T> T deserialize(final Universal input,
                              final ExceptionTracker exceptionTracker,
                              final Injector injector,
                              final DeserializerCallback callback,
                              final CustomPrimitiveMappings customPrimitiveMappings) {
        final UniversalCollection universalCollection = castSafely(input, UniversalCollection.class, exceptionTracker);
        final List deserializedList = new LinkedList();
        final ResolvedType contentType = contentType();
        int index = 0;
        for (final Universal element : universalCollection.content()) {
            final Object deserialized = callback.deserializeRecursive(element, contentType, exceptionTracker.stepIntoArray(index), injector);
            deserializedList.add(deserialized);
            index = index + 1;
        }
        return (T) deserialize(deserializedList);
    }
}
