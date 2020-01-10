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
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serializationOnly;
import static de.quantummaid.mapmaid.builder.recipes.scanner.ClassScannerRecipe.addAllReferencedClassesIn;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class HalfDefinitionsSpecs {

    @Test
    public void aCustomPrimitiveCanBeSerializationOnly() {
        given(
                aMapMaid()
                        .withManuallyAddedType(ASerializationOnlyString.class, serializationOnly())
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
                        .withManuallyAddedType(ADeserializationOnlyString.class, deserializationOnly())
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
                        .withManuallyAddedType(ASerializationOnlyComplexType.class, serializationOnly())
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
                        .withManuallyAddedType(ADeserializationOnlyComplexType.class, deserializationOnly())
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
                .withManuallyAddedType(AnUnresolvableSerializationOnlyComplexType.class, serializationOnly())
                .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("Type 'de.quantummaid.mapmaid.testsupport.domain.half.ADeserializationOnlyString' is not registered but " +
                        "needs to be in order to support serialization of 'de.quantummaid.mapmaid.testsupport.domain.half.AnUnresolvableSerializationOnlyComplexType'");
    }

    @Test
    public void mapMaidCanValidateThatDeserializationWorks() {
        given(() -> aMapMaid()
                .withManuallyAddedType(AnUnresolvableDeserializationOnlyComplexType.class, deserializationOnly())
                .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
                .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("Type 'de.quantummaid.mapmaid.testsupport.domain.half.ASerializationOnlyString' is not registered " +
                        "but needs to be in order to support deserialization of 'de.quantummaid.mapmaid.testsupport.domain.half.AnUnresolvableDeserializationOnlyComplexType'");
    }

    @Test
    public void classScannerRecipeRegistersReturnTypesAsSerializationOnly() {
        given(aMapMaid()
                .usingRecipe(addAllReferencedClassesIn(RepositoryWithSerializationOnlyType.class))
                .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
                        .usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller())
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
