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
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;
import static java.util.Collections.singletonList;

public final class SerializerSpecs {

    @Test
    public void testMethodReferenceInCPSerializationMethod() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexType.deserialize(
                        AString.fromStringValue("asdf"),
                        AString.fromStringValue("qwer"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(5555))).withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"1\",\n" +
                        "  \"number2\": \"5555\",\n" +
                        "  \"stringA\": \"asdf\",\n" +
                        "  \"stringB\": \"qwer\"\n" +
                        "}");
    }

    @Test
    public void givenStringDomain_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(AString.fromStringValue("test@test.test")).withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("\"test@test.test\"");
    }

    @Test
    public void givenNumberDomain_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(ANumber.fromInt(123)).withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("\"123\"");
    }

    @Test
    public void givenComplexDomain_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(2)))
                .withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"1\",\n" +
                        "  \"number2\": \"2\",\n" +
                        "  \"stringA\": \"a\",\n" +
                        "  \"stringB\": \"b\"\n" +
                        "}");
    }

    @Test
    public void givenComplexDomainWithCollections_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithCollections.class)
                        .build()
        )
                .when().mapMaidSerializes(AComplexTypeWithCollections.deserialize(
                Arrays.asList(AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        AString.fromStringValue("c")),
                new ANumber[]{
                        ANumber.fromInt(1),
                        ANumber.fromInt(2),
                        ANumber.fromInt(3),
                }))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"array\": [\n" +
                        "    \"1\",\n" +
                        "    \"2\",\n" +
                        "    \"3\"\n" +
                        "  ],\n" +
                        "  \"arrayList\": [\n" +
                        "    \"a\",\n" +
                        "    \"b\",\n" +
                        "    \"c\"\n" +
                        "  ]\n" +
                        "}");
    }

    @Test
    public void givenComplexNestedDomain_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidSerializes(AComplexNestedType.deserialize(
                AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(2)),
                AComplexType.deserialize(
                        AString.fromStringValue("c"),
                        AString.fromStringValue("d"),
                        ANumber.fromInt(3),
                        ANumber.fromInt(4)))).withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"complexType2\": {\n" +
                        "    \"number1\": \"3\",\n" +
                        "    \"number2\": \"4\",\n" +
                        "    \"stringA\": \"c\",\n" +
                        "    \"stringB\": \"d\"\n" +
                        "  },\n" +
                        "  \"complexType1\": {\n" +
                        "    \"number1\": \"1\",\n" +
                        "    \"number2\": \"2\",\n" +
                        "    \"stringA\": \"a\",\n" +
                        "    \"stringB\": \"b\"\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void givenNull_whenSerializing_thenThrowsError() {
        given(
                aMapMaid().build()
        )
                .when().mapMaidSerializes(null).withMarshallingType(MarshallingType.JSON)
                .anExceptionIsThrownWithAMessageContaining("object must not be null");
    }

    @Test
    public void givenNonConfiguredComplexDomain_whenSerializing_thenThrowsError() {
        given(
                aMapMaid().build()
        )
                .when().mapMaidSerializes(new ANonConfiguredDomain()).withMarshallingType(MarshallingType.JSON)
                .anExceptionIsThrownWithAMessageContaining(
                        "no definition found for type 'de.quantummaid.mapmaid.specs.SerializerSpecs$ANonConfiguredDomain'");
    }

    @Test
    public void givenNonCyclicType_whenSerializing_thenDoesNotThrowsError() {
        final ACyclicType given1 = ACyclicType.deserialize(AString.fromStringValue("a"), null);
        final ACyclicType given2 = ACyclicType.deserialize(AString.fromStringValue("b"), null);
        final ACyclicType given3 = ACyclicType.deserialize(AString.fromStringValue("c"), null);

        given1.aCyclicType = given2;
        given2.aCyclicType = given3;

        final MapMaid mapMaid = MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                .serializingAndDeserializing(ACyclicType.class)
                .build();
        given(mapMaid)
                .when().mapMaidSerializes(given1).withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"aString\": \"a\",\n" +
                        "  \"aCyclicType\": {\n" +
                        "    \"aString\": \"b\",\n" +
                        "    \"aCyclicType\": {\n" +
                        "      \"aString\": \"c\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void givenComplexDomainWithNullValues_whenSerializing_thenExcludesFromJson() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        null,
                        ANumber.fromInt(1),
                        null)).withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"1\",\n" +
                        "  \"stringA\": \"a\"\n" +
                        "}");
    }

    @Test
    public void givenComplexDomainUsingInjector_whenSerializing_thenReturnsJsonString() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializesWithInjector(
                AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(1),
                        ANumber.fromInt(2)),
                input -> {
                    input.put("stringA", "test");
                    return input;
                })
                .withMarshallingType(MarshallingType.JSON)
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"number1\": \"1\",\n" +
                        "  \"number2\": \"2\",\n" +
                        "  \"stringA\": \"test\",\n" +
                        "  \"stringB\": \"b\"\n" +
                        "}");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void nestedCollectionsCanBeSerialized() {
        final AString[][][][] nestedArray = {new AString[][][]{new AString[][]{new AString[]{AString.fromStringValue("arrays")}}}};
        final List<List<List<List<ANumber>>>> nestedList = List.of(List.of(List.of(List.of(ANumber.fromInt(42)))));
        final List<List<AString>[]>[] nestedMix1 = (List<List<AString>[]>[]) new List[]{singletonList((List<AString>[]) new List[]{singletonList(AString.fromStringValue("mixed"))})};
        final List<List<ANumber[]>[]> nestedMix2 = singletonList((List<ANumber[]>[]) new List[]{singletonList(new ANumber[]{ANumber.fromInt(43)})});
        final AComplexTypeWithNestedCollections typeWithNestedCollections = AComplexTypeWithNestedCollections.deserialize(
                nestedArray, nestedList, nestedMix1, nestedMix2);

        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithNestedCollections.class)
                        .build()
        )
                .when().mapMaidSerializes(typeWithNestedCollections)
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
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
                        "}");
    }

    public static class ANonConfiguredDomain {
    }
}
