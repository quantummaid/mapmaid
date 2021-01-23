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

package de.quantummaid.mapmaid.snakeyaml;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnakeYamlSpecs {
    @Test
    public void canMarshalAndUnmarshalNull() {
        marshalAndUnmarshalTest("null\n", null);
    }

    @Test
    public void canMarshalAndUnmarshalIntegerValue() {
        marshalAndUnmarshalTest("1\n", 1);
    }

    @Test
    public void canMarshalAndUnmarshalDecimalValue() {
        marshalAndUnmarshalTest("1.1\n", 1.1d);
    }

    @Test
    public void canUnmarshalScientificNotation() {
        marshalAndUnmarshalTest("1.3E-6\n", 1.3e-6);
    }

    @Test
    public void canMarshalAndUnmarshalStringValue() {
        marshalAndUnmarshalTest("s\n", "s");
    }

    @Test
    public void canMarshalAndUnmarshalTrueBooleanValue() {
        marshalAndUnmarshalTest("true\n", true);
    }

    @Test
    public void canMarshalAndUnmarshalFalseBooleanValue() {
        marshalAndUnmarshalTest("false\n", false);
    }

    @Test
    public void canMarshalAndUnmarshalEmptyListValue() {
        marshalAndUnmarshalTest("" +
                        "[\n" +
                        "  ]\n",
                emptyList());
    }

    @Test
    public void canMarshalAndUnmarshalListValue() {
        marshalAndUnmarshalTest("" +
                        "- null\n" +
                        "- 2\n" +
                        "- 2.2\n" +
                        "- true\n" +
                        "- false\n" +
                        "- s\n",
                asList(null, 2, 2.2d, true, false, "s"));
    }

    @Test
    public void canMarshalAndUnmarshalEmptyJsonObjectValue() {
        marshalAndUnmarshalTest("" +
                        "{\n" +
                        "  }\n",
                emptyMap());
    }

    @Test
    public void canMarshalAndUnmarshalJsonObjectValue() {
        marshalAndUnmarshalTest("" +
                        "k:\n" +
                        "- null\n" +
                        "- 2\n" +
                        "- 2.2\n" +
                        "- true\n" +
                        "- false\n" +
                        "- s\n",
                Map.of("k", asList(null, 2, 2.2d, true, false, "s")));
    }

    @Test
    public void canMarshalAndUnmarshalNullInAnObject() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("nullable", null);
        marshalAndUnmarshalTest("nullable: null\n", map);
    }

    private void marshalAndUnmarshalTest(final String yaml, final Object object) {
        final SnakeYamlMarshallerAndUnmarshaller marshallerAndUnmarshaller = SnakeYamlMarshallerAndUnmarshaller.snakeYamlMarshallerAndUnmarshaller();
        try {
            final Unmarshaller<String> unmarshaller = marshallerAndUnmarshaller.unmarshaller();
            assertEquals(object, unmarshaller.unmarshal(yaml), "unmarshalling from string to object");
            final Marshaller<String> marshaller = marshallerAndUnmarshaller.marshaller();
            assertEquals(yaml, marshaller.marshal(object), "marshalling from object to string");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
