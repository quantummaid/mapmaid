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

import de.quantummaid.mapmaid.testsupport.domain.exceptions.AnException;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

public final class DeserializerSpecs {

    @Test
    public void givenStringJson_whenDeserializing_thenReturnAStringObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("\"string with special symbols like \' \"").from(JSON).toTheType(AString.class)
                .theDeserializedObjectIs(AString.fromStringValue("string with special symbols like ' "));
    }

    @Test
    public void givenNumberJson_whenDeserializing_thenReturnANumberObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("49").from(JSON).toTheType(ANumber.class)
                .theDeserializedObjectIs(ANumber.fromInt(49));
    }

    @Test
    public void givenComplexTypeJson_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}")
                .from(JSON).toTheType(AComplexType.class)
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(2)
                ));
    }

    @Test
    public void givenComplexTypeWithArray_whenDeserializing_thenReturnObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"array\":[\"1\", \"2\", \"3\"]}")
                .from(JSON).toTheType(AComplexTypeWithArray.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexTypeWithArray.deserialize(
                        new ANumber[]{ANumber.fromInt(1), ANumber.fromInt(2), ANumber.fromInt(3)})
                );
    }

    @Test
    public void givenComplexTypeWithInvalidArray_whenDeserializing_thenThrowCorrectException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .withExceptionIndicatingValidationError(AnException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"array\":[\"1\", \"51\", \"53\"]}")
                .from(JSON).toTheType(AComplexTypeWithArray.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void givenComplexNestedTypeJson_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{" +
                "\"complexType1\":" +
                "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                "\"complexType2\":" +
                "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                "}")
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("a"),
                                AString.fromStringValue("b"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("c"),
                                AString.fromStringValue("d"),
                                ANumber.fromInt(3),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenNull_whenDeserializing_thenThrowsError() {
        given(
                aMapMaid().build()
        )
                .when().mapMaidDeserializes(null).from(JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("input must not be null");
    }

    @Test
    public void givenEmpty_whenDeserializing_thenReturnsNull() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("").from(JSON).toTheType(AComplexType.class)
                .theDeserializedObjectIs(null)
                .noExceptionHasBeenThrown();
    }

    @Test
    public void givenInvalidJson_whenDeserializing_thenThrowsError() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"number1\";\"1\",\"number2\":\"2\",\"stringA\"=\"a\",\"stringB\":\"b\"}")
                .from(JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Error during unmarshalling for type 'de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType' with input '{" +
                        "\"number1\";\"1\"," +
                        "\"number2\":\"2\"," +
                        "\"stringA\"=\"a\"," +
                        "\"stringB\":\"b\"}'");
    }

    @Test
    public void givenIncompleteJson_whenDeserializing_thenFillsWithNull() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"number1\":\"1\",\"stringA\":\"a\"}")
                .from(JSON).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        null,
                        ANumber.fromInt(1),
                        null
                ));
    }

    @Test
    public void givenJsonWithValidValues_whenDeserializing_thenReturnsObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithValidations.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"number1\":\"21\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}")
                .from(JSON).toTheType(AComplexTypeWithValidations.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexTypeWithValidations.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(21),
                        ANumber.fromInt(2)
                ));
    }

    @Test
    public void givenJsonWithNestedValidationExceptions_whenDeserializing_thenReturnsOnlyOneValidationException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedValidatedType.class)
                        .withExceptionIndicatingValidationError(AnException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"node\": {\"leaf\":\"1234\"}}")
                .from(JSON).toTheType(AComplexNestedValidatedType.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(1);
    }

    @Test
    public void deserializerCanFindFactoryMethodsWithArrays() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithListButArrayConstructor.class)
                        .build()
        )
                .when().mapMaidDeserializes("{list: [\"1\"]}").from(JSON).toTheType(AComplexTypeWithListButArrayConstructor.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexTypeWithListButArrayConstructor.deserialize(new ANumber[]{ANumber.fromInt(1)}));
    }

    @Test
    public void nestedCollectionsCanBeDeserialized() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithNestedCollections.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"nestedList\": [\n" +
                "    [\n" +
                "      [\n" +
                "        [\n" +
                "          \"42\"\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ],\n" +
                "  \"nestedArray\": [\n" +
                "    [\n" +
                "      [\n" +
                "        [\n" +
                "          \"arrays\"\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ],\n" +
                "  \"nestedMix2\": [\n" +
                "    [\n" +
                "      [\n" +
                "        [\n" +
                "          \"43\"\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ],\n" +
                "  \"nestedMix1\": [\n" +
                "    [\n" +
                "      [\n" +
                "        [\n" +
                "          \"mixed\"\n" +
                "        ]\n" +
                "      ]\n" +
                "    ]\n" +
                "  ]\n" +
                "}").from(JSON).toTheType(AComplexTypeWithNestedCollections.class)
                .noExceptionHasBeenThrown()
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedArray[0][0][0][0].equals(AString.fromStringValue("arrays")))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedList.get(0).get(0).get(0).get(0).equals(ANumber.fromInt(42)))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedMix1[0].get(0)[0].get(0).equals(AString.fromStringValue("mixed")))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedMix2.get(0)[0].get(0)[0].equals(ANumber.fromInt(43)));
    }
}
