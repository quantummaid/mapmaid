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
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimitiveMappingSpecs {

    @Test
    public void doubleCanBeMappedToString() {
        given(
                aMapMaid()
                        .deserializing(String.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.2").from(JSON).toTheType(String.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs("2.2");
    }

    @Test
    public void doubleWithoutDecimalsCanBeMappedToLong() {
        given(
                aMapMaid()
                        .deserializing(long.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.0").from(JSON).toTheType(long.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(2L);
    }

    @Test
    public void doubleWithoutDecimalsCanBeMappedToInt() {
        given(
                aMapMaid()
                        .deserializing(int.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.0").from(JSON).toTheType(int.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(2);
    }

    @Test
    public void doubleWithoutDecimalsCanBeMappedToShort() {
        given(
                aMapMaid()
                        .deserializing(short.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.0").from(JSON).toTheType(short.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs((short)2);
    }

    @Test
    public void doubleWithoutDecimalsCanBeMappedToByte() {
        given(
                aMapMaid()
                        .deserializing(byte.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.0").from(JSON).toTheType(byte.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs((byte)2);
    }

    @Test
    public void longCanBeMappedToString() {
        given(
                aMapMaid()
                        .deserializing(String.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(String.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs("1");
    }

    @Test
    public void longCanBeMappedToDouble() {
        given(
                aMapMaid()
                        .deserializing(double.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(double.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1.0);
    }

    @Test
    public void longCanBeMappedToFloat() {
        given(
                aMapMaid()
                        .deserializing(float.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(float.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1.0F);
    }

    @Test
    public void booleanCanBeMappedToString() {
        given(
                aMapMaid()
                        .deserializing(String.class)
                        .build()
        )
                .when().mapMaidDeserializes("true").from(JSON).toTheType(String.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs("true");
    }

    @Test
    public void floatCanBeMappedToDouble() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(Float.class)
                .build();
        final String json = mapMaid.serializeToJson(1.3f);
        assertEquals("1.3", json);
    }
}
