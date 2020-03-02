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

package de.quantummaid.mapmaid.examples.domain;

import java.util.Objects;

public final class ValidPerson {
    public final FullName fullName;
    public final Address address;

    private ValidPerson(final FullName fullName, final Address address) {
        this.fullName = fullName;
        this.address = address;
    }

    public static ValidPerson deserialize(final FullName fullName, final Address address) {
        if (Objects.isNull(fullName)) {
            throw new IllegalArgumentException("fullName must not be null");
        }
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("address must not be null");
        }
        return new ValidPerson(fullName, address);
    }

    @Override
    public String toString() {
        return "Person{" +
                "fullName=" + this.fullName +
                '}';
    }
}
