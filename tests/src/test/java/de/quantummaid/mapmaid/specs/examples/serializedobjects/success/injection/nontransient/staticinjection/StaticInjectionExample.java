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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.injection.nontransient.staticinjection;

import de.quantummaid.mapmaid.specs.examples.serializedobjects.success.injection.nontransient.DtoWithNonTransientInjection;
import de.quantummaid.mapmaid.specs.examples.serializedobjects.success.injection.nontransient.InjectionObject;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class StaticInjectionExample {

    @Test
    public void normalInjectionExample() {
        scenarioBuilderFor(DtoWithNonTransientInjection.class)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"normalField2\": \"b\",\n" +
                        "  \"normalField1\": \"a\"\n" +
                        "}")
                .withDeserializedForm(DtoWithNonTransientInjection.dtoWithNonTransientInjection("a", "b", InjectionObject.injectionObject("foo")))
                .withSerializationSuccessful("" +
                        "{\n" +
                        "  \"normalField2\": \"b\",\n" +
                        "  \"normalField1\": \"a\",\n" +
                        "  \"injectedField\": \"foo\"\n" +
                        "}")
                .withFixedSerialization(mapMaidBuilder -> {
                    mapMaidBuilder
                            .serializing(DtoWithNonTransientInjection.class)
                            .injecting(InjectionObject.class, () -> InjectionObject.injectionObject("foo"));
                })
                .withDeserializationSuccessful(DtoWithNonTransientInjection.dtoWithNonTransientInjection("a", "b", null))
                .withFixedDeserialization(mapMaidBuilder -> {
                    mapMaidBuilder
                            .deserializing(DtoWithNonTransientInjection.class)
                            .injecting(InjectionObject.class, () -> InjectionObject.injectionObject("foo"));
                })
                .withDuplexSuccessful("" +
                        "{\n" +
                        "  \"normalField2\": \"b\",\n" +
                        "  \"normalField1\": \"a\",\n" +
                        "  \"injectedField\": \"foo\"\n" +
                        "}", DtoWithNonTransientInjection.dtoWithNonTransientInjection("a", "b", null))
                .withFixedDuplex(mapMaidBuilder -> {
                    mapMaidBuilder
                            .serializingAndDeserializing(DtoWithNonTransientInjection.class)
                            .injecting(InjectionObject.class, () -> InjectionObject.injectionObject("foo"));
                })
                .run();
    }
}
