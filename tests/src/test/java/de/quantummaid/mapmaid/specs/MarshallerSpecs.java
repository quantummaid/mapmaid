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

import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.instances.Instances;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.recipes.marshallers.urlencoded.UrlEncodedMarshallerRecipe.urlEncoded;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.testsupport.instances.Instances.theFullyInitializedExampleDto;

public final class MarshallerSpecs {

    @Test
    public void testJsonMarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(json())
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"1\",\n" +
                        "  \"number2\": \"5\",\n" +
                        "  \"stringA\": \"asdf\",\n" +
                        "  \"stringB\": \"qwer\"\n" +
                        "}");
    }

    @Test
    public void testJsonMarshallingWithCollectionsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(Instances.theFullyInitializedExampleDtoWithCollections()).withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"array\": [\n" +
                        "    \"1\",\n" +
                        "    \"2\"\n" +
                        "  ]\n" +
                        "}");
    }

    @Test
    public void testXmlMarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(xml())
                .theSerializationResultWas("" +
                        "<HashMap>\n" +
                        "  <number1>1</number1>\n" +
                        "  <number2>5</number2>\n" +
                        "  <stringA>asdf</stringA>\n" +
                        "  <stringB>qwer</stringB>\n" +
                        "</HashMap>\n");
    }

    @Test
    public void testYamlMarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(yaml())
                .theSerializationResultWas("" +
                        "number1: '1'\n" +
                        "number2: '5'\n" +
                        "stringA: asdf\n" +
                        "stringB: qwer\n");
    }

    @Test
    public void testUrlEncodedMarshallingIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(urlEncoded())
                .theSerializationResultWas("number1=1&number2=5&stringA=asdf&stringB=qwer");
    }

    @Test
    public void testUrlEncodedMarshallingWithCollectionsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(Instances.theFullyInitializedExampleDtoWithCollections()).withMarshallingType(urlEncoded())
                .theSerializationResultWas("array[0]=1&array[1]=2");
    }

    @Test
    public void testUrlEncodedMarshallingWithMapsIsPossible() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(Instances.theFullyInitializedNestedExampleDto()).withMarshallingType(urlEncoded())
                .theSerializationResultWas("" +
                        "complexType2[number1]=3&" +
                        "complexType2[number2]=4&" +
                        "complexType2[stringA]=c&" +
                        "complexType2[stringB]=d&" +
                        "complexType1[number1]=1&" +
                        "complexType1[number2]=2&" +
                        "complexType1[stringA]=a&" +
                        "complexType1[stringB]=b");
    }

    @Test
    public void testUnknownMarshallerThrowsAnException() {
        Given.givenTheExampleMapMaidWithAllMarshallers()
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(marshallingType("unknown"))
                .anExceptionIsThrownWithAMessageContaining(
                        "Unsupported marshalling type 'unknown'," +
                                " known marshalling types are: ['urlencoded', 'json', 'yaml', 'xml']");
    }
}
