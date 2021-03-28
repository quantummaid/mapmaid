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

package de.quantummaid.mapmaid.builder.builder;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionFactory;
import de.quantummaid.mapmaid.builder.customcollection.InlinedCollectionListExtractor;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface CustomCollectionBuilder {

    default <C, T> MapMaidBuilder serializingInlinedCollection(final Class<C> collectionType,
                                                               final Class<T> contentType,
                                                               final InlinedCollectionListExtractor<C, T> listExtractor) {
        final GenericType<C> genericCollectionType = genericType(collectionType);
        final GenericType<T> genericContentType = genericType(contentType);
        return serializingInlinedCollection(genericCollectionType, genericContentType, listExtractor);
    }

    <C, T> MapMaidBuilder serializingInlinedCollection(GenericType<C> collectionType,
                                                       GenericType<T> contentType,
                                                       InlinedCollectionListExtractor<C, T> listExtractor);

    default <C, T> MapMaidBuilder deserializingInlinedCollection(final Class<C> collectionType,
                                                                 final Class<T> contentType,
                                                                 final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> genericCollectionType = genericType(collectionType);
        final GenericType<T> genericContentType = genericType(contentType);
        return deserializingInlinedCollection(genericCollectionType, genericContentType, collectionFactory);
    }

    <C, T> MapMaidBuilder deserializingInlinedCollection(GenericType<C> collectionType,
                                                         GenericType<T> contentType,
                                                         InlinedCollectionFactory<C, T> collectionFactory);

    default <C, T> MapMaidBuilder serializingAndDeserializingInlinedCollection(final Class<C> collectionType,
                                                                               final Class<T> contentType,
                                                                               final InlinedCollectionListExtractor<C, T> listExtractor,
                                                                               final InlinedCollectionFactory<C, T> collectionFactory) {
        final GenericType<C> genericCollectionType = genericType(collectionType);
        final GenericType<T> genericContentType = genericType(contentType);
        return serializingAndDeserializingInlinedCollection(genericCollectionType, genericContentType, listExtractor, collectionFactory);
    }

    <C, T> MapMaidBuilder serializingAndDeserializingInlinedCollection(GenericType<C> collectionType,
                                                                       GenericType<T> contentType,
                                                                       InlinedCollectionListExtractor<C, T> listExtractor,
                                                                       InlinedCollectionFactory<C, T> collectionFactory);
}
