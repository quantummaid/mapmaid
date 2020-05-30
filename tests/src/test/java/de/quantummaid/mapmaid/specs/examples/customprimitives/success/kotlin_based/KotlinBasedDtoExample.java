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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.kotlin_based;

import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class KotlinBasedDtoExample {

    @Test
    public void kotlinBasedDtoExample() {
        scenarioBuilderFor(KotlinDto.class)
                .withSerializedForm("" +
                        "{" +
                        "\"field1\":\"foo\"," +
                        "\"field3\":42," +
                        "\"field2\":\"test\"," +
                        "\"field4\":\"bar\"" +
                        "}")
                .withDeserializedForm(new KotlinDto(
                        new KotlinCustomPrimitive("foo"),
                        "test",
                        42,
                        new KotlinCustomPrimitive("bar")
                ))
                .withAllScenariosSuccessful()
                .withManualDeserialization(mapMaidBuilder ->
                        mapMaidBuilder.deserializing(DeserializationOnlyType.serializedObject(KotlinDto.class)
                                .withField("field1", KotlinCustomPrimitive.class)
                                .withField("field2", String.class)
                                .withField("field3", int.class)
                                .withField("field4", KotlinCustomPrimitive.class)
                                .deserializedUsing(KotlinDto::new)))
                .withManualSerialization(mapMaidBuilder ->
                        mapMaidBuilder.serializing(SerializationOnlyType.serializedObject(KotlinDto.class)
                                .withField("field1", KotlinCustomPrimitive.class, KotlinDto::getField1)
                                .withField("field2", String.class, KotlinDto::getField2)
                                .withField("field3", int.class, KotlinDto::getField3)
                                .withField("field4", KotlinCustomPrimitive.class, KotlinDto::getField4)))
                .withManualDuplex(mapMaidBuilder ->
                        mapMaidBuilder.serializingAndDeserializing(
                                DuplexType.serializedObject(KotlinDto.class)
                                        .withField("field1", KotlinCustomPrimitive.class, KotlinDto::getField1)
                                        .withField("field2", String.class, KotlinDto::getField2)
                                        .withField("field3", int.class, KotlinDto::getField3)
                                        .withField("field4", KotlinCustomPrimitive.class, KotlinDto::getField4)
                                        .deserializedUsing(KotlinDto::new)))
                .run();
    }
}
