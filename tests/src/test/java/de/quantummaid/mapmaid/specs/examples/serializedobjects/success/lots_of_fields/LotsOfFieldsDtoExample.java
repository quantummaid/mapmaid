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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.lots_of_fields;

import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class LotsOfFieldsDtoExample {

    @Test
    public void lotsOfFieldsExample() {
        scenarioBuilderFor(LotsOfFieldsDto.class)
                .withSerializedForm("{\n" +
                        "  \"field11\": \"11\",\n" +
                        "  \"field12\": \"12\",\n" +
                        "  \"field1\": \"1\",\n" +
                        "  \"field10\": \"10\",\n" +
                        "  \"field15\": \"15\",\n" +
                        "  \"field16\": \"16\",\n" +
                        "  \"field13\": \"13\",\n" +
                        "  \"field14\": \"14\",\n" +
                        "  \"field7\": \"7\",\n" +
                        "  \"field6\": \"6\",\n" +
                        "  \"field9\": \"9\",\n" +
                        "  \"field8\": \"8\",\n" +
                        "  \"field3\": \"3\",\n" +
                        "  \"field2\": \"2\",\n" +
                        "  \"field5\": \"5\",\n" +
                        "  \"field4\": \"4\"\n" +
                        "}\n")
                .withDeserializedForm(LotsOfFieldsDto.lotsOfFieldsDto(
                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
                ))
                .withFixedScenarios((mapMaidBuilder, requiredCapabilities) -> {
                    if (requiredCapabilities.hasDeserialization() && requiredCapabilities.hasSerialization()) {
                        mapMaidBuilder.serializingAndDeserializing(DuplexType.serializedObject(LotsOfFieldsDto.class)
                                .withField("field1", String.class, object -> object.field1)
                                .withField("field2", String.class, object -> object.field2)
                                .withField("field3", String.class, object -> object.field3)
                                .withField("field4", String.class, object -> object.field4)
                                .withField("field5", String.class, object -> object.field5)
                                .withField("field6", String.class, object -> object.field6)
                                .withField("field7", String.class, object -> object.field7)
                                .withField("field8", String.class, object -> object.field8)
                                .withField("field9", String.class, object -> object.field9)
                                .withField("field10", String.class, object -> object.field10)
                                .withField("field11", String.class, object -> object.field11)
                                .withField("field12", String.class, object -> object.field12)
                                .withField("field13", String.class, object -> object.field13)
                                .withField("field14", String.class, object -> object.field14)
                                .withField("field15", String.class, object -> object.field15)
                                .withField("field16", String.class, object -> object.field16)
                                .deserializedUsing(LotsOfFieldsDto::lotsOfFieldsDto)
                        );
                    } else if (requiredCapabilities.hasSerialization()) {
                        mapMaidBuilder.serializing(SerializationOnlyType.serializedObject(LotsOfFieldsDto.class)
                                .withField("field1", String.class, object -> object.field1)
                                .withField("field2", String.class, object -> object.field2)
                                .withField("field3", String.class, object -> object.field3)
                                .withField("field4", String.class, object -> object.field4)
                                .withField("field5", String.class, object -> object.field5)
                                .withField("field6", String.class, object -> object.field6)
                                .withField("field7", String.class, object -> object.field7)
                                .withField("field8", String.class, object -> object.field8)
                                .withField("field9", String.class, object -> object.field9)
                                .withField("field10", String.class, object -> object.field10)
                                .withField("field11", String.class, object -> object.field11)
                                .withField("field12", String.class, object -> object.field12)
                                .withField("field13", String.class, object -> object.field13)
                                .withField("field14", String.class, object -> object.field14)
                                .withField("field15", String.class, object -> object.field15)
                                .withField("field16", String.class, object -> object.field16)
                        );
                    } else if (requiredCapabilities.hasDeserialization()) {
                        mapMaidBuilder.deserializing(DeserializationOnlyType.serializedObject(LotsOfFieldsDto.class)
                                .withField("field1", String.class)
                                .withField("field2", String.class)
                                .withField("field3", String.class)
                                .withField("field4", String.class)
                                .withField("field5", String.class)
                                .withField("field6", String.class)
                                .withField("field7", String.class)
                                .withField("field8", String.class)
                                .withField("field9", String.class)
                                .withField("field10", String.class)
                                .withField("field11", String.class)
                                .withField("field12", String.class)
                                .withField("field13", String.class)
                                .withField("field14", String.class)
                                .withField("field15", String.class)
                                .withField("field16", String.class)
                                .deserializedUsing(LotsOfFieldsDto::lotsOfFieldsDto)
                        );
                    }
                })
                .run();
    }
}
