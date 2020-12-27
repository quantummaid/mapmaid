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

import de.quantummaid.mapmaid.domain.AComplexType;
import de.quantummaid.mapmaid.domain.AString;
import de.quantummaid.mapmaid.mapper.deserialization.PredeserializedObjectCannotBeDeserialized;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class IndirectInjectionSpecs {

    @Test
    public void indirectInjectionIsNotPossible() {
        final Map<String, Object> input = Map.of(
                "stringA", "a",
                "stringB", AString.fromStringValue("b"),
                "number1", "1",
                "number2", "2"
        );
        given(
                aMapMaid()
                        .deserializing(AComplexType.class)
                        .build())
                .when().mapMaidDeserializesTheMap(input).toTheType(AComplexType.class)
                .anExceptionIsThrownWithAMessageContaining("Pre-deserialized objects are not supported for " +
                        "deserialization'. Please use injections to add pre-deserialized objects.")
                .anExceptionOfClassIsThrownFulfilling(PredeserializedObjectCannotBeDeserialized.class, e -> {
                    assertThat(e.objectToSerialize(), equalTo(AString.fromStringValue("b")));
                });
    }
}
