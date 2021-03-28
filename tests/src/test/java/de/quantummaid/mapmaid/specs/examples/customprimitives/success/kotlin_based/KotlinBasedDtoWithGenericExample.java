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

import de.quantummaid.reflectmaid.GenericType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class KotlinBasedDtoWithGenericExample {

    @Test
    public void kotlinBasedDtoExample() {
        final GenericType<?> genericType = genericType(KotlinDtoWithGeneric.class, KotlinCustomPrimitive.class);
        scenarioBuilderFor(genericType)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"field1\": [\n" +
                        "    \"a\",\n" +
                        "    \"b\",\n" +
                        "    \"c\"\n" +
                        "  ],\n" +
                        "  \"field3\": [\n" +
                        "    \"x\"\n" +
                        "  ],\n" +
                        "  \"field2\": [\n" +
                        "    [\n" +
                        "      1,\n" +
                        "      2\n" +
                        "    ],\n" +
                        "    [\n" +
                        "      3,\n" +
                        "      4\n" +
                        "    ]\n" +
                        "  ],\n" +
                        "  \"field4\": [\n" +
                        "    [\n" +
                        "      \"y\"\n" +
                        "    ],\n" +
                        "    [\n" +
                        "      \"z\"\n" +
                        "    ]\n" +
                        "  ]\n" +
                        "}\n")
                .withDeserializedForm(new KotlinDtoWithGeneric<>(
                        List.of("a", "b", "c"),
                        List.of(List.of(1, 2), List.of(3, 4)),
                        List.of(new KotlinCustomPrimitive("x")),
                        List.of(List.of(new KotlinCustomPrimitive("y")), List.of(new KotlinCustomPrimitive("z")))
                ))
                .withAllScenariosSuccessful()
                .run();
    }
}
