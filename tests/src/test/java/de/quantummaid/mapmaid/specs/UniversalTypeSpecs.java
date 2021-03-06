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

import de.quantummaid.mapmaid.domain.ANumber;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.specs.examples.customprimitives.success.tostring.MyCustomPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class UniversalTypeSpecs {

    @Test
    public void mapMaidCanSerializeToUniversalObject() {
        given(
                aMapMaid()
                        .serializing(ANumber.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(ANumber.fromInt(2), ANumber.class)
                .theSerializationResultWas("2");
    }

    @Test
    public void mapMaidCanMarshalFromUniversalObject() {
        given(
                aMapMaid().build()
        )
                .when().mapMaidMarshalsFromUniversalObject(List.of("a", "b", "c"), MarshallingType.JSON)
                .theSerializationResultWas("[\"a\",\"b\",\"c\"]");
    }

    @Test
    public void nullCanBeDeserializedFromUniversalType() {
        given(
                aMapMaid()
                        .deserializing(MyCustomPrimitive.class)
                        .build()
        )
                .when().mapMaidDeserializesTheMap((Map<String, Object>) null).toTheType(MyCustomPrimitive.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(null);
    }
}
