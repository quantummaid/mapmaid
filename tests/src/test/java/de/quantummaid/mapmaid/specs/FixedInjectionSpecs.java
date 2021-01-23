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
import de.quantummaid.mapmaid.domain.AComplexType;
import de.quantummaid.mapmaid.domain.ANumber;
import de.quantummaid.mapmaid.domain.AString;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import org.junit.jupiter.api.Test;

public final class FixedInjectionSpecs {

    @Test
    public void aClassCanBeDeserializedUsingFixedInjection() {
        Given.given(
                MapMaid.aMapMaid()
                        .serializingAndDeserializing(AComplexType.class)
                        .injecting(ANumber.class, () -> ANumber.fromInt(42))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "   \"stringA\": \"a\",\n" +
                "   \"stringB\": \"b\",\n" +
                "   \"number1\": \"1\",\n" +
                "   \"number2\": \"1\"\n" +
                "}")
                .from(MarshallingType.JSON).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(
                        AString.fromStringValue("a"),
                        AString.fromStringValue("b"),
                        ANumber.fromInt(42),
                        ANumber.fromInt(42)
                ));
    }

    @Test
    public void aClassCanBeDeserializedUsingFixedInjectionEvenIfTheCorrespondingFieldIsNotPresent() {
        Given.given(
                MapMaid.aMapMaid()
                        .serializingAndDeserializing(AComplexType.class)
                        .injecting(ANumber.class, () -> ANumber.fromInt(42))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "   \"stringA\": \"a\",\n" +
                "   \"stringB\": \"b\"\n" +
                "}")
                .from(MarshallingType.JSON).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(
                        AComplexType.deserialize(
                                AString.fromStringValue("a"),
                                AString.fromStringValue("b"),
                                ANumber.fromInt(42),
                                ANumber.fromInt(42)
                        )
                );
    }
}
