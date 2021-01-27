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

package de.quantummaid.mapmaid.dynamodb;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE;
import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshallerAndUnmarshaller.attributeValueMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.util.Asserters.storeAndLoadAgain;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

public final class AttributeValueMarshallerSpecs {
    private static final String PRIMARY_KEY = "id";
    private static final String VALUE_IDENTIFIER = "value";

    @Test
    public void nullCanBeMarshalled() {
        assertRoundTrip(null);
    }

    @Test
    public void stringCanBeMarshalled() {
        assertRoundTrip("abc");
        assertRoundTrip("");
    }

    @Test
    public void intCanBeMarshalled() {
        assertRoundTrip(1, 1L);
        assertRoundTrip(0, 0L);
    }

    @Test
    public void longCanBeMarshalled() {
        assertRoundTrip(1L);
        assertRoundTrip(0L);
    }

    @Test
    public void booleanCanBeMarshalled() {
        assertRoundTrip(true);
        assertRoundTrip(false);
    }

    @Test
    public void mapCanBeMarshalled() {
        assertRoundTrip(Map.of("a", "b", "c", 1L));
        assertRoundTrip(Map.of());
    }

    @Test
    public void listCanBeMarshalled() {
        assertRoundTrip(List.of("a", 1L, true, Map.of()));
        assertRoundTrip(List.of());
    }

    private static void assertRoundTrip(final Object value) {
        assertRoundTrip(value, value);
    }

    private static void assertRoundTrip(final Object value, final Object expectedAfter) {
        final MapMaid mapMaid = aMapMaid()
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(attributeValueMarshallerAndUnmarshaller()))
                .build();

        final AttributeValue beforeRoundTripAttributeValue = mapMaid.serializer().marshalFromUniversalObject(value, DYNAMODB_ATTRIBUTEVALUE);
        final String id = UUID.randomUUID().toString();
        final Map<String, AttributeValue> wrappedMap = Map.of(
                PRIMARY_KEY, builder().s(id).build(),
                VALUE_IDENTIFIER, beforeRoundTripAttributeValue
        );

        final Map<String, AttributeValue> loaded = storeAndLoadAgain(PRIMARY_KEY, wrappedMap);
        final AttributeValue attributeValue = loaded.get(VALUE_IDENTIFIER);
        final Object deserialized = mapMaid.deserializer()
                .deserializeToUniversalObject(attributeValue, DYNAMODB_ATTRIBUTEVALUE);

        assertThat(deserialized, is(expectedAfter));
    }
}
