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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeserializationFields {
    private final Map<String, TypeIdentifier> elements;

    public static DeserializationFields deserializationFields(final Map<String, TypeIdentifier> elements) {
        return new DeserializationFields(elements);
    }

    public Map<String, TypeIdentifier> fields() {
        return unmodifiableMap(this.elements);
    }

    public int size() {
        return this.elements.size();
    }

    public List<TypeIdentifier> referencedTypes() {
        return this.elements.values()
                .stream()
                .distinct()
                .collect(toList());
    }
}
