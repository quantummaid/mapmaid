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

package de.quantummaid.mapmaid.minimaljson;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshaller.minimalJsonMarshaller;
import static de.quantummaid.mapmaid.minimaljson.MinimalJsonUnmarshaller.minimalJsonUnmarshaller;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinimalJsonSpecs {
    @Test
    public void canMarshalAndUnmarshalNull() {
        marshalAndUnmarshalTest("null", null);
    }

    @Test
    public void canMarshalAndUnmarshalIntegerValue() {
        marshalAndUnmarshalTest("1", 1l);
    }

    @Test
    public void canMarshalAndUnmarshalDecimalValue() {
        marshalAndUnmarshalTest("1.1", 1.1d);
    }

    @Test
    public void canUnmarshalScientificNotation() {
        marshalAndUnmarshalTest("1.3E-6", 1.3e-6);
    }

    @Test
    public void canMarshalAndUnmarshalStringValue() {
        marshalAndUnmarshalTest("\"s\"", "s");
    }

    @Test
    public void canMarshalAndUnmarshalTrueBooleanValue() {
        marshalAndUnmarshalTest("true", true);
    }

    @Test
    public void canMarshalAndUnmarshalFalseBooleanValue() {
        marshalAndUnmarshalTest("false", false);
    }

    @Test
    public void canMarshalAndUnmarshalEmptyListValue() {
        marshalAndUnmarshalTest("[]", emptyList());
    }

    @Test
    public void canMarshalAndUnmarshalListValue() {
        marshalAndUnmarshalTest("[null,2,2.2,true,false,\"s\"]", asList(null, 2l, 2.2d, true, false, "s"));
    }

    @Test
    public void canMarshalAndUnmarshalEmptyJsonObjectValue() {
        marshalAndUnmarshalTest("{}", emptyMap());
    }

    @Test
    public void canMarshalAndUnmarshalJsonObjectValue() {
        marshalAndUnmarshalTest("{\"k\":[null,2,2.2,true,false,\"s\"]}", Map.of("k", asList(null, 2l, 2.2d, true, false, "s")));
    }

    private void marshalAndUnmarshalTest(final String json, final Object object) {
        try {
            final MinimalJsonUnmarshaller unmarshaller = minimalJsonUnmarshaller();
            assertEquals(object, unmarshaller.unmarshal(json), "unmarshalling from string to object");
            final MinimalJsonMarshaller marshaller = minimalJsonMarshaller();
            assertEquals(json, marshaller.marshal(object), "marshalling from object to string");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
