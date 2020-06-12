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

public final class PrimitiveSchemaSpecs {

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForStringPrimitives() {
        given(
                aMapMaid()
                        .deserializing(String.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(String.class)
                .theSchemaWas("type: string\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForIntPrimitives() {
        given(
                aMapMaid()
                        .deserializing(int.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(int.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int32\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedIntegerPrimitives() {
        given(
                aMapMaid()
                        .deserializing(Integer.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Integer.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int32\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForLongPrimitives() {
        given(
                aMapMaid()
                        .deserializing(long.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(long.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int64\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedLongPrimitives() {
        given(
                aMapMaid()
                        .deserializing(Long.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Long.class)
                .theSchemaWas("" +
                        "type: integer\n" +
                        "format: int64\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForShortPrimitives() {
        given(
                aMapMaid()
                        .deserializing(short.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(short.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedShortPrimitives() {
        given(
                aMapMaid()
                        .deserializing(Short.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Short.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBytePrimitives() {
        given(
                aMapMaid()
                        .deserializing(byte.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(byte.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedBytePrimitives() {
        given(
                aMapMaid()
                        .deserializing(Byte.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Byte.class)
                .theSchemaWas("type: integer\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForDoublePrimitives() {
        given(
                aMapMaid()
                        .deserializing(double.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(double.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: double\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedDoublePrimitives() {
        given(
                aMapMaid()
                        .deserializing(Double.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Double.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: double\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForFloatPrimitives() {
        given(
                aMapMaid()
                        .deserializing(float.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(float.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: float\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedFloatPrimitives() {
        given(
                aMapMaid()
                        .deserializing(Float.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(Float.class)
                .theSchemaWas("" +
                        "type: number\n" +
                        "format: float\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBooleanPrimitives() {
        given(
                aMapMaid()
                        .deserializing(boolean.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(boolean.class)
                .theSchemaWas("type: boolean\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForBoxedBooleanPrimitives() {
        given(
                aMapMaid()
                        .deserializing(boolean.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(boolean.class)
                .theSchemaWas("type: boolean\n");
    }
}
