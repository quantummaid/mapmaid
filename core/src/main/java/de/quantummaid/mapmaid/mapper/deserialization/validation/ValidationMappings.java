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

package de.quantummaid.mapmaid.mapper.deserialization.validation;

import java.util.*;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

public final class ValidationMappings {

    private final Map<Class<? extends Throwable>, ExceptionMappingList<Throwable>> validationMappings;

    private ValidationMappings() {
        this.validationMappings = new HashMap<>(0);
    }

    public static ValidationMappings empty() {
        return new ValidationMappings();
    }

    public void putOneToOne(final Class<? extends Throwable> exception, final ExceptionMappingWithPropertyPath<Throwable> m) {
        this.putAll(exception, (t, p) -> List.of(m.map(t, p)));
    }

    public void putOneToMany(final Class<? extends Throwable> exception, final ExceptionMappingList<Throwable> m) {
        this.putAll(exception, m);
    }

    private void putAll(final Class<? extends Throwable> exception, final ExceptionMappingList<Throwable> m) {
        this.validationMappings.merge(exception, m, (a, b) -> (t, propertyPath) -> {
            final List<ValidationError> map1 = a.map(t, propertyPath);
            final List<ValidationError> map2 = b.map(t, propertyPath);
            final List<ValidationError> map = new ArrayList<>(map1.size() + map2.size());
            map.addAll(map1);
            map.addAll(map2);
            return map;
        });
    }

    public Optional<ExceptionMappingList<Throwable>> get(final Class<? extends Throwable> throwable) {
        final ExceptionMappingList<Throwable> mapping = this.validationMappings.get(throwable);
        if (mapping != null) {
            return of(mapping);
        }

        final Set<Class<? extends Throwable>> assignableClasses = this.validationMappings.keySet().stream()
                .filter(t -> t.isAssignableFrom(throwable))
                .collect(toSet());
        final ThrowableRelativesLookup lookup = ThrowableRelativesLookup.fromThrowable(throwable);
        final Class<?> closestRelative = lookup.closestRelativeFrom(assignableClasses);

        return ofNullable(this.validationMappings.get(closestRelative));
    }

}
