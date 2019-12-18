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

package de.quantummaid.mapmaid.mapper.serialization.serializers.collections;

import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.universal.UniversalCollection.universalCollection;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public interface CollectionSerializer extends TypeSerializer {
    List<Object> serialize(Object collection);

    @Override
    default Universal serialize(final Object object,
                                final SerializationCallback callback,
                                final SerializationTracker tracker,
                                final CustomPrimitiveMappings customPrimitiveMappings) {
        final ResolvedType contentType = contentType();
        final List<Universal> list = serialize(object)
                .stream()
                .map(element -> callback.serializeDefinition(contentType, element, tracker))
                .collect(toList());
        return universalCollection(list);
    }

    @Override
    default String classification() {
        return "Collection";
    }

    @Override
    default List<ResolvedType> requiredTypes() {
        return singletonList(this.contentType());
    }

    ResolvedType contentType();
}
