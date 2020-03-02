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
import de.quantummaid.mapmaid.testsupport.domain.parameterized.AComplexParameterizedType;
import de.quantummaid.mapmaid.testsupport.domain.repositories.RepositoryWithTypeVariableReference;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIs;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.shared.types.ArrayType.fromArrayClass;
import static de.quantummaid.mapmaid.shared.types.ClassType.fromClassWithoutGenerics;
import static de.quantummaid.mapmaid.shared.types.unresolved.UnresolvedType.unresolvedType;

public final class TypeVariableSpecs {

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeSerialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(AString.fromStringValue("foo")))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"foo\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableFieldsCanBeDeserialized() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(AString.class)))
                        .withManuallyAddedType(unresolvedType(AComplexParameterizedType.class).resolve(fromClassWithoutGenerics(ANumber.class)))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(ANumber.fromInt(42)))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": \"42\"\n" +
                        "}");
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanBeFoundAsAReferenceOfAScannedClass() {
        Given.given(
                MapMaid.aMapMaid()
                        .usingRecipe(addAllReferencedClassesIs(RepositoryWithTypeVariableReference.class))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(AString.fromStringValue("foo")))
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
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(
                                unresolvedType(AComplexParameterizedType.class)
                                        .resolve(unresolvedType(List.class).resolve(fromClassWithoutGenerics(AString.class))))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(List.of(AString.fromStringValue("foo"))))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("");
    }

    @Test
    public void aSerializedObjectWithTypeVariableCanSerializedIfTheValueOfTheTypeVariableIsAnArray() {
        Given.given(
                MapMaid.aMapMaid()
                        .withManuallyAddedType(
                                unresolvedType(AComplexParameterizedType.class)
                                        .resolve(fromArrayClass(AString[].class)))
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                        .build()
        )
                .when().mapMaidSerializes(AComplexParameterizedType.deserialize(new AString[]{AString.fromStringValue("foo")}))
                .withMarshallingType(json())
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"value\": [\n" +
                        "    \"foo\"\n" +
                        "  ]\n" +
                        "}");
    }
}
