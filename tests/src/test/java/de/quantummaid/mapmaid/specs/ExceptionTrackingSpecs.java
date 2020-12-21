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
import de.quantummaid.mapmaid.domain.exceptions.AnException;
import de.quantummaid.mapmaid.mapper.deserialization.validation.UnexpectedExceptionThrownDuringDeserializationException;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationError.fromExceptionMessageAndPropertyPath;
import static de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationError.fromStringMessageAndPropertyPath;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

public final class ExceptionTrackingSpecs {

    @Test
    public void testUnmappedException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"x\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(MarshallingType.JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown when deserializing field " +
                        "'number1': NumberFormatException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringDeserializationException.class, e -> {
                    assertThat(
                            e.rawCompleteInput.toString(),
                            equalTo("{number1=x, number2=5, stringA=asdf, stringB=qwer}")
                    );
                    assertThat(e.unmappedException, instanceOf(NumberFormatException.class));
                    assertThat(e.unmappedException.getMessage(), equalTo("For input string: \"x\""));
                });
    }

    @Test
    public void testUnmappedExceptionDeeperInHierarchy() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder
                                .usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{" +
                "\"complexType1\"={\n" +
                "  \"number1\": \"1\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "},\n" +
                "\"complexType2\"={\n" +
                "  \"number1\": \"x\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}\n" +
                "}"
        ).from(MarshallingType.JSON).toTheType(AComplexNestedType.class)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown when deserializing field " +
                        "'complexType2.number1': NumberFormatException")
                .anExceptionOfClassIsThrownFulfilling(UnexpectedExceptionThrownDuringDeserializationException.class, e -> {
                    assertThat(
                            e.rawCompleteInput.toString(),
                            equalTo("{" +
                                    "complexType2={number1=x, number2=5, stringA=asdf, stringB=qwer}, " +
                                    "complexType1={number1=1, number2=5, stringA=asdf, stringB=qwer}" +
                                    "}")
                    );
                    assertThat(e.unmappedException, instanceOf(NumberFormatException.class));
                    assertThat(e.unmappedException.getMessage(), equalTo("For input string: \"x\""));
                });
    }

    @Test
    public void testMappedException() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .withExceptionIndicatingValidationError(AnException.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"4\",\n" +
                "  \"number2\": \"5000\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(MarshallingType.JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("deserialization encountered validation errors." +
                        " Validation error at 'number2', value cannot be over 50; ");
    }

    @Test
    public void testUnmappedExceptionWithoutMarshalling() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializesTheMap("" +
                "{\n" +
                "  \"number1\": \"x\",\n" +
                "  \"number2\": \"5\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Unexpected exception thrown when deserializing field " +
                        "'number1': NumberFormatException");
    }

    @Test
    public void testMappedExceptionWithoutMarshalling() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .withExceptionIndicatingValidationError(AnException.class)
                        .build()
        )
                .when().mapMaidDeserializesTheMap("" +
                "{\n" +
                "  \"number1\": \"4\",\n" +
                "  \"number2\": \"5000\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("deserialization encountered validation errors." +
                        " Validation error at 'number2', value cannot be over 50; ");
    }

    @Test
    public void testMultipleExceptionsCanBeMapped() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .withExceptionIndicatingValidationError(AnException.class)
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"4000\",\n" +
                "  \"number2\": \"5000\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(MarshallingType.JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("deserialization encountered validation errors." +
                        " Validation error at 'number1', value cannot be over 50;" +
                        " Validation error at 'number2', value cannot be over 50; ");
    }

    @Test
    public void exceptionIndicatingMultipleValidationErrors() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .withExceptionIndicatingMultipleValidationErrors(AnException.class,
                                (exception, propertyPath) -> List.of(
                                        fromExceptionMessageAndPropertyPath(exception, "a"),
                                        fromStringMessageAndPropertyPath("foo", "a.b")
                                ))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"number1\": \"4000\",\n" +
                "  \"number2\": \"5000\",\n" +
                "  \"stringA\": \"asdf\",\n" +
                "  \"stringB\": \"qwer\"\n" +
                "}").from(MarshallingType.JSON).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("deserialization encountered validation errors. " +
                        "Validation error at 'a', value cannot be over 50; " +
                        "Validation error at 'a.b', foo; " +
                        "Validation error at 'a', value cannot be over 50; " +
                        "Validation error at 'a.b', foo; ");
    }
}
