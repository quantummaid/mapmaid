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

import de.quantummaid.mapmaid.testsupport.domain.half.*;
import de.quantummaid.mapmaid.testsupport.domain.repositories.RepositoryWithDeserializationOnlyType;
import de.quantummaid.mapmaid.testsupport.domain.repositories.RepositoryWithSerializationOnlyType;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIn;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class HalfDefinitionsSpecs {

    @Test
    public void aCustomPrimitiveCanBeSerializationOnly() {
        given(
                aMapMaid()
                        .mapping(ASerializationOnlyString.class, serialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyString.init()).withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("\"theValue\"");
    }

    @Test
    public void aCustomPrimitiveCanBeDeserializationOnly() {
        given(
                aMapMaid()
                        .mapping(ADeserializationOnlyString.class, deserialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("\"foo\"").from(json()).toTheType(ADeserializationOnlyString.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyString.fromStringValue("foo"));
    }

    @Test
    public void aSerializedObjectCanBeSerializationOnly() {
        given(
                aMapMaid()
                        .mapping(ASerializationOnlyComplexType.class, serialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyComplexType.init()).withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"string\": \"theValue\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectCanBeDeserializationOnly() {
        given(
                aMapMaid()
                        .mapping(ADeserializationOnlyComplexType.class, deserialization())
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"string\": \"foo\"\n" +
                "}")
                .from(json()).toTheType(ADeserializationOnlyComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyComplexType.deserialize(ADeserializationOnlyString.fromStringValue("foo")));
    }

    @Test
    public void mapMaidCanValidateThatSerializationWorks() {
        given(() -> aMapMaid()
                .mapping(AnUnresolvableSerializationOnlyComplexType.class, serialization())
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyString: unable to detect serializer\n" +
                        "\n" +
                        "de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyString:\n" +
                        "Mode: serialization-only\n" +
                        "How it is serialized:\n" +
                        "\tNo serializer available\n" +
                        "Why it needs to be serializable:\n" +
                        "\t- because of de.quantummaid.mapmaid.testsupport.domain.half.AnUnresolvableSerializationOnlyComplexType\n" +
                        "Ignored features for serialization:");
    }

    @Test
    public void mapMaidCanValidateThatDeserializationWorks() {
        given(() -> aMapMaid()
                .mapping(AnUnresolvableDeserializationOnlyComplexType.class, deserialization())
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("de.quantummaid.mapmaid.testsupport.domain.half.ASerializationOnlyString: unable to detect deserializer\n" +
                        "\n" +
                        "de.quantummaid.mapmaid.testsupport.domain.half.ASerializationOnlyString:\n" +
                        "Mode: deserialization-only\n" +
                        "How it is deserialized:\n" +
                        "\tNo deserializer available\n" +
                        "Why it needs to be deserializable:\n" +
                        "\t- because of de.quantummaid.mapmaid.testsupport.domain.half.AnUnresolvableDeserializationOnlyComplexType\n" +
                        "Ignored features for deserialization:");
    }

    @Test
    public void classScannerRecipeRegistersReturnTypesAsSerializationOnly() {
        given(aMapMaid()
                .usingRecipe(addAllReferencedClassesIn(RepositoryWithSerializationOnlyType.class))
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyComplexType.init()).withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"string\": \"theValue\"\n" +
                        "}");
    }

    @Test
    public void classScannerRecipeRegistersParametersAsDeserializationOnly() {
        given(
                aMapMaid()
                        .usingRecipe(addAllReferencedClassesIn(RepositoryWithDeserializationOnlyType.class))
                        .withAdvancedSettings(advancedBuilder ->
                                advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"string\": \"foo\"\n" +
                "}")
                .from(json()).toTheType(ADeserializationOnlyComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyComplexType.deserialize(ADeserializationOnlyString.fromStringValue("foo")));
    }
}
