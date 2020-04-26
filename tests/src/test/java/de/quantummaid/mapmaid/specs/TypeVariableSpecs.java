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

import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexParameterizedType;
import de.quantummaid.mapmaid.domain.AComplexTypeWithParameterizedUnusedMethods;
import de.quantummaid.mapmaid.domain.ANumber;
import de.quantummaid.mapmaid.domain.AString;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class TypeVariableSpecs {

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, AString.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(AString.fromStringValue("foo")), genericType(AComplexParameterizedType.class, AString.class))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"foo\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeDeserialized() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, AString.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"value\": \"foo\"\n" +
                "}").from(MarshallingType.JSON).toTheType(genericType(AComplexParameterizedType.class, AString.class))
                .noExceptionHasBeenThrown();
    }

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeRegisteredTwice() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, AString.class))
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, ANumber.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(ANumber.fromInt(42)), genericType(AComplexParameterizedType.class, ANumber.class))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"42\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableFieldCanBeSerializedIfTheTypeOfTheTypeVariableIsProvidedDuringSerialization() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, genericType(List.class, AString.class)))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(List.of(AString.fromStringValue("foo"))), genericType(AComplexParameterizedType.class, genericType(List.class, AString.class)))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": [\n" +
                        "    \"foo\"\n" +
                        "  ]\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanSerializedIfTheValueOfTheTypeVariableIsAnArray() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(genericType(AComplexParameterizedType.class, AString[].class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(new AString[]{AString.fromStringValue("foo")}), genericType(AComplexParameterizedType.class, AString[].class))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": [\n" +
                        "    \"foo\"\n" +
                        "  ]\n" +
                        "}");
    }

    @Test
    public void methodTypeVariablesDoNotCauseProblems() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexTypeWithParameterizedUnusedMethods.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexTypeWithParameterizedUnusedMethods.deserialize(
                        AString.fromStringValue("foo"),
                        AString.fromStringValue("bar"),
                        ANumber.fromInt(42),
                        ANumber.fromInt(21)))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"42\",\n" +
                        "  \"number2\": \"21\",\n" +
                        "  \"stringA\": \"foo\",\n" +
                        "  \"stringB\": \"bar\"\n" +
                        "}");
    }
}
