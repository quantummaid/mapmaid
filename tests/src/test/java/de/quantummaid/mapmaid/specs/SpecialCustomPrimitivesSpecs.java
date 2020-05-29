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

import de.quantummaid.mapmaid.shared.mapping.BooleanFormatException;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class SpecialCustomPrimitivesSpecs {

    @Test
    public void doubleBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": 1, \"doubleB\": 2}").from(JSON).toTheType(AComplexTypeWithDoublesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)));
    }

    @Test
    public void doubleBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": \"1\", \"doubleB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithDoublesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)));
    }

    @Test
    public void doubleBasedCustomPrimitivesCanNotBeDeserializedWithWrongStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .withExceptionIndicatingValidationError(NumberFormatException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"doubleA\": \"foo\", \"doubleB\": \"bar\"}").from(JSON).toTheType(AComplexTypeWithDoublesDto.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void doubleBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithDoublesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithDoublesDto(new APrimitiveDouble(1.0), new AWrapperDouble(2.0)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"doubleB\": 2.0,\n" +
                        "  \"doubleA\": 1.0" +
                        "\n}");
    }

    @Test
    public void floatBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithFloatsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"floatA\": 1, \"floatB\": 2}").from(JSON).toTheType(AComplexTypeWithFloatsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithFloatsDto(new APrimitiveFloat(1.0F), new AWrapperFloat(2.0F)));
    }

    @Test
    public void floatBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithFloatsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"floatA\": \"1\", \"floatB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithFloatsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithFloatsDto(new APrimitiveFloat(1.0F), new AWrapperFloat(2.0F)));
    }

    @Test
    public void floatBasedCustomPrimitivesCanNotBeDeserializedWithWrongStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithFloatsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .withExceptionIndicatingValidationError(NumberFormatException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"floatA\": \"foo\", \"floatB\": \"bar\"}").from(JSON).toTheType(AComplexTypeWithFloatsDto.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void floatBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithFloatsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithFloatsDto(new APrimitiveFloat(1.0F), new AWrapperFloat(2.0F)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"floatA\": 1.0,\n" +
                        "  \"floatB\": 2.0" +
                        "\n}");
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": true, \"booleanB\": false}").from(JSON).toTheType(AComplexTypeWithBooleansDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)));
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": \"true\", \"booleanB\": \"false\"}").from(JSON).toTheType(AComplexTypeWithBooleansDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)));
    }

    @Test
    public void booleanBasedCustomPrimitivesCanNotBeDeserializedWithWrongStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .withExceptionIndicatingValidationError(BooleanFormatException.class)
                        .build()
        )
                .when().mapMaidDeserializes("{\"booleanA\": \"foo\", \"booleanB\": \"bar\"}").from(JSON).toTheType(AComplexTypeWithBooleansDto.class)
                .anAggregatedExceptionHasBeenThrownWithNumberOfErrors(2);
    }

    @Test
    public void booleanBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBooleansDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithBooleansDto(new APrimitiveBoolean(true), new AWrapperBoolean(false)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"booleanB\": false,\n" +
                        "  \"booleanA\": true\n" +
                        "}");
    }

    @Test
    public void longBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithLongsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"longA\": 1, \"longB\": 2}").from(JSON).toTheType(AComplexTypeWithLongsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithLongsDto(new APrimitiveLong(1), new AWrapperLong(2L)));
    }

    @Test
    public void longBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithLongsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"longA\": \"1\", \"longB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithLongsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithLongsDto(new APrimitiveLong(1), new AWrapperLong(2L)));
    }

    @Test
    public void longBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithLongsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithLongsDto(new APrimitiveLong(1), new AWrapperLong(2L)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"longB\": 2,\n" +
                        "  \"longA\": 1\n" +
                        "}");
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"intA\": 1, \"intB\": 2}").from(JSON).toTheType(AComplexTypeWithIntegersDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)));
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"intA\": \"1\", \"intB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithIntegersDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)));
    }

    @Test
    public void integerBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithIntegersDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithIntegersDto(new APrimitiveInteger(1), new AWrapperInteger(2)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"intB\": 2,\n" +
                        "  \"intA\": 1\n" +
                        "}");
    }

    @Test
    public void shortBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithShortsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"shortA\": 1, \"shortB\": 2}").from(JSON).toTheType(AComplexTypeWithShortsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithShortsDto(new APrimitiveShort((short) 1), new AWrapperShort((short) 2)));
    }

    @Test
    public void shortBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithShortsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"shortA\": \"1\", \"shortB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithShortsDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithShortsDto(new APrimitiveShort((short) 1), new AWrapperShort((short) 2)));
    }

    @Test
    public void shortBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithShortsDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithShortsDto(new APrimitiveShort((short) 1), new AWrapperShort((short) 2)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"shortB\": 2,\n" +
                        "  \"shortA\": 1\n" +
                        "}");
    }

    @Test
    public void byteBasedCustomPrimitivesCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBytesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"byteA\": 1, \"byteB\": 2}").from(JSON).toTheType(AComplexTypeWithBytesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBytesDto(new APrimitiveByte((byte) 1), new AWrapperByte((byte) 2)));
    }

    @Test
    public void byteBasedCustomPrimitivesCanBeDeserializedWithStrings() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBytesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"byteA\": \"1\", \"byteB\": \"2\"}").from(JSON).toTheType(AComplexTypeWithBytesDto.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new AComplexTypeWithBytesDto(new APrimitiveByte((byte) 1), new AWrapperByte((byte) 2)));
    }

    @Test
    public void byteBasedCustomPrimitivesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithBytesDto.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(new AComplexTypeWithBytesDto(new APrimitiveByte((byte) 1), new AWrapperByte((byte) 2)))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"byteA\": 1,\n" +
                        "  \"byteB\": 2\n" +
                        "}");
    }

    @Test
    public void tooLargeDoubleValueCastedToFloatThrowsOverflowException() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(APrimitiveFloat.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"" + largestFloatPlusOne() + "\"").from(JSON).toTheType(APrimitiveFloat.class)
                .anExceptionIsThrownWithAUnderlyingCause("Overflow when converting double '340282346638528900000000000000000000000.000000' to float.");
    }

    @Test
    public void tooLargeLongValueCastedToIntegerThrowsOverflowException() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(APrimitiveInteger.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"" + largestIntegerPlusOne() + "\"").from(JSON).toTheType(APrimitiveInteger.class)
                .anExceptionIsThrownWithAUnderlyingCause("Overflow when converting long '2147483648' to int.");
    }

    @Test
    public void tooLargeIntegerValueCastedToShortThrowsOverflowException() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(APrimitiveShort.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"" + largestShortPlusOne() + "\"").from(JSON).toTheType(APrimitiveShort.class)
                .anExceptionIsThrownWithAUnderlyingCause("Overflow when converting long '32768' to short.");
    }

    @Test
    public void tooLargeShortValueCastedToByteThrowsOverflowException() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(APrimitiveByte.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"" + largestBytePlusOne() + "\"").from(JSON).toTheType(APrimitiveByte.class)
                .anExceptionIsThrownWithAUnderlyingCause("Overflow when converting long '128' to byte.");
    }

    private double largestFloatPlusOne() {
        final double maxValue = (double) Float.MAX_VALUE;
        final double oneAbove = Math.nextUp(maxValue);
        return oneAbove;
    }

    private long largestIntegerPlusOne() {
        final long maxValue = (long) Integer.MAX_VALUE;
        final long oneAbove = maxValue + 1L;
        return oneAbove;
    }

    private long largestShortPlusOne() {
        final int maxValue = (int) Short.MAX_VALUE;
        final int oneAbove = maxValue + 1;
        return oneAbove;
    }

    private long largestBytePlusOne() {
        final short maxValue = Byte.MAX_VALUE;
        final short oneAbove = maxValue + (byte) 1;
        return oneAbove;
    }
}
