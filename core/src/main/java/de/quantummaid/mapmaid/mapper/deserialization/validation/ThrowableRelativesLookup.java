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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class ThrowableRelativesLookup {

    private final Class<? extends Throwable> clazz;
    private final List<Class<? extends Throwable>> relatives;

    private ThrowableRelativesLookup(final Class<? extends Throwable> clazz) {
        this.clazz = clazz;
        this.relatives = new ArrayList<>(0);
        this.traverseRelatives(clazz);
    }

    static ThrowableRelativesLookup fromThrowable(final Class<? extends Throwable> clazz) {
        return new ThrowableRelativesLookup(clazz);
    }

    Class<? extends Throwable> closestRelativeFrom(final Collection<Class<? extends Throwable>> assignableClasses) {
        for (final Class<? extends Throwable> relative : this.relatives) {
            if (assignableClasses.stream()
                    .anyMatch(relative::equals)) {
                return relative;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void traverseRelatives(final Class<?> clazz) {
        if (Throwable.class.isAssignableFrom(clazz)) {
            this.relatives.add((Class<? extends Throwable>) clazz);
            final Class<?> superclass = clazz.getSuperclass();
            final Class<?>[] interfaces = clazz.getInterfaces();

            Arrays.stream(interfaces).forEach(this::traverseRelatives);

            if (superclass != null) {
                traverseRelatives(superclass);
            }
        }
    }
}
