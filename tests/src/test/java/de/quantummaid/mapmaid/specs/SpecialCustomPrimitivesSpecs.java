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
import de.quantummaid.mapmaid.shared.mapping.BooleanFormatException;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;

public final class SpecialCustomPrimitivesSpecs {

    @Test
    public void doubleBasedCustomPrimitivesCanBeDeserialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": 1, \"doubleB\": 2}").from(json()).toTheType(AComplexTypeWithDoublesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)));
    }

    @Test
    public void doubleBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": \"1\", \"doubleB\": \"2\"}").from(json()).toTheType(AComplexTypeWithDoublesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)));
    }

    @Test
    public void doubleBasedCustomPrimitivesCanNotBeDeserializedWithWrongStrings() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .withExceptionIndicatingValidationError(NumberFormatException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": \"foo\", \"doubleB\": \"bar\"}").from(json()).toTheType(AComplexTypeWithDoublesDto.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void doubleBasedCustomPrimitivesCanBeSerialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"doubleB\": 2.0,\n" +
                        "  \"doubleA\": 1.0" +
                        "\n}");
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeDeserialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": true, \"booleanB\": false}").from(json()).toTheType(AComplexTypeWithBooleansDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)));
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": \"true\", \"booleanB\": \"false\"}").from(json()).toTheType(AComplexTypeWithBooleansDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)));
    }

    @Test
    public void booleanBasedCustomPrimitivesCanNotBeDeserializedWithWrongStrings() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .withExceptionIndicatingValidationError(BooleanFormatException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": \"foo\", \"booleanB\": \"bar\"}").from(json()).toTheType(AComplexTypeWithBooleansDto.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeSerialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"booleanB\": false,\n" +
                        "  \"booleanA\": true\n" +
                        "}");
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeDeserialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"intA\": 1, \"intB\": 2}").from(json()).toTheType(AComplexTypeWithIntegersDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)));
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"intA\": \"1\", \"intB\": \"2\"}").from(json()).toTheType(AComplexTypeWithIntegersDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)));
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeSerialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedTypes(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"intB\": 2.0,\n" +
                        "  \"intA\": 1.0\n" +
                        "}");
    }
}
