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

package de.quantummaid.mapmaid;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ParameterNameTest {

    private static boolean isPresent() {
        try {
            final Method m = ParameterNameTest.class.getMethod("isPresent0", Object.class);
            return isPresent0(null) & m.getParameters()[0].isNamePresent();
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPresent0(final Object param1) {
        return true;
    }

    @Test
    public void ensureThatMethodParameterNamesAreAvailableThroughReflection() {
        assertThat("javac command line option -parameters must be in use", isPresent(), is(true));
    }
}
