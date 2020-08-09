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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequiredCapabilities {
    private final boolean serialization;
    private final boolean deserialization;
    private final String description;

    public static RequiredCapabilities duplex() {
        return new RequiredCapabilities(true, true, "duplex");
    }

    public static RequiredCapabilities serialization() {
        return new RequiredCapabilities(true, false, "serialization");
    }

    public static RequiredCapabilities deserialization() {
        return new RequiredCapabilities(false, true, "deserialization");
    }

    public boolean hasDeserialization() {
        return this.deserialization;
    }

    public boolean hasSerialization() {
        return this.serialization;
    }

    public String describe() {
        return description;
    }
}
