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

import de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexNestedType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexTypeWithArray;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe.urlEncoded;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.*;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.*;
import static de.quantummaid.mapmaid.testsupport.instances.Instances.*;

public final class UnmarshallerSpecs {

    @Test
    public void testJsonUnmarshallingIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"1\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(JSON).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testJsonUnmarshallingWithCollectionsIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"array\": [\n" +
                "    \"1\",\n" +
                "    \"2\"\n" +
                "  ]\n" +
                "}").from(JSON).toTheType(AComplexTypeWithArray.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDtoWithCollections());
    }

    @Test
    public void testXmlUnmarshallingIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingXmlMarshaller(xmlMarshaller(), xmlUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "<HashMap>\n" +
                "  <number1>1</number1>\n" +
                "  <number2>5</number2>\n" +
                "  <stringA>asdf</stringA>\n" +
                "  <stringB>qwer</stringB>\n" +
                "</HashMap>\n").from(XML).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testYamlUnmarshallingIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "number1: '1'\n" +
                "number2: '5'\n" +
                "stringA: asdf\n" +
                "stringB: qwer\n").from(YAML).toTheType(AComplexType.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testUrlEncodedUnmarshallingIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("number1=1&number2=5&stringA=asdf&stringB=qwer")
                .from(urlEncoded()).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(theFullyInitializedExampleDto());
    }

    @Test
    public void testUrlEncodedUnmarshallingWithCollectionsIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .build()
        )
                .when().mapMaidDeserializes("array[0]=1&array[1]=2")
                .from(urlEncoded()).toTheType(AComplexTypeWithArray.class)
                .theDeserializedObjectIs(theFullyInitializedExampleDtoWithCollections());
    }

    @Test
    public void testUrlEncodedUnmarshallingWithMapsIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "complexType2[number1]=3&" +
                "complexType2[number2]=4&" +
                "complexType2[stringA]=c&" +
                "complexType2[stringB]=d&" +
                "complexType1[number1]=1&" +
                "complexType1[number2]=2&" +
                "complexType1[stringA]=a&" +
                "complexType1[stringB]=b").from(urlEncoded()).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(theFullyInitializedNestedExampleDto());
    }

    @Test
    public void testUnknownUnmarshallerThrowsAnException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingXmlMarshaller(xmlMarshaller(), xmlUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"1\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(marshallingType("unknown")).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining(
                        "Unsupported marshalling type 'unknown'," +
                                " known marshalling types are: ['xml']");
    }
}
