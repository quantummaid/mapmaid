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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.injection.normalinjection;

import de.quantummaid.mapmaid.docs.examples.serializedobjects.success.injection.DtoWithInjections;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;

import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class NormalInjectionExample {

    @Test
    public void normalInjectionExample() {
        scenarioBuilderFor(DtoWithInjections.class)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"normalField2\": \"b\",\n" +
                        "  \"normalField1\": \"a\"\n" +
                        "}")
                .withDeserializedForm(DtoWithInjections.dtoWithInjections("a", "b", System.out))
                .withInjection(injector -> injector.put(System.out))
                .withSerializationSuccessful()
                .withDeserializationFailing("java.io.OutputStream: unable to detect deserializer:")
                .withDuplexFailing()
                .withFixedDeserialization(mapMaidBuilder -> {
                    mapMaidBuilder.deserializing(DtoWithInjections.class)
                            .injecting(OutputStream.class)
                            .build();
                })
                .withFixedDuplex(mapMaidBuilder -> {
                    mapMaidBuilder.serializingAndDeserializing(DtoWithInjections.class)
                            .injecting(OutputStream.class)
                            .build();
                })
                .run();
    }
}
