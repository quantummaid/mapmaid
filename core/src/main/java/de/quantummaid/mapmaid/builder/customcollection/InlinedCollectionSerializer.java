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

import de.quantummaid.mapmaid.mapper.generation.ManualRegistration;
import de.quantummaid.mapmaid.mapper.serialization.serializers.collections.CollectionSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.mapper.generation.ManualRegistration.emptyManualRegistration;
import static lombok.AccessLevel.PRIVATE;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
public final class InlinedCollectionSerializer implements CollectionSerializer {
    private final TypeIdentifier contentType;
    private final InlinedCollectionListExtractor<Object, Object> listExtractor;

    public static InlinedCollectionSerializer inlinedCollectionSerializer(
            final TypeIdentifier contentTypeIdentifier,
            final InlinedCollectionListExtractor<Object, Object> listExtractor) {
        return new InlinedCollectionSerializer(contentTypeIdentifier, listExtractor);
    }

    @Override
    public List<Object> collectionAsList(final Object collection) {
        return this.listExtractor.extract(collection);
    }

    @Override
    public TypeIdentifier contentType() {
        return this.contentType;
    }

    @Override
    public String description() {
        return "custom collection";
    }

    @Override
    public ManualRegistration manualRegistration(final ResolvedType type) {
        return emptyManualRegistration();
    }
}
