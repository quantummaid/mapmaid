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

package de.quantummaid.mapmaid.specs.repositoryscanning;

import de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyComplexType;
import de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyString;
import de.quantummaid.mapmaid.testsupport.domain.half.ASerializationOnlyComplexType;
import de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexParameterizedType;
import de.quantummaid.mapmaid.testsupport.domain.valid.APrimitiveBoolean;
import de.quantummaid.mapmaid.testsupport.domain.valid.APrimitiveInteger;
import de.quantummaid.mapmaid.domain.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIn;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class RepositoryScanningSpecs {

    @Test
    public void classScannerRecipeRegistersReturnTypesAsSerializationOnly() {
        given(aMapMaid()
                .usingRecipe(addAllReferencedClassesIn(RepositoryWithSerializationOnlyType.class))
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                .build()
        )
                .when().mapMaidSerializes(ASerializationOnlyComplexType.init()).withMarshallingType(JSON)
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
                .from(JSON).toTheType(ADeserializationOnlyComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ADeserializationOnlyComplexType.deserialize(ADeserializationOnlyString.fromStringValue("foo")));
    }

    @Test
    public void referencesInClassesCanBeScanned() {
        given(
                aMapMaid()
                        .usingRecipe(addAllReferencedClassesIn(MyRepository.class))
                        .build()
        )
                .when().theDefinitionsAreQueried()
                .theDefinitionsContainExactlyTheSerializedObjects()
                .theDefinitionsContainExactlyTheCustomPrimitives(APrimitiveBoolean.class, APrimitiveInteger.class);
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanBeFoundAsAReferenceOfAScannedClass() {
        given(
                aMapMaid()
                        .usingRecipe(addAllReferencedClassesIn(RepositoryWithTypeVariableReference.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(AString.fromStringValue("foo")), genericType(AComplexParameterizedType.class, AString.class))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"foo\"\n" +
                        "}");
    }
}
