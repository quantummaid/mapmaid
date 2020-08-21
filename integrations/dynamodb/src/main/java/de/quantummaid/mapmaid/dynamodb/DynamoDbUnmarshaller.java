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

import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DynamoDbUnmarshaller implements Unmarshaller<AttributeValue> {

    public static DynamoDbUnmarshaller dynamoDbUnmarshaller() {
        return new DynamoDbUnmarshaller();
    }

    @Override
    public Object unmarshal(final AttributeValue attributeValue) {
        if (attributeValue.hasM()) {
            final Map<String, AttributeValue> map = attributeValue.m();
            return unmarshalMap(map);
        }
        final Boolean nul = attributeValue.nul();
        if (nul != null && nul) {
            return null;
        }

        final Boolean bool = attributeValue.bool();
        if (bool != null) {
            return bool;
        }

        final String number = attributeValue.n();
        if (number != null) {
            return parseLong(number);
        }

        final String string = attributeValue.s();
        if (string != null) {
            return string;
        }

        if (attributeValue.hasL() && attributeValue.l() != null) {
            return attributeValue.l().stream()
                    .map(this::unmarshal)
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException(format("unable to unmarshal from: %s", attributeValue));
    }

    public Map<String, Object> unmarshalMap(final Map<String, AttributeValue> input) {
        final Map<String, Object> result = new HashMap<>(input.size());
        input.keySet().forEach(key -> {
            final AttributeValue attributeValue = input.get(key);
            final Object value = unmarshal(attributeValue);
            result.put(key, value);
        });
        return result;
    }
}
