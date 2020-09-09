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

package de.quantummaid.mapmaid.dynamodb.util;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.dynamodb.util.localdynamodb.LocalDynamoDb;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.dynamodb.DynamoDbMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE;
import static de.quantummaid.mapmaid.dynamodb.DynamoDbMarshallerAndUnmarshaller.dynamoDbMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.util.FreePortPool.freePort;
import static de.quantummaid.mapmaid.dynamodb.util.localdynamodb.LocalDynamoDb.startLocalDynamoDb;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder;

public final class Asserters {
    private static final String TABLE_NAME = "tablename";
    private static final String PRIMARY_KEY = "id";
    private static final String VALUE_IDENTIFIER = "value";

    private Asserters() {
    }

    public static void assertRoundTrip(final Object value) {
        assertRoundTrip(value, value);
    }

    public static void assertRoundTrip(final Object value, final Object expectedAfter) {
        final MapMaid mapMaid = aMapMaid()
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(dynamoDbMarshallerAndUnmarshaller()))
                .build();
        final AttributeValue beforeRoundTripAttributeValue = mapMaid.serializer().marshalFromUniversalObject(value, DYNAMODB_ATTRIBUTEVALUE);
        final String id = UUID.randomUUID().toString();
        final AttributeValue afterRoundTripAttributeValue = testOnDynamoDbInstance(dynamoDbClient -> {
            final Map<String, AttributeValue> wrappedMap = Map.of(
                    PRIMARY_KEY, builder().s(id).build(),
                    VALUE_IDENTIFIER, beforeRoundTripAttributeValue
            );
            dynamoDbClient.putItem(builder -> builder
                    .tableName(TABLE_NAME)
                    .item(wrappedMap));

            final GetItemResponse itemResponse = dynamoDbClient.getItem(builder -> builder
                    .tableName(TABLE_NAME)
                    .key(Map.of(PRIMARY_KEY, builder().s(id).build())));

            return itemResponse.item().get(VALUE_IDENTIFIER);
        });
        assertThat(beforeRoundTripAttributeValue, is(afterRoundTripAttributeValue));

        final Object afterRoundTripValue = mapMaid.deserializer()
                .deserializeToUniversalObject(afterRoundTripAttributeValue, DYNAMODB_ATTRIBUTEVALUE);
        assertThat(afterRoundTripValue, is(expectedAfter));
    }

    public static AttributeValue testOnDynamoDbInstance(final Function<DynamoDbClient, AttributeValue> test) {
        final Properties originalProperties = System.getProperties();

        final Properties patchedProperties = new Properties(originalProperties);
        System.setProperty("aws.region", "egal");
        System.setProperty("aws.accessKeyId", "egal");
        System.setProperty("aws.secretAccessKey", "egal");
        System.setProperties(patchedProperties);

        try (LocalDynamoDb localDynamoDb = startLocalDynamoDb(freePort())) {
            localDynamoDb.createTable(TABLE_NAME, PRIMARY_KEY);
            final DynamoDbClient client = localDynamoDb.client();
            return test.apply(client);
        } finally {
            System.setProperties(originalProperties);
        }
    }

}
