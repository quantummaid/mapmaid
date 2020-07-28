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

package de.quantummaid.mapmaid.specs.polymorphy;

import de.quantummaid.mapmaid.domain.AnImplementation1;
import de.quantummaid.mapmaid.domain.AnImplementation2;
import de.quantummaid.mapmaid.domain.AnInterface;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.specs.polymorphy.PrimitiveSubtype.primitiveSubtype;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

public final class PolymorphySpecs {

    @Test
    public void theTypeIdentifierKeyCanBeConfigured() {
        given(
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .withAdvancedSettings(advancedBuilder -> {
                            advancedBuilder.withTypeIdentifierKey("test");
                            advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller());
                        })
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"a\": \"foo\",\n" +
                "  \"b\": \"bar\",\n" +
                "  \"test\": \"de.quantummaid.mapmaid.domain.AnImplementation1\"\n" +
                "}").from(JSON).toTheType(AnInterface.class)
                .theDeserializedObjectIs(new AnImplementation1("foo", "bar"));
    }

    @Test
    public void theTypeIdentifierExtractorCanBeConfigured() {
        given(
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .withAdvancedSettings(advancedBuilder -> {
                            advancedBuilder.withTypeIdentifierExtractor(typeIdentifier -> {
                                final Class<?> assignableType = typeIdentifier.getRealType().assignableType();
                                if (assignableType.equals(AnImplementation1.class)) {
                                    return "impl1";
                                } else if (assignableType.equals(AnImplementation2.class)) {
                                    return "impl2";
                                } else {
                                    throw new UnsupportedOperationException();
                                }
                            });
                            advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller());
                        })
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"__type__\": \"impl1\",\n" +
                "  \"a\": \"foo\",\n" +
                "  \"b\": \"bar\"\n" +
                "}").from(JSON).toTheType(AnInterface.class)
                .theDeserializedObjectIs(new AnImplementation1("foo", "bar"));
    }

    @Test
    public void polymorphicSubtypesAreNotPrimitiveInlined() {
        given(
                aMapMaid()
                        .serializingAndDeserializingSubtypes(Supertype.class, PrimitiveSubtype.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .build()
        )
                .when().mapMaidSerializes(primitiveSubtype("abc"), Supertype.class).withMarshallingType(JSON)
                .theSerializationResultWas("" +
                        "{\n" +
                        "  \"__type__\": \"de.quantummaid.mapmaid.specs.polymorphy.PrimitiveSubtype\",\n" +
                        "  \"internalValue\": \"abc\"\n" +
                        "}");
    }
}
