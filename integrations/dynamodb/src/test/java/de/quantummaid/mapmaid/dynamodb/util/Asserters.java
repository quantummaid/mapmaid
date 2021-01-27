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

import de.quantummaid.mapmaid.dynamodb.util.localdynamodb.LocalDynamoDb;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import static de.quantummaid.mapmaid.dynamodb.util.FreePortPool.freePort;
import static de.quantummaid.mapmaid.dynamodb.util.localdynamodb.LocalDynamoDb.startLocalDynamoDb;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class Asserters {
    private static final String TABLE_NAME = "tablename";

    private Asserters() {
    }

    public static Map<String, AttributeValue> storeAndLoadAgain(final String primaryKey, final Map<String, AttributeValue> map) {
        final Map<String, AttributeValue> after = testOnDynamoDbInstance(primaryKey, dynamoDbClient -> {
            dynamoDbClient.putItem(builder -> builder
                    .tableName(TABLE_NAME)
                    .item(map));

            final AttributeValue primaryKeyValue = map.get(primaryKey);
            final GetItemResponse itemResponse = dynamoDbClient.getItem(builder -> builder
                    .tableName(TABLE_NAME)
                    .key(Map.of(primaryKey, primaryKeyValue)));

            return itemResponse.item();
        });
        assertThat(map, is(after));
        return after;
    }

    public static <T> T testOnDynamoDbInstance(final String primaryKey,
                                               final Function<DynamoDbClient, T> test) {
        final Properties originalProperties = System.getProperties();

        final Properties patchedProperties = new Properties(originalProperties);
        System.setProperty("aws.region", "egal");
        System.setProperty("aws.accessKeyId", "egal");
        System.setProperty("aws.secretAccessKey", "egal");
        System.setProperties(patchedProperties);

        try (LocalDynamoDb localDynamoDb = startLocalDynamoDb(freePort())) {
            localDynamoDb.createTable(TABLE_NAME, primaryKey);
            final DynamoDbClient client = localDynamoDb.client();
            return test.apply(client);
        } finally {
            System.setProperties(originalProperties);
        }
    }
}
