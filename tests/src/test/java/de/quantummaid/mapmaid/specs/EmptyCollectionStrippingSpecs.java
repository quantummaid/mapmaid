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

import de.quantummaid.mapmaid.builder.AdvancedBuilder;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class EmptyCollectionStrippingSpecs {

    @Test
    public void emptyListIsStripped() {
        given(
                aMapMaid()
                        .serializingCustomObject(String.class, builder -> builder
                                .withField("field0", GenericType.genericType(new TypeToken<List<Integer>>() {
                                }), object -> List.of())
                                .withField("field1", Integer.class, object -> 1)
                        )
                        .withAdvancedSettings(AdvancedBuilder::strippingEmptyCollectionsWhenMarshalling)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject("abc", String.class)
                .theSerializationResultWas(Map.of("field1", 1L));
    }

    @Test
    public void nullValuesOfMapAreStripped() {
        given(
                aMapMaid()
                        .serializingCustomObject(String.class, builder -> builder
                                .withField("field0", Integer.class, object -> 1)
                                .withField("field1", InputStream.class, object -> null)
                                .withField("field2", Integer.class, object -> 2)
                        )
                        .serializingCustomPrimitive(InputStream.class, object -> null)
                        .withAdvancedSettings(AdvancedBuilder::strippingEmptyCollectionsWhenMarshalling)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject("abc", String.class)
                .theSerializationResultWas(Map.of("field0", 1L, "field2", 2L));
    }

    @Test
    public void mapWithOnlyNullValuesIsStripped() {
        given(
                aMapMaid()
                        .serializingCustomObject(String.class, builder -> builder
                                .withField("field0", InputStream.class, object -> null)
                                .withField("field1", InputStream.class, object -> null)
                                .withField("field2", InputStream.class, object -> null)
                        )
                        .serializingCustomPrimitive(InputStream.class, object -> null)
                        .withAdvancedSettings(AdvancedBuilder::strippingEmptyCollectionsWhenMarshalling)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject("abc", String.class)
                .theSerializationResultWas(null);
    }
}
