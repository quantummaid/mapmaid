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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.conflicting.asymetric.getter_setter_mismatch;

import de.quantummaid.mapmaid.docs.examples.customprimitives.success.normal.example2.TownName;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class GetterSetterMismatchExample {

    // TODO not conflicting?
    @Test
    public void getterSetterMismatchExample() {
        final AddALotRequest addALotRequest = new AddALotRequest();
        addALotRequest.setTownName(TownName.townName("foo"));
        scenarioBuilderFor(AddALotRequest.class)
                .withDeserializedForm(addALotRequest)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"townName\": \"foo\"\n" +
                        "}")
                .withDuplexSuccessful()
                .withDeserializationOnlySuccessful()
                .run();
    }
}
