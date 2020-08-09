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

import de.quantummaid.mapmaid.testsupport.domain.half.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

public final class HalfDefinitionsSpecs {

    @Test
    public void aCustomPrimitiveCanBeSerializationOnly() {
        given(
                aMapMaid()
                        .withType(ASerializationOnlyString.class, serialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyString.init()).withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("\"theValue\"");
    }

    @Test
    public void aCustomPrimitiveCanBeDeserializationOnly() {
        given(
                aMapMaid()
                        .withType(ADeserializationOnlyString.class, deserialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"foo\"").from(JSON).toTheType(ADeserializationOnlyString.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyString.fromStringValue("foo"));
    }

    @Test
    public void aSerializedObjectCanBeSerializationOnly() {
        given(
                aMapMaid()
                        .withType(ASerializationOnlyComplexType.class, serialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyComplexType.init()).withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"string\": \"theValue\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectCanBeDeserializationOnly() {
        given(
                aMapMaid()
                        .withType(ADeserializationOnlyComplexType.class, deserialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"string\": \"foo\"\n" +
                "}")
                .from(JSON).toTheType(ADeserializationOnlyComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyComplexType.deserialize(ADeserializationOnlyString.fromStringValue("foo")));
    }

    @Test
    public void mapMaidCanValidateThatSerializationWorks() {
        given(() -> aMapMaid()
                .serializing(AnUnresolvableSerializationOnlyComplexType.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyString: unable to detect serialization-only");
    }

    @Test
    public void mapMaidCanValidateThatDeserializationWorks() {
        given(() -> aMapMaid()
                .withType(AnUnresolvableDeserializationOnlyComplexType.class, deserialization())
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("de.quantummaid.mapmaid.testsupport.domain.half.ASerializationOnlyString: unable to detect deserialization-only");
    }
}
