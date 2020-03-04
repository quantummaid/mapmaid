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

package de.quantummaid.mapmaid.docs.examples.customprimitives.success.whole;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static de.quantummaid.mapmaid.docs.examples.system.WrongMethodCalledException.wrongMethodCalledException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Foo {
    private final String value;

    public static Date a() {
        throw wrongMethodCalledException();
    }

    public static Foo fromString(final String value) {
        return new Foo(value);
    }

    public static Foo b(final Date date) {
        throw wrongMethodCalledException();
    }

    public static Date c(final InputStream inputStream) {
        throw wrongMethodCalledException();
    }

    public String internalValueForMapping() {
        return this.value;
    }

    public Date d() {
        throw wrongMethodCalledException();
    }

    public boolean e(final OutputStream o) {
        throw wrongMethodCalledException();
    }

    private static Throwable f() {
        throw wrongMethodCalledException();
    }
}
