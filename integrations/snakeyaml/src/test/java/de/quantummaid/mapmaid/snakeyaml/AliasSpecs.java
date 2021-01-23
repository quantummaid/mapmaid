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

import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.snakeyaml.SnakeYamlMarshallerAndUnmarshaller.snakeYamlMarshallerAndUnmarshaller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class AliasSpecs {
    private static final String EVIL_PAYLOAD = "" +
            "a: &a [\"lol\",\"lol\",\"lol\",\"lol\",\"lol\",\"lol\",\"lol\",\"lol\",\"lol\"]\n" +
            "b: &b [*a,*a,*a,*a,*a,*a,*a,*a,*a]\n" +
            "c: &c [*b,*b,*b,*b,*b,*b,*b,*b,*b]\n" +
            "d: &d [*c,*c,*c,*c,*c,*c,*c,*c,*c]\n" +
            "e: &e [*d,*d,*d,*d,*d,*d,*d,*d,*d]\n" +
            "f: &f [*e,*e,*e,*e,*e,*e,*e,*e,*e]\n" +
            "g: &g [*f,*f,*f,*f,*f,*f,*f,*f,*f]\n" +
            "h: &h [*g,*g,*g,*g,*g,*g,*g,*g,*g]\n" +
            "i: &i [*h,*h,*h,*h,*h,*h,*h,*h,*h]";

    /*
    https://en.wikipedia.org/wiki/Billion_laughs_attack#Variations
     */
    @Test
    public void snakeYamlUnmarshallerDoesNotSufferFromBillionLaughsAttack() throws Exception {
        final Unmarshaller<String> unmarshaller = snakeYamlMarshallerAndUnmarshaller().unmarshaller();
        YAMLException yamlException = null;
        try {
            unmarshaller.unmarshal(EVIL_PAYLOAD);
        } catch (final YAMLException e) {
            yamlException = e;
            e.printStackTrace();
        }
        assertNotNull(yamlException);
        assertEquals("Number of aliases for non-scalar nodes exceeds the specified max=50", yamlException.getMessage());
    }

    @Test
    public void snakeYamlUnmarshallerSupportsAliases() throws Exception {
        final Unmarshaller<String> unmarshaller = snakeYamlMarshallerAndUnmarshaller().unmarshaller();
        final Object unmarshalled = unmarshaller.unmarshal("" +
                "a: &a \"foo\"\n" +
                "b: [*a,*a,*a]\n");
        assertEquals(Map.of("a", "foo", "b", List.of("foo", "foo", "foo")), unmarshalled);
    }
}
