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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.conflicting.multiple_factories;

import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example1.Name;
import de.quantummaid.mapmaid.specs.examples.customprimitives.success.normal.example2.TownName;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class MultipleFactoriesExample {

    @Test
    public void multipleFactoriesExample() {
        scenarioBuilderFor(AddALotRequest.class)
                .withDeserializedForm(AddALotRequest.factory1(
                        Name.fromStringValue("foo"),
                        TownName.townName("townNameA"),
                        TownName.townName("townNameB"),
                        TownName.townName("townNameC")
                ))
                .withSerializedForm("" +
                        "{\n" +
                        "  \"name\": \"foo\",\n" +
                        "  \"townNameA\": \"townNameA\",\n" +
                        "  \"townNameB\": \"townNameB\",\n" +
                        "  \"townNameC\": \"townNameC\"\n" +
                        "}")
                .withDuplexFailing("de.quantummaid.mapmaid.specs.examples.serializedobjects.conflicting.multiple_factories.AddALotRequest: unable to detect")
                .withDeserializationFailing("de.quantummaid.mapmaid.specs.examples.serializedobjects.conflicting.multiple_factories.AddALotRequest: unable to detect")
                .withSerializationSuccessful()
                .withManual(
                        (mapMaidBuilder, capabilities) -> {
                            if (capabilities.hasDeserialization() && capabilities.hasSerialization()) {
                                mapMaidBuilder.serializingAndDeserializingCustomObject(AddALotRequest.class, builder -> builder
                                        .withField("name", Name.class, object -> object.name)
                                        .withField("townNameA", TownName.class, object -> object.townNameA)
                                        .withField("townNameB", TownName.class, object -> object.townNameB)
                                        .withField("townNameC", TownName.class, object -> object.townNameC)
                                        .deserializedUsing(AddALotRequest::factory1)
                                );
                            } else if (capabilities.hasSerialization()) {
                                mapMaidBuilder.serializingCustomObject(AddALotRequest.class, builder -> builder
                                        .withField("name", Name.class, object -> object.name)
                                        .withField("townNameA", TownName.class, object -> object.townNameA)
                                        .withField("townNameB", TownName.class, object -> object.townNameB)
                                        .withField("townNameC", TownName.class, object -> object.townNameC)
                                );
                            } else if (capabilities.hasDeserialization()) {
                                mapMaidBuilder.deserializingCustomObject(AddALotRequest.class, builder -> builder
                                        .withField("name", Name.class)
                                        .withField("townNameA", TownName.class)
                                        .withField("townNameB", TownName.class)
                                        .withField("townNameC", TownName.class)
                                        .deserializedUsing(AddALotRequest::factory1)
                                );
                            }
                        })
                .run();
    }
}
