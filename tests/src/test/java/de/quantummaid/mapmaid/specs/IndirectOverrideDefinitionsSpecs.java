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
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexNestedType;
import de.quantummaid.mapmaid.testsupport.domain.valid.AComplexType;
import de.quantummaid.mapmaid.testsupport.domain.valid.ANumber;
import de.quantummaid.mapmaid.testsupport.domain.valid.AString;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Given;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.customPrimitive;
import static de.quantummaid.mapmaid.builder.customtypes.DuplexType.serializedObject;

public final class IndirectOverrideDefinitionsSpecs {

    @Test
    public void customDeserializationForCustomPrimitiveOverridesIndirectDefault() {
        Given.given(
                MapMaid.aMapMaid()
                        .serializingAndDeserializing(AComplexType.class)
                        .serializingAndDeserializing(customPrimitive(ANumber.class, object -> "23", value -> ANumber.fromInt(23)))
                        .withAdvancedSettings(advancedBuilder -> {
                            advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller());
                        })
                        .build()
        )
                .when().mapMaidDeserializes("42").from(MarshallingType.JSON).toTheType(ANumber.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(ANumber.fromInt(23));
    }

    @Test
    public void customDeserializationForSerializedObjectOverridesIndirectDefault() {
        Given.given(
                MapMaid.aMapMaid()
                        .serializingAndDeserializing(AComplexNestedType.class)
                        .serializingAndDeserializing(serializedObject(AComplexType.class)
                                .withField("foo", AString.class, object -> AString.fromStringValue("bar"))
                                .deserializedUsing(foo -> AComplexType.deserialize(
                                        AString.fromStringValue("custom1"),
                                        AString.fromStringValue("custom2"),
                                        ANumber.fromInt(100),
                                        ANumber.fromInt(200)
                                ))
                        )
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(Marshallers.jsonMarshaller(), Unmarshallers.jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidDeserializes("{\"foo\": \"qwer\"}").from(MarshallingType.JSON).toTheType(AComplexType.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AComplexType.deserialize(AString.fromStringValue("custom1"), AString.fromStringValue("custom2"), ANumber.fromInt(100), ANumber.fromInt(200)));
    }
}
