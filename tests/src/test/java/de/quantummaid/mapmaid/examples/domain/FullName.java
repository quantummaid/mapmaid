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

package de.quantummaid.mapmaid.examples.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public final class FullName {
    public final FirstName firstName;
    public final LastName lastName;
    public final LastNamePrefix lastNamePrefix;
    public final MiddleName[] middleNames;

    private FullName(
            final FirstName firstName,
            final LastName lastName,
            final LastNamePrefix lastNamePrefix,
            final MiddleName[] middleNames) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.middleNames = middleNames;
    }

    public static FullName deserialize(
            final FirstName firstName,
            final LastName lastName,
            final LastNamePrefix lastNamePrefix,
            final MiddleName[] middleNames) {
        if (Objects.isNull(firstName)) {
            throw new IllegalArgumentException("firstName must not be null");
        }
        if (Objects.isNull(lastName)) {
            throw new IllegalArgumentException("lastName must not be null");
        }

        MiddleName[] names = middleNames;
        LastNamePrefix prefix = lastNamePrefix;
        if (names == null) {
            names = new MiddleName[]{};
        }
        if (prefix == null) {
            prefix = LastNamePrefix.fromStringValue("");
        }

        return new FullName(firstName, lastName, prefix, names);
    }

    public String textual() {
        return String.format("%s %s %s %s",
                this.firstName.stringValue(),
                Arrays.stream(this.middleNames).map(MiddleName::stringValue).collect(Collectors.joining(" ")),
                this.lastNamePrefix.stringValue(),
                this.lastName.stringValue());
    }
}
