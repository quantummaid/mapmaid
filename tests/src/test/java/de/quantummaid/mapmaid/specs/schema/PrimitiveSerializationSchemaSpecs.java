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

package de.quantummaid.mapmaid.specs.schema;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.yamlMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.yamlUnmarshaller;

public final class PrimitiveSerializationSchemaSpecs {

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForStringPrimitives() {
        given(
                aMapMaid()
                        .serializing(String.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(String.class)
                .theSchemaWas("type: string\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForIntPrimitives() {
        given(
                aMapMaid()
                        .serializing(int.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(int.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int32\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedIntegerPrimitives() {
        given(
                aMapMaid()
                        .serializing(Integer.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Integer.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int32\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForLongPrimitives() {
        given(
                aMapMaid()
                        .serializing(long.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(long.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int64\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedLongPrimitives() {
        given(
                aMapMaid()
                        .serializing(Long.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Long.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int64\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForShortPrimitives() {
        given(
                aMapMaid()
                        .serializing(short.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(short.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedShortPrimitives() {
        given(
                aMapMaid()
                        .serializing(Short.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Short.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBytePrimitives() {
        given(
                aMapMaid()
                        .serializing(byte.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(byte.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedBytePrimitives() {
        given(
                aMapMaid()
                        .serializing(Byte.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Byte.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForDoublePrimitives() {
        given(
                aMapMaid()
                        .serializing(double.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(double.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: double\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedDoublePrimitives() {
        given(
                aMapMaid()
                        .serializing(Double.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Double.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: double\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForFloatPrimitives() {
        given(
                aMapMaid()
                        .serializing(float.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(float.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: float\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedFloatPrimitives() {
        given(
                aMapMaid()
                        .serializing(Float.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(Float.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: float\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBooleanPrimitives() {
        given(
                aMapMaid()
                        .serializing(boolean.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(boolean.class)
                .theSchemaWas("type: boolean\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForBoxedBooleanPrimitives() {
        given(
                aMapMaid()
                        .serializing(boolean.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(boolean.class)
                .theSchemaWas("type: boolean\n");
    }
}
