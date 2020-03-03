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

package de.quantummaid.mapmaid.shared.mapping;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TypeMappings {
    private final List<Mapping> mappings;

    public static TypeMappings typeMappings(final Mapping... mappings) {
        NotNullValidator.validateNotNull(mappings, "mappings");
        return new TypeMappings(Arrays.asList(mappings));
    }

    public <T> Optional<T> map(final Object object, final Class<T> to) {
        final Class<?> from = object.getClass();
        return this.mappings.stream()
                .filter(mapping -> mapping.to().equals(to))
                .filter(mapping -> mapping.from().equals(from))
                .findFirst()
                .map(mapping -> mapping.map(object));
    }
}
