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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.testsupport.domain.pojos.AComplexTypeWithGetters;
import de.quantummaid.mapmaid.testsupport.domain.pojos.AComplexTypeWithSetters;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;

public final class PojoSpecs {

    @Test
    public void gettersCanBeDetectedAsFields() {
        Given.given(
                MapMaid.aMapMaid()
                        .mapping(AComplexTypeWithGetters.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexTypeWithGetters.deserialize(
                        AString.fromStringValue("foo"),
                        AString.fromStringValue("bar")
                )).withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"stringA\": \"foo\",\n" +
                        "  \"stringB\": \"bar\"\n" +
                        "}");
    }

    @Test
    public void settersCanBeDetectedAsFields() {
        Given.given(
                MapMaid.aMapMaid()
                        .mapping(AComplexTypeWithSetters.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"stringA\": \"foo\",\n" +
                "  \"stringB\": \"bar\"\n" +
                "}").from(json()).toTheType(AComplexTypeWithSetters.class)
                .noExceptionHasBeenThrown();
    }
}
