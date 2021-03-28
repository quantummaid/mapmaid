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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.boolean_based;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.customprimitives.success.boolean_based.LoggedIn.fromStringValue;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class BooleanBasedExample {

    @Test
    public void booleanBasedExample() {
        scenarioBuilderFor(LoggedIn.class)
                .withSerializedForm("true")
                .withDeserializedForm(fromStringValue(true))
                .withAllScenariosSuccessful()
                .withManualDeserialization(mapMaidBuilder ->
                        mapMaidBuilder.deserializingBooleanBasedCustomPrimitive(
                                LoggedIn.class, LoggedIn::fromStringValue))
                .withManualSerialization(mapMaidBuilder ->
                        mapMaidBuilder.serializingBooleanBasedCustomPrimitive(
                                LoggedIn.class, LoggedIn::stringValue
                        ))
                .withManualDuplex(mapMaidBuilder ->
                        mapMaidBuilder.serializingAndDeserializingBooleanBasedCustomPrimitive(
                                LoggedIn.class, LoggedIn::stringValue, LoggedIn::fromStringValue
                        ))
                .run();
    }
}
