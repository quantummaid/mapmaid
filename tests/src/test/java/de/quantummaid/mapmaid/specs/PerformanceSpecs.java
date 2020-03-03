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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.MapMaidInstances.theExampleMapMaidWithAllMarshallers;

public final class PerformanceSpecs {

    @Disabled
    @Test
    public void aLotOfSerializationsDoNotCauseProblems() {
        final MapMaid mapMaid = theExampleMapMaidWithAllMarshallers();

        for (int i = 0; i < 10_000_000; ++i) {
            Given.given(mapMaid)
                    .when().mapMaidSerializes(
                    AComplexType.deserialize(
                            AString.fromStringValue("asdf"),
                            AString.fromStringValue("qwer"),
                            ANumber.fromInt(1),
                            ANumber.fromInt(5)))
                    .withMarshallingType(json())
                    .theSerializationResultWas("" +
                            "{\n" +
                            "  \"number1\": \"1\",\n" +
                            "  \"number2\": \"5\",\n" +
                            "  \"stringA\": \"asdf\",\n" +
                            "  \"stringB\": \"qwer\"\n" +
                            "}");
        }
    }

    @Disabled
    @Test
    public void aLotOfDeserializationsDoNotCauseProblems() {
        final MapMaid mapMaid = theExampleMapMaidWithAllMarshallers();

        for (int i = 0; i < 10_000_000; ++i) {
            Given.given(mapMaid)
                    .when().mapMaidDeserializes("" +
                    "{\n" +
                    "  \"number1\": \"1\",\n" +
                    "  \"number2\": \"5\",\n" +
                    "  \"stringA\": \"asdf\",\n" +
                    "  \"stringB\": \"qwer\"\n" +
                    "}")
                    .from(json()).toTheType(AComplexType.class)
                    .noExceptionHasBeenThrown()
                    .theDeserializedObjectIs(AComplexType.deserialize(
                            AString.fromStringValue("asdf"),
                            AString.fromStringValue("qwer"),
                            ANumber.fromInt(1),
                            ANumber.fromInt(5)));
        }
    }
}
