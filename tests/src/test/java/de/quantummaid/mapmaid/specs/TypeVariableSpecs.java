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

import de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexParameterizedType;
import de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexTypeWithParameterizedUnusedMethods;
import de.quantummaid.mapmaid.testsupport.domain.repositories.RepositoryWithTypeVariableReference;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIn;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.shared.types.ArrayType.fromArrayClass;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.types.ResolvedType.resolvedType;
import static de.quantummaid.mapmaid.shared.types.unresolved.UnresolvedType.unresolvedType;
import static de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexParameterizedType.deserialize;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

public final class TypeVariableSpecs {

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeSerialized() {
        given(
                aMapMaid()
                        .mapping(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(deserialize(AString.fromStringValue("foo")))
                .withMarshallingType(json())
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
                        .mapping(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"value\": \"foo\"\n" +
                "}").from(json()).toTheType(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                .noExceptionHasBeenThrown();
    }

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeRegisteredTwice() {
        given(
                aMapMaid()
                        .mapping(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .mapping(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(ANumber.class)))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(deserialize(ANumber.fromInt(42)))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"42\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanBeFoundAsAReferenceOfAScannedClass() {
        given(
                aMapMaid()
                        .usingRecipe(addAllReferencedClassesIn(RepositoryWithTypeVariableReference.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(deserialize(AString.fromStringValue("foo")))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"foo\"\n" +
                        "}");
    }

    @Disabled
    @Test
    public void aSerializedObjectWithTypeVariableFieldCanBeSerializedIfTheValueOfTheTypeVariableIsAnInterfaceWithTypeVariables() {
        given(
                aMapMaid()
                        .mapping(
                                unresolvedType(AComplexParameterizedType.class)
                                        .resolve(unresolvedType(List.class).resolve(fromClassWithoutGenerics(AString.class))))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(deserialize(List.of(AString.fromStringValue("foo"))))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("");
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanSerializedIfTheValueOfTheTypeVariableIsAnArray() {
        given(
                aMapMaid()
                        .mapping(
                                unresolvedType(AComplexParameterizedType.class)
                                        .resolve(fromArrayClass(AString[].class)))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(deserialize(new AString[]{AString.fromStringValue("foo")}))
                .withMarshallingType(json())
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
                        .mapping(
                                resolvedType(AComplexTypeWithParameterizedUnusedMethods.class))
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(
                AComplexTypeWithParameterizedUnusedMethods.deserialize(
                        AString.fromStringValue("foo"),
                        AString.fromStringValue("bar"),
                        ANumber.fromInt(42),
                        ANumber.fromInt(21)))
                .withMarshallingType(json())
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
