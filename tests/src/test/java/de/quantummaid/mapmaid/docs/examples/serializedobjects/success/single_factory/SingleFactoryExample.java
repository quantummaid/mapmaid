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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.single_factory;

import de.quantummaid.mapmaid.docs.examples.customprimitives.success.factory_by_classname.example1.CountryName;
import de.quantummaid.mapmaid.docs.examples.customprimitives.success.normal.example2.TownName;
import de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder;
import org.junit.jupiter.api.Test;

public final class SingleFactoryExample {

    @Test
    public void singleFactoryExample() {
        ScenarioBuilder.scenarioBuilderFor(AddALotRequest.class)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"townName\": \"b\",\n" +
                        "  \"countryName\": \"d\"\n" +
                        "}")
                .withDeserializedForm(AddALotRequest.fooo(
                        TownName.townName("b"),
                        CountryName.fromStringValue("d")
                ))
                .withAllScenariosSuccessful()
                .run();
    }
}
