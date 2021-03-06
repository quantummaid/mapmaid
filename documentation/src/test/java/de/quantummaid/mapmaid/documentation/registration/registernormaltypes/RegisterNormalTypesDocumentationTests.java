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

package de.quantummaid.mapmaid.documentation.registration.registernormaltypes;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class RegisterNormalTypesDocumentationTests {

    @Test
    public void registeringDuplex() {
        //Showcase start duplexConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(MyCustomClass.class)
                .build();
        //Showcase end duplexConfig
        final String json = mapMaid.serializeToJson(MyCustomClass.myCustomClass("foo"));
        assertThat(json, is("\"foo\""));
        final MyCustomClass deserialized = mapMaid.deserializeJson("\"foo\"", MyCustomClass.class);
        assertThat(deserialized, is(MyCustomClass.myCustomClass("foo")));
    }

    @Test
    public void registeringSerialization() {
        //Showcase start serializationConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(MyCustomClass.class)
                .build();
        //Showcase end serializationConfig
        final String json = mapMaid.serializeToJson(MyCustomClass.myCustomClass("foo"));
        assertThat(json, is("\"foo\""));
    }

    @Test
    public void registeringDeserialization() {
        //Showcase start deserializationConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .deserializing(MyCustomClass.class)
                .build();
        //Showcase end deserializationConfig
        final MyCustomClass deserialized = mapMaid.deserializeJson("\"foo\"", MyCustomClass.class);
        assertThat(deserialized, is(MyCustomClass.myCustomClass("foo")));
    }
}
