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

import de.quantummaid.mapmaid.domain.AComplexNestedType;
import de.quantummaid.mapmaid.domain.AComplexType;
import de.quantummaid.mapmaid.domain.AComplexTypeWithArray;
import de.quantummaid.mapmaid.domain.Instances;
import de.quantummaid.mapmaid.mapper.deserialization.UnexpectedExceptionThrownDuringUnmarshallingException;
import de.quantummaid.mapmaid.mapper.serialization.UnexpectedExceptionThrownDuringMarshallingException;
import de.quantummaid.mapmaid.specs.exceptions.TestException;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe.urlEncoded;
import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe.urlEncodedMarshaller;
import static de.quantummaid.mapmaid.domain.Instances.theFullyInitializedExampleDto;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.specs.exceptions.ExceptionThrowingMarshaller.exceptionThrowingMarshaller;
import static de.quantummaid.mapmaid.specs.exceptions.ExceptionThrowingMarshaller.exceptionThrowingUnmarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.*;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public final class MarshallerSpecs {

    @Test
    public void testJsonMarshallingIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(JSON)
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
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .build()
        )
                .when().mapMaidSerializes(Instances.theFullyInitializedExampleDtoWithCollections()).withMarshallingType(JSON)
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
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingXmlMarshaller(xmlMarshaller(), xmlUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(XML)
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
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(YAML)
                .theSerializationResultWas("" +
                        "number1: '1'\n" +
                        "number2: '5'\n" +
                        "stringA: asdf\n" +
                        "stringB: qwer\n");
    }

    @Test
    public void testUrlEncodedMarshallingIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(urlEncoded())
                .theSerializationResultWas("number1=1&number2=5&stringA=asdf&stringB=qwer");
    }

    @Test
    public void testUrlEncodedMarshallingWithCollectionsIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexTypeWithArray.class)
                        .build()
        )
                .when().mapMaidSerializes(Instances.theFullyInitializedExampleDtoWithCollections()).withMarshallingType(urlEncoded())
                .theSerializationResultWas("array[0]=1&array[1]=2");
    }

    @Test
    public void testUrlEncodedMarshallingWithMapsIsPossible() {
        given(
                aMapMaid()
                        .usingRecipe(urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
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
    public void urlEncodedCanUnmarshallEmptyString() {
        given(
                aMapMaid()
                        .usingRecipe(urlEncodedMarshaller())
                        .build()
        )
                .when().mapMaidUnmarshalsToUniversalObject("", urlEncoded())
                .theDeserializedObjectIs(null);
    }

    @Test
    public void testUnknownMarshallerThrowsAnException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller())
                                .usingXmlMarshaller(xmlMarshaller(), xmlUnmarshaller())
                                .usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .usingRecipe(urlEncodedMarshaller())
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(theFullyInitializedExampleDto()).withMarshallingType(marshallingType("unknown"))
                .anExceptionIsThrownWithAMessageContaining("Unsupported marshalling type 'unknown', known marshalling types are:")
                .anExceptionIsThrownWithAMessageContaining("'urlencoded'")
                .anExceptionIsThrownWithAMessageContaining("'json'")
                .anExceptionIsThrownWithAMessageContaining("'yaml'")
                .anExceptionIsThrownWithAMessageContaining("'xml'");
    }

    @Test
    public void testMarshallerThrowingAnExceptionDuringSerialization() {
        final Object input = theFullyInitializedExampleDto();
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(exceptionThrowingMarshaller(), jsonUnmarshaller())
                        )
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(input).withMarshallingType(JSON)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown during marshalling: TestException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringMarshallingException.class, e -> {
                    assertThat(e.exception(), instanceOf(TestException.class));
                    assertThat(e.objectToBeMarshalled(), equalTo(input));
                });
    }

    @Test
    public void testMarshallerThrowingAnExceptionDuringMarshalling() {
        final Object input = theFullyInitializedExampleDto();
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(exceptionThrowingMarshaller(), jsonUnmarshaller())
                        )
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidMarshalsFromUniversalObject(input, JSON)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown during marshalling: TestException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringMarshallingException.class, e -> {
                    assertThat(e.exception(), instanceOf(TestException.class));
                    assertThat(e.objectToBeMarshalled(), equalTo(input));
                });
    }

    @Test
    public void testUnmarshallerThrowingAnExceptionDuringDeserialization() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(jsonMarshaller(), exceptionThrowingUnmarshaller())
                        )
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("{}").from(JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown during unmarshalling: TestException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringUnmarshallingException.class, e -> {
                    assertThat(e.exception(), instanceOf(TestException.class));
                    assertThat(e.objectToUnmarshall(), equalTo("{}"));
                });
    }

    @Test
    public void testUnmarshallerThrowingAnExceptionDuringUnmarshalling() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(jsonMarshaller(), exceptionThrowingUnmarshaller())
                        )
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidUnmarshalsToUniversalObject("{}", JSON)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown during unmarshalling: TestException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringUnmarshallingException.class, e -> {
                    assertThat(e.exception(), instanceOf(TestException.class));
                    assertThat(e.objectToUnmarshall(), equalTo("{}"));
                });
    }
}
