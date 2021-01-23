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

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public class DirectPrimitivesSpecs {

    @Test
    public void doubleCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(double.class)
                        .build()
        )
                .when().mapMaidDeserializes("2.2").from(JSON).toTheType(double.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(2.2);
    }

    @Test
    public void floatCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(float.class)
                        .build()
        )
                .when().mapMaidDeserializes("1.23").from(JSON).toTheType(float.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1.23F);
    }

    @Test
    public void floatWithExponentsCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(float.class)
                        .build()
        )
                .when().mapMaidDeserializes("1.23E-5").from(JSON).toTheType(float.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1.23E-5F);
    }

    @Test
    public void longCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(long.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(long.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1L);
    }

    @Test
    public void longWrittenAsDoubleCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(long.class)
                        .build()
        )
                .when().mapMaidDeserializes("1E12").from(JSON).toTheType(long.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1_000_000_000_000L);
    }

    @Test
    public void intCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(int.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(int.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(1);
    }

    @Test
    public void shortCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(short.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(short.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs((short) 1);
    }

    @Test
    public void byteCanBeRegisteredDirectly() {
        given(
                aMapMaid()
                        .serializingAndDeserializing(byte.class)
                        .build()
        )
                .when().mapMaidDeserializes("1").from(JSON).toTheType(byte.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs((byte) 1);
    }
}
