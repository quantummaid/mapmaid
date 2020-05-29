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

package de.quantummaid.mapmaid.json;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.AdvancedBuilder;
import de.quantummaid.mapmaid.json.domain.AComplexType;
import de.quantummaid.mapmaid.json.domain.AFloatingNumber;
import de.quantummaid.mapmaid.json.domain.ANumber;
import de.quantummaid.mapmaid.json.domain.AString;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.jackson.JacksonMarshallers.jacksonMarshallerJson;
import static de.quantummaid.mapmaid.json.JsonMatcher.isJson;
import static org.hamcrest.MatcherAssert.assertThat;

public final class JacksonSpecs {

    @Test
    public void jacksonWithRecipe() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(AComplexType.class)
                .usingRecipe(jacksonMarshallerJson())
                .withAdvancedSettings(AdvancedBuilder::doNotAutoloadMarshallers)
                .build();
        final String json = mapMaid.serializeToJson(AComplexType.deserialize(
                AString.fromStringValue("a"), AString.fromStringValue("b"),
                ANumber.fromInt(1), ANumber.fromInt(2),
                AFloatingNumber.fromDouble(3.3), AFloatingNumber.fromDouble(4.4)
        ));
        assertThat(json, isJson("{\"number3\":\"3.3\",\"number4\":\"4.4\",\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}"));
    }
}
