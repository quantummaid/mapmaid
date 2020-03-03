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
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.MapMaidInstances.theExampleMapMaidWithAllMarshallers;

public final class DeserializerSpecs {

    @Test
    public void givenStringJson_whenDeserializing_thenReturnAStringObject() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("\"string with special symbols like \' \"").from(json()).toTheType(AString.class)
                .theDeserializedObjectIs(AString.fromStringValue("string with special symbols like ' "));
    }

    @Test
    public void givenNumberJson_whenDeserializing_thenReturnANumberObject() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("49").from(json()).toTheType(ANumber.class)
                .theDeserializedObjectIs(ANumber.fromInt(49));
    }

    @Test
    public void givenComplexTypeJson_whenDeserializing_thenReturnAComplexObject() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}")
                .from(json()).toTheType(AComplexType.class)
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(2)
                ));
    }

    @Test
    public void givenComplexTypeWithArray_whenDeserializing_thenReturnObject() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"array\":[\"1\", \"2\", \"3\"]}")
                .from(json()).toTheType(AComplexTypeWithArray.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexTypeWithArray.deserialize(
                        new ANumber[]{ANumber.fromInt(1), ANumber.fromInt(2), ANumber.fromInt(3)})
                );
    }

    @Test
    public void givenComplexTypeWithInvalidArray_whenDeserializing_thenThrowCorrectException() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"array\":[\"1\", \"51\", \"53\"]}")
                .from(json()).toTheType(AComplexTypeWithArray.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void givenComplexNestedTypeJson_whenDeserializing_thenReturnAComplexObject() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "{" +
                "\"complexType1\":" +
                "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                "\"complexType2\":" +
                "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                "}")
                .from(json()).toTheType(AComplexNestedType.class)
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
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes(null).from(json()).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("input must not be null");
    }

    @Test
    public void givenEmpty_whenDeserializing_thenReturnsNull() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("").from(json()).toTheType(AComplexType.class)
                .theDeserializedObjectIs(null)
                .noExceptionHasBeenThrown();
    }

    @Test
    public void givenInvalidJson_whenDeserializing_thenThrowsError() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"number1\";\"1\",\"number2\":\"2\",\"stringA\"=\"a\",\"stringB\":\"b\"}")
                .from(json()).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Error during unmarshalling for type 'de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType' with input '{" +
                        "\"number1\";\"1\"," +
                        "\"number2\":\"2\"," +
                        "\"stringA\"=\"a\"," +
                        "\"stringB\":\"b\"}'");
    }

    @Test
    public void givenIncompleteJson_whenDeserializing_thenFillsWithNull() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"number1\":\"1\",\"stringA\":\"a\"}")
                .from(json()).toTheType(AComplexType.class)
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
        final MapMaid mapMaid = theExampleMapMaidWithAllMarshallers();
        final ScanInformation scanInformation = mapMaid.debugInformation().scanInformationFor(AComplexTypeWithValidations.class);
        System.out.println(scanInformation.render());
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"number1\":\"21\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}")
                .from(json()).toTheType(AComplexTypeWithValidations.class)
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
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{\"node\": {\"leaf\":\"1234\"}}")
                .from(json()).toTheType(AComplexNestedValidatedType.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(1);
    }

    @Test
    public void deserializerCanFindFactoryMethodsWithArrays() {
        final MapMaid mapMaid = theExampleMapMaidWithAllMarshallers();
        final String render = mapMaid.debugInformation().scanInformationFor(AComplexTypeWithListButArrayConstructor.class).render();
        System.out.println(render);

        final String render1 = mapMaid.debugInformation().scanInformationFor(ANumber[].class).render();
        System.out.println(render1);

        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("{list: [\"1\"]}").from(json()).toTheType(AComplexTypeWithListButArrayConstructor.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexTypeWithListButArrayConstructor.deserialize(new ANumber[]{ANumber.fromInt(1)}));
    }

    @Test
    public void nestedCollectionsCanBeDeserialized() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
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
                "}").from(json()).toTheType(AComplexTypeWithNestedCollections.class)
                .noExceptionHasBeenThrown()
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedArray[0][0][0][0].equals(AString.fromStringValue("arrays")))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedList.get(0).get(0).get(0).get(0).equals(ANumber.fromInt(42)))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedMix1[0].get(0)[0].get(0).equals(AString.fromStringValue("mixed")))
                .theDeserialiedObjectHas(AComplexTypeWithNestedCollections.class, result -> result.nestedMix2.get(0)[0].get(0)[0].equals(ANumber.fromInt(43)));
    }
}
