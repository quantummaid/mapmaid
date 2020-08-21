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

package de.quantummaid.mapmaid.dynamodb;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.dynamodb.util.Asserters.assertRoundTrip;

public final class DynamoDbMarshallerSpecs {

    @Test
    public void nullCanBeMarshalled() {
        assertRoundTrip(null);
    }

    @Test
    public void stringCanBeMarshalled() {
        assertRoundTrip("abc");
        assertRoundTrip("");
    }

    @Test
    public void intCanBeMarshalled() {
        assertRoundTrip(1, 1L);
        assertRoundTrip(0, 0L);
    }

    @Test
    public void longCanBeMarshalled() {
        assertRoundTrip(1L);
        assertRoundTrip(0L);
    }

    @Test
    public void booleanCanBeMarshalled() {
        assertRoundTrip(true);
        assertRoundTrip(false);
    }

    @Test
    public void mapCanBeMarshalled() {
        assertRoundTrip(Map.of("a", "b", "c", 1L));
        assertRoundTrip(Map.of());
    }

    @Test
    public void listCanBeMarshalled() {
        assertRoundTrip(List.of("a", 1L, true, Map.of()));
        assertRoundTrip(List.of());
    }
}
