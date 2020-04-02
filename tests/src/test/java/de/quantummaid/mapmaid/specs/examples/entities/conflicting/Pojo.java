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

package de.quantummaid.mapmaid.specs.examples.entities.conflicting;

import de.quantummaid.mapmaid.builder.models.constructor.Name;

public final class Pojo {
    private int someNumber;
    private Integer someOtherNumber;
    private String someString;
    private Name someName;

    private Pojo() {
    }

    public int getSomeNumber() {
        return this.someNumber;
    }

    public void setSomeNumber(final int someNumber) {
        this.someNumber = someNumber;
    }

    public Integer getSomeOtherNumber() {
        return this.someOtherNumber;
    }

    public void setSomeOtherNumber(final Integer someOtherNumber) {
        this.someOtherNumber = someOtherNumber;
    }

    public String getSomeString() {
        return this.someString;
    }

    public void setSomeString(final String someString) {
        this.someString = someString;
    }

    public Name getSomeName() {
        return this.someName;
    }

    public void setSomeName(final Name someName) {
        this.someName = someName;
    }
}
