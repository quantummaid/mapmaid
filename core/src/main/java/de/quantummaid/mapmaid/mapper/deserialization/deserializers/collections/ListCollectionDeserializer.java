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

import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static de.quantummaid.mapmaid.builder.resolving.framework.identifier.RealTypeIdentifier.realTypeIdentifier;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListCollectionDeserializer implements CollectionDeserializer {
    private final ResolvedType componentType;
    private final Function<List<Object>, Collection<Object>> mapper;

    public static CollectionDeserializer listDeserializer(final ResolvedType componentType,
                                                          final Function<List<Object>, Collection<Object>> mapper) {
        validateNotNull(componentType, "componentType");
        validateNotNull(mapper, "mapper");
        return new ListCollectionDeserializer(componentType, mapper);
    }

    @Override
    public Object listToCollection(final List<Object> deserializedElements) {
        return this.mapper.apply(deserializedElements);
    }

    @Override
    public TypeIdentifier contentType() {
        return realTypeIdentifier(this.componentType);
    }

    @Override
    public String description() {
        return format("deserializing a collection with content type '%s'", this.componentType.description());
    }
}
