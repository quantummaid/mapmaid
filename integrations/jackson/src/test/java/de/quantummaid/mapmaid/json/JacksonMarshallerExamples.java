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

package de.quantummaid.mapmaid.json;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.jackson.JacksonMarshallers.*;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class JacksonMarshallerExamples {

    private static final String JSON = "" +
            "{" +
            "   \"list\": [" +
            "       \"a\", \"b\", \"c\"" +
            "   ]," +
            "   \"string\":\"foo\"," +
            "   \"object\":" +
            "       {" +
            "           \"string\": \"asdf\"" +
            "       }" +
            "}";

    private static final String XML = "" +
            "<MapN>\n" +
            "  <object>\n" +
            "    <string>asdf</string>\n" +
            "  </object>\n" +
            "  <list>a</list>\n" +
            "  <list>b</list>\n" +
            "  <list>c</list>\n" +
            "  <string>foo</string>\n" +
            "</MapN>";

    private static final String YAML = "" +
            "---\n" +
            "list:\n" +
            "- \"a\"\n" +
            "- \"b\"\n" +
            "- \"c\"\n" +
            "string: \"foo\"\n" +
            "object:\n" +
            "  string: \"asdf\"";

    private static final Map<String, Object> MAP = Map.of(
            "string", "foo",
            "list", List.of("a", "b", "c"),
            "object", Map.of("string", "asdf"));

    @Test
    public void jsonMarshallerExample() {
        //Showcase start json
        final MapMaid mapMaid = aMapMaid()
                .usingRecipe(jacksonMarshallerJson())
                .build();
        //Showcase end json

        final Map<String, Object> deserialized = mapMaid.deserializer().deserializeToMap(JSON, json());
        assertThat(deserialized, is(MAP));
    }

    @Test
    public void xmlMarshallerExample() {
        //Showcase start xml
        final MapMaid mapMaid = aMapMaid()
                .usingRecipe(jacksonMarshallerXml())
                .build();
        //Showcase end xml

        final Map<String, Object> deserialized = mapMaid.deserializer().deserializeToMap(XML, xml());
        assert MAP.equals(deserialized);
    }

    @Test
    public void yamlMarshallerExample() {
        //Showcase start yaml
        final MapMaid mapMaid = aMapMaid()
                .usingRecipe(jacksonMarshallerYaml())
                .build();
        //Showcase end yaml

        final Map<String, Object> deserialized = mapMaid.deserializer().deserializeToMap(YAML, yaml());
        assert MAP.equals(deserialized);
    }
}
