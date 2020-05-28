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

package de.quantummaid.mapmaid.builder.customcollection;

import de.quantummaid.mapmaid.mapper.serialization.serializers.collections.CollectionSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

import static de.quantummaid.mapmaid.shared.identifier.TypeIdentifier.typeIdentifierFor;
import static lombok.AccessLevel.PRIVATE;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
public final class InlinedCollectionSerializer<T, C> implements CollectionSerializer {
    private final TypeIdentifier contentType;
    private final Function<T, List<C>> listExtractor;

    public static <T, C> InlinedCollectionSerializer<T, C> inlinedCollectionSerializer(final Class<T> customCollectionType,
                                                                                       final Class<C> contentType,
                                                                                       final Function<T, List<C>> listExtractor) {
        final TypeIdentifier contentTypeIdentifier = typeIdentifierFor(contentType);
        return new InlinedCollectionSerializer<>(contentTypeIdentifier, listExtractor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> collectionAsList(final Object collection) {
        final T customCollection = (T) collection;
        return (List<Object>) listExtractor.apply(customCollection);
    }

    @Override
    public TypeIdentifier contentType() {
        return contentType;
    }

    @Override
    public String description() {
        return "custom collection";
    }

}
