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

import java.util.Map;

import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerAndUnmarshaller.urlEncodedMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.customdynamodb.CustomDynamoDbMarshallerAndUnmarshaller.CUSTOM_DYNAMO_DB;
import static de.quantummaid.mapmaid.dynamodb.customdynamodb.CustomDynamoDbMarshallerRecipe.aDynamoDbMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.util.Asserters.storeAndLoadAgain;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;

public final class CustomDynamoDbMarshallerSpecs {
    private static final String PRIMARY_KEY = "id";

    @Test
    public void customDynamoDbCanMarshalWithoutCompression() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .usingRecipe(aDynamoDbMarshallerAndUnmarshaller())
                .build();
        final Map<String, AttributeValue> marshalled = assertRoundTrip(Map.of(
                PRIMARY_KEY, "a",
                "b", "foo",
                "c", "bar"
        ), mapMaid);

        assertThat(marshalled, aMapWithSize(3));
        assertThat(marshalled, hasKey(PRIMARY_KEY));
        assertThat(marshalled, hasKey("b"));
        assertThat(marshalled, hasKey("c"));

        assertThat(marshalled.get("b").s(), is("foo"));
    }

    @Test
    public void customDynamoDbCanMarshalWithSingleEntryCompression() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .usingRecipe(aDynamoDbMarshallerAndUnmarshaller().compressingTopLevelProperty("b"))
                .build();
        final Map<String, AttributeValue> marshalled = assertRoundTrip(Map.of(
                PRIMARY_KEY, "a",
                "b", "fooooooooooooooooooooooooooooooo",
                "c", "bar"
        ), mapMaid);

        assertThat(marshalled, aMapWithSize(3));
        assertThat(marshalled, hasKey(PRIMARY_KEY));
        assertThat(marshalled, hasKey("b"));
        assertThat(marshalled, hasKey("c"));

        assertThat(marshalled.get("b").b(), is(notNullValue()));
    }

    @Test
    public void customDynamoDbCanMarshalWithMultipleEntriesCompressedIntoOneField() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .usingRecipe(
                        aDynamoDbMarshallerAndUnmarshaller()
                                .compressingTopLevelProperties("a", "b").intoDynamoDbAttribute("c")
                )
                .build();

        final Map<String, AttributeValue> marshalled = assertRoundTrip(Map.of(
                PRIMARY_KEY, "a",
                "a", "foo",
                "b", "bar"
        ), mapMaid);

        assertThat(marshalled.size(), is(2));
        assertThat(marshalled, hasKey(PRIMARY_KEY));
        assertThat(marshalled, hasKey("c"));
        assertThat(marshalled, not(hasKey("a")));
        assertThat(marshalled, not(hasKey("b")));

        assertThat(marshalled.get("c").b(), is(notNullValue()));
    }

    @Test
    public void customDynamoDbCanHaveDifferentInternalMarshallingFormat() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .usingRecipe(
                        aDynamoDbMarshallerAndUnmarshaller()
                                .compressingTopLevelProperties("a", "b").intoDynamoDbAttribute("c")
                                .usingMarshallerInsideOfCompression(urlEncodedMarshallerAndUnmarshaller())
                )
                .build();

        final Map<String, AttributeValue> marshalled = assertRoundTrip(Map.of(
                PRIMARY_KEY, "a",
                "a", "foo",
                "b", "bar"
        ), mapMaid);

        assertThat(marshalled.size(), is(2));
        assertThat(marshalled, hasKey(PRIMARY_KEY));
        assertThat(marshalled, hasKey("c"));
        assertThat(marshalled, not(hasKey("a")));
        assertThat(marshalled, not(hasKey("b")));

        assertThat(marshalled.get("c").b(), is(notNullValue()));
    }

    private static Map<String, AttributeValue> assertRoundTrip(final Object value, final MapMaid mapMaid) {
        return assertRoundTrip(value, value, mapMaid);
    }

    private static Map<String, AttributeValue> assertRoundTrip(final Object value, final Object expectedAfter, final MapMaid mapMaid) {
        final Map<String, AttributeValue> before = mapMaid.serializer()
                .marshalFromUniversalObject(value, CUSTOM_DYNAMO_DB);

        final Map<String, AttributeValue> loaded = storeAndLoadAgain(PRIMARY_KEY, before);
        final Object deserialized = mapMaid.deserializer()
                .deserializeToUniversalObject(loaded, CUSTOM_DYNAMO_DB);

        assertThat(deserialized, is(expectedAfter));
        return before;
    }
}
