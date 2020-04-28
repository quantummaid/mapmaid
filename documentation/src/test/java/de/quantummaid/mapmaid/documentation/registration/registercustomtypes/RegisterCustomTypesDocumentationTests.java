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

package de.quantummaid.mapmaid.documentation.registration.registercustomtypes;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class RegisterCustomTypesDocumentationTests {

    @Test
    public void registeringDuplexCustomCustomPrimitive() {
        //Showcase start duplexCustomCustomPrimitiveConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(
                        DuplexType.customPrimitive(
                                MyCustomPrimitive.class,
                                MyCustomPrimitive::value,
                                MyCustomPrimitive::new
                        )
                )
                .build();
        //Showcase end duplexCustomCustomPrimitiveConfig
        final String json = mapMaid.serializeToJson(new MyCustomPrimitive("foo"));
        assertThat(json, is("\"foo\""));
        final MyCustomPrimitive deserialized = mapMaid.deserializeJson("\"foo\"", MyCustomPrimitive.class);
        assertThat(deserialized, is(new MyCustomPrimitive("foo")));
    }

    @Test
    public void registeringDuplexCustomSerializedObject() {
        //Showcase start duplexCustomSerializedObjectConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(
                        DuplexType.serializedObject(MySerializedObject.class)
                                .withField("field1", String.class, MySerializedObject::getField1)
                                .withField("field2", String.class, MySerializedObject::getField2)
                                .withField("field3", String.class, MySerializedObject::getField3)
                                .deserializedUsing(MySerializedObject::new)
                )
                .build();
        //Showcase end duplexCustomSerializedObjectConfig

        final MySerializedObject instance = new MySerializedObject("a", "b", "c");
        final String json = mapMaid.serializeToJson(instance);

        final String expectedJson = "{\"field3\":\"c\",\"field2\":\"b\",\"field1\":\"a\"}";
        assertThat(json, is(expectedJson));

        final MySerializedObject deserialized = mapMaid.deserializeJson(expectedJson, MySerializedObject.class);
        assertThat(deserialized, is(instance));
    }

    @Test
    public void registeringSerializationCustomCustomPrimitive() {
        //Showcase start serializationCustomCustomPrimitiveConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(
                        SerializationOnlyType.customPrimitive(
                                MyCustomPrimitive.class,
                                MyCustomPrimitive::value
                        )
                )
                .build();
        //Showcase end serializationCustomCustomPrimitiveConfig
        final String json = mapMaid.serializeToJson(new MyCustomPrimitive("foo"));
        assertThat(json, is("\"foo\""));
    }

    @Test
    public void registeringSerializationCustomSerializedObject() {
        //Showcase start serializationCustomSerializedObjectConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(
                        SerializationOnlyType.serializedObject(MySerializedObject.class)
                                .withField("field1", String.class, MySerializedObject::getField1)
                                .withField("field2", String.class, MySerializedObject::getField2)
                                .withField("field3", String.class, MySerializedObject::getField3)
                )
                .build();
        //Showcase end serializationCustomSerializedObjectConfig

        final MySerializedObject instance = new MySerializedObject("a", "b", "c");
        final String json = mapMaid.serializeToJson(instance);
        final String expectedJson = "{\"field3\":\"c\",\"field2\":\"b\",\"field1\":\"a\"}";
        assertThat(json, is(expectedJson));
    }

    @Test
    public void registeringDeserializationCustomCustomPrimitive() {
        //Showcase start deserializationCustomCustomPrimitiveConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .deserializing(
                        DeserializationOnlyType.customPrimitive(
                                MyCustomPrimitive.class,
                                MyCustomPrimitive::new
                        )
                )
                .build();
        //Showcase end deserializationCustomCustomPrimitiveConfig
        final MyCustomPrimitive deserialized = mapMaid.deserializeJson("\"foo\"", MyCustomPrimitive.class);
        assertThat(deserialized, is(new MyCustomPrimitive("foo")));
    }

    @Test
    public void registeringDeserializationCustomSerializedObject() {
        //Showcase start deserializationCustomSerializedObjectConfig
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .deserializing(
                        DeserializationOnlyType.serializedObject(MySerializedObject.class)
                                .withField("field1", String.class)
                                .withField("field2", String.class)
                                .withField("field3", String.class)
                                .deserializedUsing(MySerializedObject::new)
                )
                .build();
        //Showcase end deserializationCustomSerializedObjectConfig

        final MySerializedObject instance = new MySerializedObject("a", "b", "c");
        final String expectedJson = "{\"field3\":\"c\",\"field2\":\"b\",\"field1\":\"a\"}";
        final MySerializedObject deserialized = mapMaid.deserializeJson(expectedJson, MySerializedObject.class);
        assertThat(deserialized, is(instance));
    }
}
