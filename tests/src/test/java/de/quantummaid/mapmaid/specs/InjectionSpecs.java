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

import de.quantummaid.mapmaid.testsupport.domain.valid.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;
import static java.util.Collections.singletonList;

public final class InjectionSpecs {

    @Test
    public void givenComplexTypeJsonWithInjectorUsingPropertyNameAndInstance_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                        "}",
                injector -> injector
                        .put("complexType1.stringB", AString.fromStringValue("test"))
                        .put("complexType2.number1", ANumber.fromStringValue("45")))
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("a"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("c"),
                                AString.fromStringValue("d"),
                                ANumber.fromInt(45),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenComplexTypeJsonWithInjectorUsingInstance_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                        "}",
                injector -> injector
                        .put(AString.fromStringValue("test"))
                        .put(AString.fromStringValue("test")))
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(3),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenComplexTypeJsonWithInjectorUsingInstanceAndType_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                        "}",
                injector -> injector.put(AString.class, AString.fromStringValue("test")))
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(3),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenComplexTypeWithIncompleteJsonWithInjectorUsingPropertyPath_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\"}" +
                        "}",
                injector -> injector.put("complexType2.stringA", AString.fromStringValue("test")))
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("a"),
                                AString.fromStringValue("b"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                null,
                                ANumber.fromInt(3),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenComplexTypeWithIncompleteJsonWithInjectorUsingInstance_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\"}" +
                        "}",
                injector -> injector.put(AString.fromStringValue("test"))
        )
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("test"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(3),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void givenComplexTypeJsonWithInjectorUsingPropertyNameAndStringValue_whenDeserializing_thenReturnAComplexObject() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{" +
                        "\"complexType1\":" +
                        "{\"number1\":\"1\",\"number2\":\"2\",\"stringA\":\"a\",\"stringB\":\"b\"}," +
                        "\"complexType2\":" +
                        "{\"number1\":\"3\",\"number2\":\"4\",\"stringA\":\"c\",\"stringB\":\"d\"}" +
                        "}",
                injector -> injector
                        .put("complexType1.stringB", "test")
                        .put("complexType2.number1", "45")
        )
                .from(JSON).toTheType(AComplexNestedType.class)
                .theDeserializedObjectIs(AComplexNestedType.deserialize(
                        AComplexType.deserialize(
                                AString.fromStringValue("a"),
                                AString.fromStringValue("test"),
                                ANumber.fromInt(1),
                                ANumber.fromInt(2)
                        ),
                        AComplexType.deserialize(
                                AString.fromStringValue("c"),
                                AString.fromStringValue("d"),
                                ANumber.fromInt(45),
                                ANumber.fromInt(4)
                        )
                ));
    }

    @Test
    public void universalInjectionIntoCollectionIsPossible() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(AComplexTypeWithCollections.class)
                        .build()
        )
                .when().mapMaidDeserializesWithInjection("{ \"arrayList\": [\"not_injected\"], \"array\": [\"1\"] }",
                injector -> {
                    injector.put("arrayList.[0]", "injected");
                    injector.put("array.[0]", "42");
                })
                .from(JSON).toTheType(AComplexTypeWithCollections.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(
                        AComplexTypeWithCollections.deserialize(
                                singletonList(AString.fromStringValue("injected")),
                                new ANumber[]{ANumber.fromInt(42)}
                        ));
    }
}
