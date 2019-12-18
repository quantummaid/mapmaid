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

package de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections;

import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Array;
import java.util.List;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArrayCollectionDeserializer implements CollectionDeserializer {
    private final ResolvedType componentType;

    public static CollectionDeserializer arrayDeserializer(final ResolvedType componentType) {
        return new ArrayCollectionDeserializer(componentType);
    }

    @Override
    public ResolvedType contentType() {
        return this.componentType;
    }

    @Override
    public Object deserialize(final List<Object> deserializedElements) {
        final int size = deserializedElements.size();
        final Object[] array = (Object[]) Array.newInstance(this.componentType.assignableType(), size);
        for (int i = 0; i < size; ++i) {
            array[i] = deserializedElements.get(i);
        }
        return array;
    }

    @Override
    public String classification() {
        return "Collection";
    }
}
