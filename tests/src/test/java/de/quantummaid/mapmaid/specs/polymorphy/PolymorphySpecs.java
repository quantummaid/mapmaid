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

import de.quantummaid.mapmaid.domain.*;
import de.quantummaid.mapmaid.polymorphy.MissingPolymorphicTypeFieldException;
import de.quantummaid.mapmaid.polymorphy.UnknownPolymorphicSubtypeException;
import de.quantummaid.reflectmaid.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.JSON;
import static de.quantummaid.mapmaid.specs.polymorphy.PrimitiveSubtype.primitiveSubtype;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public final class PolymorphySpecs {

    @Test
    public void theTypeIdentifierKeyCanBeConfigured() {
        given(
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.withTypeIdentifierKey("test"))
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
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.withTypeIdentifierExtractor(typeIdentifier -> {
                            final Class<?> assignableType = typeIdentifier.getRealType().assignableType();
                            if (assignableType.equals(AnImplementation1.class)) {
                                return "impl1";
                            } else if (assignableType.equals(AnImplementation2.class)) {
                                return "impl2";
                            } else {
                                throw new UnsupportedOperationException();
                            }
                        }))
                        .build()
        )
                .when().mapMaidDeserializes("" +
                "{\n" +
                "  \"type\": \"impl1\",\n" +
                "  \"a\": \"foo\",\n" +
                "  \"b\": \"bar\"\n" +
                "}").from(JSON).toTheType(AnInterface.class)
                .theDeserializedObjectIs(new AnImplementation1("foo", "bar"));
    }

    @Test
    public void polymorphicSubtypesCannotBePrimitiveInlined() {
        given(() ->
                aMapMaid()
                        .serializingAndDeserializingSubtypes(Supertype.class, PrimitiveSubtype.class)
                        .build()
        )
                .when().mapMaidSerializes(primitiveSubtype("abc"), Supertype.class).withMarshallingType(JSON)
                .theSerializationResultWas("{\"type\":\"de.quantummaid.mapmaid.specs.polymorphy.PrimitiveSubtype\",\"internalValue\":\"abc\"}");
    }

    @Test
    public void polymorphicSubtypesCauseInitializationToFailIfTheyCanOnlyBePrimitive() {
        given(() ->
                aMapMaid()
                        .serializingAndDeserializingSubtypes(Supertype.class, PrimitiveOnlySubtype.class)
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("de.quantummaid.mapmaid.specs.polymorphy.PrimitiveOnlySubtype: unable to detect duplex:");
    }

    @Test
    public void failsWhenInputIsNotAnObject() {
        given(() ->
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .build()
        )
                .when().mapMaidDeserializes("\"a\"").from(JSON).toTheType(AnInterface.class)
                .anExceptionIsThrownWithAMessageContaining("Requiring the input to be an 'object'");
    }

    @Test
    public void failsForMissingSubTypeFieldDuringDeserialization() {
        given(() ->
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class, AnImplementation2.class)
                        .build()
        )
                .when().mapMaidDeserializes("{}").from(JSON).toTheType(AnInterface.class)
                .anExceptionIsThrownWithAMessageContaining("Missing 'type' field to identify polymorphic subtype " +
                        "for type de.quantummaid.mapmaid.domain.AnInterface")
                .anExceptionOfClassIsThrownFulfilling(MissingPolymorphicTypeFieldException.class, e -> {
                    assertThat(e.input, equalTo(emptyMap()));
                });
    }

    @Test
    public void failsForUnknownSubTypeDuringDeserialization() {
        final String input = "{" +
                "\"type\":\"UnknownClass\"" +
                "}";
        given(() ->
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class)
                        .build()
        )
                .when().mapMaidDeserializes(input).from(JSON).toTheType(AnInterface.class)
                .anExceptionIsThrownWithAMessageContaining("Unknown type 'UnknownClass' (needs to be a subtype of " +
                        "de.quantummaid.mapmaid.domain.AnInterface, " +
                        "known subtype identifiers: [de.quantummaid.mapmaid.domain.AnImplementation1])"
                )
                .anExceptionOfClassIsThrownFulfilling(UnknownPolymorphicSubtypeException.class, e -> {
                    assertThat(e.input(), equalTo(Map.of("type", "UnknownClass")));
                });
    }

    @Test
    public void nullCanBeDeserializedPolymorphically() {
        given(() ->
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, AnImplementation1.class)
                        .build()
        )
                .when().mapMaidDeserializes("null").from(JSON).toTheType(AnInterface.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(null);
    }

    @Test
    public void serializationWorksForParameterizedSubtypes() {
        given(() ->
                aMapMaid()
                        .serializingSubtypes(
                                genericType(new TypeToken<GenericInterface<String>>() {
                                }),
                                genericType(new TypeToken<GenericImplementation<String>>() {
                                })
                        )
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(
                new GenericImplementation<String>("a", "b", "c"),
                genericType(new TypeToken<GenericInterface<String>>() {
                }))
                .theSerializationResultWas(Map.of(
                        "field0", "a",
                        "field1", "b",
                        "field2", "c",
                        "type", "de.quantummaid.mapmaid.domain.GenericImplementation<java.lang.String>"
                ));
    }

    @Test
    public void serializationFailsAtBuildTimeIfTypeCannotBeDeterminedAtRuntime() {
        given(() ->
                aMapMaid()
                        .serializingSubtypes(
                                genericType(Object.class),
                                genericType(new TypeToken<List<String>>() {
                                }),
                                genericType(new TypeToken<List<Boolean>>() {
                                })
                        )
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("not possible to reliably determine the generic type " +
                        "of objects of type 'java.util.List' based on pool of possible types: " +
                        "[java.util.List<java.lang.String>, java.util.List<java.lang.Boolean>]");
    }

    @Test
    public void nestedInterfacesCanBeSerialized() {
        given(
                aMapMaid()
                        .serializingSubtypes(AnInterface.class, SubInterface.class)
                        .serializingSubtypes(SubInterface.class, SubImplementation1.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(new SubImplementation1("a", "b"), AnInterface.class)
                .theSerializationResultWas(Map.of(
                        "a", "a",
                        "b", "b",
                        "type", "de.quantummaid.mapmaid.domain.SubImplementation1",
                        "_type", "de.quantummaid.mapmaid.domain.SubInterface"
                ));
    }

    @Test
    public void nestedInterfacesCanBeDeserialized() {
        given(
                aMapMaid()
                        .deserializingSubtypes(AnInterface.class, SubInterface.class)
                        .deserializingSubtypes(SubInterface.class, SubImplementation1.class)
                        .build()
        )
                .when().mapMaidDeserializesTheMap(
                Map.of(
                        "a", "a",
                        "b", "b",
                        "type", "de.quantummaid.mapmaid.domain.SubImplementation1",
                        "_type", "de.quantummaid.mapmaid.domain.SubInterface"
                )).toTheType(AnInterface.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(new SubImplementation1("a", "b"));
    }
}
