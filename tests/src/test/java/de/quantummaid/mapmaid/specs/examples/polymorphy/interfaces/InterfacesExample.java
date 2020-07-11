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

package de.quantummaid.mapmaid.specs.examples.polymorphy.interfaces;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class InterfacesExample {

    @Test
    public void interfacesExample() {
        scenarioBuilderFor(MyInterface.class)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"type\": \"de.quantummaid.mapmaid.specs.examples.polymorphy.interfaces.MyImplementation1\",\n" +
                        "  \"field2\": {\n" +
                        "    \"field1\": \"d\",\n" +
                        "    \"field3\": \"f\",\n" +
                        "    \"type\": \"de.quantummaid.mapmaid.specs.examples.polymorphy.interfaces.MyImplementation3\",\n" +
                        "    \"field2\": \"e\"\n" +
                        "  },\n" +
                        "  \"field1\": {\n" +
                        "    \"field\": {\n" +
                        "      \"field1\": \"a\",\n" +
                        "      \"field3\": \"c\",\n" +
                        "      \"type\": \"de.quantummaid.mapmaid.specs.examples.polymorphy.interfaces.MyImplementation3\",\n" +
                        "      \"field2\": \"b\"\n" +
                        "    },\n" +
                        "    \"type\": \"de.quantummaid.mapmaid.specs.examples.polymorphy.interfaces.MyImplementation2\"\n" +
                        "  }\n" +
                        "}")
                .withDeserializedForm(
                        new MyImplementation1(
                                new MyImplementation2(
                                        new MyImplementation3("a", "b", "c")
                                ),
                                new MyImplementation3("d", "e", "f")
                        )
                )
                .withManualDeserialization(mapMaidBuilder ->
                        mapMaidBuilder.deserializingSubtypes(
                                MyInterface.class,
                                MyImplementation1.class,
                                MyImplementation2.class,
                                MyImplementation3.class
                        )
                )
                .withManualSerialization(mapMaidBuilder ->
                        mapMaidBuilder.serializingSubtypes(
                                MyInterface.class,
                                MyImplementation1.class,
                                MyImplementation2.class,
                                MyImplementation3.class
                        )
                )
                .withManualDuplex(mapMaidBuilder ->
                        mapMaidBuilder.serializingAndDeserializingSubtypes(
                                MyInterface.class,
                                MyImplementation1.class,
                                MyImplementation2.class,
                                MyImplementation3.class
                        )
                )
                .run();
    }
}
