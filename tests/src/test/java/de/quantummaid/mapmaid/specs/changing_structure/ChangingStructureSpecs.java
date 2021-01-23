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

package de.quantummaid.mapmaid.specs.changing_structure;

import de.quantummaid.mapmaid.domain.AComplexType;
import de.quantummaid.mapmaid.domain.AString;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserialization;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serialization;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public final class ChangingStructureSpecs {

    @Test
    public void serializationGetsRemovedDuringProcessing() {
        given(
                aMapMaid()
                        .serializing(SerializationDto.class)
                        .serializingAndDeserializing(genericType(MakeDuplexDto.class, SerializationDto.class))
                        .build()
        )
                .when().mapMaidSerializes(CustomPrimitive.customPrimitive("foo")).withMarshallingType(JSON)
                .anExceptionIsThrownWithAMessageContaining("no definition found for type 'de.quantummaid.mapmaid.specs.changing_structure.CustomPrimitive'." +
                        " Known definitions are:");
    }

    @Test
    public void deserializationGetsRemovedDuringProcessing() {
        given(
                aMapMaid()
                        .deserializing(DeserializationDto.class)
                        .serializingAndDeserializing(genericType(MakeDuplexDto.class, DeserializationDto.class))
                        .build()
        )
                .when().mapMaidDeserializes("\"foo\"").from(JSON).toTheType(CustomPrimitive.class)
                .anExceptionIsThrownWithAMessageContaining("no definition found for type 'de.quantummaid.mapmaid.specs.changing_structure.CustomPrimitive'." +
                        " Known definitions are:");
    }

    @Test
    public void canBecomeDuplexFromSerializationOnly() {
        given(
                aMapMaid()
                        .serializing(AString.class)
                        .deserializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidDeserializes("\"foo\"").from(JSON).toTheType(AString.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(AString.fromStringValue("foo"));
    }

    @Test
    public void canBecomeDuplexFromDeserializationOnly() {
        given(
                aMapMaid()
                        .deserializing(AString.class)
                        .serializing(AComplexType.class)
                        .build()
        )
                .when().mapMaidSerializes(AString.fromStringValue("foo")).withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("\"foo\"");
    }

    @Test
    public void serializationCanBeAddedToDeserializationOnlyTypeFromWithinNestedType() {
        given(
                aMapMaid()
                        .withType(Address.class, deserialization())
                        .withType(Order.class, serialization())
                        .build()
        )
                .when().mapMaidSerializes(
                new Address(
                        new Zip("a"),
                        new City("b"),
                        new Street("c"),
                        new StreetNumber("d")
                ))
                .withMarshallingType(JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("{\"zip\":\"a\",\"number\":\"d\",\"city\":\"b\",\"street\":\"c\"}");
    }
}
