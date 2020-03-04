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

import de.quantummaid.mapmaid.builder.recipes.marshallers.urlencoded.UrlEncodedMarshallerRecipe;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexNestedType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexTypeWithArray;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.testsupport.instances.Instances.*;

public final class UnmarshallerSpecs {

    @Test
    public void testJsonUnmarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"1\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(json()).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testJsonUnmarshallingWithCollectionsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"array\": [\n" +
                "    \"1\",\n" +
                "    \"2\"\n" +
                "  ]\n" +
                "}").from(json()).toTheType(AComplexTypeWithArray.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDtoWithCollections());
    }

    @Test
    public void testXmlUnmarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "<HashMap>\n" +
                "  <number1>1</number1>\n" +
                "  <number2>5</number2>\n" +
                "  <stringA>asdf</stringA>\n" +
                "  <stringB>qwer</stringB>\n" +
                "</HashMap>\n").from(xml()).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testYamlUnmarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "number1: '1'\n" +
                "number2: '5'\n" +
                "stringA: asdf\n" +
                "stringB: qwer\n").from(yaml()).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testUrlEncodedUnmarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("number1=1&number2=5&stringA=asdf&stringB=qwer")
                .from(UrlEncodedMarshallerRecipe.urlEncoded()).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testUrlEncodedUnmarshallingWithCollectionsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("array[0]=1&array[1]=2")
                .from(UrlEncodedMarshallerRecipe.urlEncoded()).toTheType(AComplexTypeWithArray.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDtoWithCollections());
    }

    @Test
    public void testUrlEncodedUnmarshallingWithMapsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "complexType2[number1]=3&" +
                "complexType2[number2]=4&" +
                "complexType2[stringA]=c&" +
                "complexType2[stringB]=d&" +
                "complexType1[number1]=1&" +
                "complexType1[number2]=2&" +
                "complexType1[stringA]=a&" +
                "complexType1[stringB]=b").from(UrlEncodedMarshallerRecipe.urlEncoded()).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(theFullyInitializedNestedExampleDto());
    }

    @Test
    public void testUnknownUnmarshallerThrowsAnException() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"1\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(marshallingType("unknown")).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining(
                        "Unsupported marshalling type 'unknown'," +
                                " known marshalling types are: ['urlencoded', 'json', 'xml', 'yaml']");
    }
}
