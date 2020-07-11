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

import de.quantummaid.mapmaid.domain.*;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.yamlMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.yamlUnmarshaller;

public final class SchemaSpecs {

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForObjects() {
        given(
                aMapMaid()
                        .deserializing(AComplexType.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(AComplexType.class)
                .theSchemaWas("" +
                        "type: object\n" +
                        "properties:\n" +
                        "  number1:\n" +
                        "    type: string\n" +
                        "  number2:\n" +
                        "    type: string\n" +
                        "  stringA:\n" +
                        "    type: string\n" +
                        "  stringB:\n" +
                        "    type: string\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForObjects() {
        given(
                aMapMaid()
                        .serializing(AComplexType.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(AComplexType.class)
                .theSchemaWas("" +
                        "type: object\n" +
                        "properties:\n" +
                        "  number1:\n" +
                        "    type: string\n" +
                        "  number2:\n" +
                        "    type: string\n" +
                        "  stringA:\n" +
                        "    type: string\n" +
                        "  stringB:\n" +
                        "    type: string\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForCollections() {
        given(
                aMapMaid()
                        .deserializing(AComplexTypeWithCollections.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(AComplexTypeWithCollections.class)
                .theSchemaWas("" +
                        "type: object\n" +
                        "properties:\n" +
                        "  array:\n" +
                        "    type: array\n" +
                        "    items:\n" +
                        "      type: string\n" +
                        "  arrayList:\n" +
                        "    type: array\n" +
                        "    items:\n" +
                        "      type: string\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForCollections() {
        given(
                aMapMaid()
                        .serializing(AComplexTypeWithCollections.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(AComplexTypeWithCollections.class)
                .theSchemaWas("" +
                        "type: object\n" +
                        "properties:\n" +
                        "  array:\n" +
                        "    type: array\n" +
                        "    items:\n" +
                        "      type: string\n" +
                        "  arrayList:\n" +
                        "    type: array\n" +
                        "    items:\n" +
                        "      type: string\n");
    }

    @Test
    public void mapMaidCanGenerateDeserializationSchemaForPolymorphicHierarchy() {
        given(
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theDeserializationSchemaIsQueriedFor(AnInterface.class)
                .theSchemaWas("" +
                        "oneOf:\n" +
                        "- type:\n" +
                        "    pattern: de.quantummaid.mapmaid.domain.AnImplementation2\n" +
                        "    type: string\n" +
                        "  properties:\n" +
                        "    d:\n" +
                        "      type: string\n" +
                        "    c:\n" +
                        "      type: string\n" +
                        "- type:\n" +
                        "    pattern: de.quantummaid.mapmaid.domain.AnImplementation1\n" +
                        "    type: string\n" +
                        "  properties:\n" +
                        "    a:\n" +
                        "      type: string\n" +
                        "    b:\n" +
                        "      type: string\n");
    }

    @Test
    public void mapMaidCanGenerateSerializationSchemaForPolymorphicHierarchy() {
        given(
                aMapMaid()
                        .serializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(yamlMarshaller(), yamlUnmarshaller()))
                        .build()
        )
                .when().theSerializationSchemaIsQueriedFor(AnInterface.class)
                .theSchemaWas("" +
                        "oneOf:\n" +
                        "- type:\n" +
                        "    pattern: de.quantummaid.mapmaid.domain.AnImplementation2\n" +
                        "    type: string\n" +
                        "  properties:\n" +
                        "    d:\n" +
                        "      type: string\n" +
                        "    c:\n" +
                        "      type: string\n" +
                        "- type:\n" +
                        "    pattern: de.quantummaid.mapmaid.domain.AnImplementation1\n" +
                        "    type: string\n" +
                        "  properties:\n" +
                        "    a:\n" +
                        "      type: string\n" +
                        "    b:\n" +
                        "      type: string\n");
    }
}
