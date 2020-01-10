/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.symetric.set_to_array;

import de.quantummaid.mapmaid.docs.examples.customprimitives.success.normal.example1.Name;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class SetToArrayExample {

    @Test
    public void setToArrayExample() {
        scenarioBuilderFor(ARequest.class)
                .withSerializedForm("{\n" +
                        "  \"names\": [\n" +
                        "    \"foo\"\n" +
                        "  ]\n" +
                        "}")
                .withDeserializedForm(ARequest.aRequest(new HashSet<>(List.of(Name.fromStringValue("foo")))))
                .withAllScenariosSuccessful()
                .run();
    }
}
