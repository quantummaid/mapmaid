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

import de.quantummaid.mapmaid.domain.*;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class CyclicReferencesSpecs {

    @Test
    public void givenCyclicType_whenSerializing_thenThrowsError() {
        final ACyclicType given1 = ACyclicType.deserialize(AString.fromStringValue("a"), null);
        final ACyclicType given2 = ACyclicType.deserialize(AString.fromStringValue("b"), null);
        given1.aCyclicType = given2;
        given2.aCyclicType = given1;

        given(
                aMapMaid()
                        .serializingAndDeserializing(ACyclicType.class)
                        .build()
        )
                .when().mapMaidSerializes(given1).withMarshallingType(MarshallingType.JSON)
                .anExceptionIsThrownWithAMessageContaining("a circular reference has been detected for objects " +
                        "of type de.quantummaid.mapmaid.domain.ACyclicType");
    }

    @Test
    public void givenNonCyclicTypeWithMultipleReferencesToSameInstance_whenSerializing_thenPassesValidation() {
        final AComplexType complexType = AComplexType.deserialize(
                AString.fromStringValue("foo"),
                AString.fromStringValue("bar"),
                ANumber.fromInt(42),
                ANumber.fromInt(21));
        final AComplexNestedType nonCyclicType = AComplexNestedType.deserialize(complexType, complexType);

        given(
                aMapMaid()
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .build()
        )
                .when().mapMaidSerializes(nonCyclicType).withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "{" +
                        "\"complexType2\":{" +
                        "\"number1\":\"42\"," +
                        "\"number2\":\"21\"," +
                        "\"stringA\":\"foo\"," +
                        "\"stringB\":\"bar\"" +
                        "}," +
                        "\"complexType1\":{" +
                        "\"number1\":\"42\"," +
                        "\"number2\":\"21\"," +
                        "\"stringA\":\"foo\"," +
                        "\"stringB\":\"bar\"" +
                        "}" +
                        "}");
    }
}
