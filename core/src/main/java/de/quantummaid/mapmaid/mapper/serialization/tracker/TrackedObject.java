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

package de.quantummaid.mapmaid.mapper.serialization.tracker;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrackedObject {
    private final Object object;

    public static TrackedObject trackedObject(final Object object) {
        NotNullValidator.validateNotNull(object, "object");
        return new TrackedObject(object);
    }

    private boolean isSameReference(final Object other) {
        NotNullValidator.validateNotNull(other, "other");
        return this.object == other;
    }

    @Override
    public String toString() {
        return format(
                "A tracked object of type '%s' that is not displayed because it might contain circular references",
                this.object.getClass().getName());
    }

    @Override
    public boolean equals(final Object other) {
        NotNullValidator.validateNotNull(other, "other");
        if (!(other instanceof TrackedObject)) {
            throw new UnsupportedOperationException();
        }
        return isSameReference(((TrackedObject) other).object);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
