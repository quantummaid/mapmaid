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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.mapper.definitions.Definition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequiredCapabilities {
    private boolean serialization;
    private boolean deserialization;

    public static RequiredCapabilities none() {
        return new RequiredCapabilities(false, false);
    }

    public static RequiredCapabilities duplex() {
        return new RequiredCapabilities(true, true);
    }

    public static RequiredCapabilities serialization() {
        return new RequiredCapabilities(true, false);
    }

    public static RequiredCapabilities deserialization() {
        return new RequiredCapabilities(false, true);
    }

    public static RequiredCapabilities fromDefinition(final Definition definition) {
        final boolean serialization = definition.serializer().isPresent();
        final boolean deserialization = definition.deserializer().isPresent();
        return new RequiredCapabilities(serialization, deserialization);
    }

    public void add(final RequiredCapabilities other) {
        this.serialization = this.serialization || other.serialization;
        this.deserialization = this.deserialization || other.deserialization;
    }

    public boolean contains(final RequiredCapabilities other) {
        if (other.serialization && !this.serialization) {
            return false;
        }
        if (other.deserialization && !this.deserialization) {
            return false;
        }
        return true;
    }

    public boolean hasDeserialization() {
        return this.deserialization;
    }

    public boolean hasSerialization() {
        return this.serialization;
    }
}
