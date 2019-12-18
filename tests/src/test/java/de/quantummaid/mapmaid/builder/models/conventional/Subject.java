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

package de.quantummaid.mapmaid.builder.models.conventional;

import de.quantummaid.mapmaid.builder.validation.LengthValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Subject {
    /*
     * These static fields are to verify that map mate is ignoring such things, do not remove these constants.
     */
    public static final Subject HELLO_WORLD = fromStringValue("Hello World");
    public static final Subject HELLO_UNIVERSE = fromStringValue("Hello Universe");
    public static final Subject HELLO_MULTIVERSE = fromStringValue("Hello Multiverse");

    private final String value;

    public static Subject fromStringValue(final String value) {
        final String validated = LengthValidator.ensureLength(value, 1, 256, "subject");
        return new Subject(validated);
    }

    public String stringValue() {
        return this.value;
    }
}
