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

package de.quantummaid.mapmaid.mapper.serialization.serializers.collections;

import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.quantummaid.reflectmaid.typescanner.TypeIdentifier.typeIdentifierFor;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListCollectionSerializer implements CollectionSerializer {
    private final ResolvedType type;

    public static CollectionSerializer listSerializer(final ResolvedType type) {
        return new ListCollectionSerializer(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> collectionAsList(final Object collection) {
        final Collection<Object> realCollection = (Collection<Object>) collection;
        return new ArrayList<>(realCollection);
    }

    @Override
    public TypeIdentifier contentType() {
        return typeIdentifierFor(this.type);
    }

    @Override
    public String description() {
        return format("serializing a collection with content type '%s'", this.type.description());
    }
}
