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

package de.quantummaid.mapmaid.dynamodb.attributevalue;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttributeValueMarshaller implements Marshaller<AttributeValue> {

    public static AttributeValueMarshaller attributeValueMarshaller() {
        return new AttributeValueMarshaller();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AttributeValue marshal(final Object input) {
        if (input == null) {
            return marshalNull();
        }
        if (input instanceof String) {
            return marshalString((String) input);
        }
        if (input instanceof Map) {
            return marshalMap((Map<String, ?>) input);
        }
        if (input instanceof List) {
            return marshalList((List<?>) input);
        }
        if (input instanceof Boolean) {
            return marshalBoolean((Boolean) input);
        }
        if (input instanceof Long) {
            return marshalLong((Long) input);
        }
        if (input instanceof Integer) {
            return marshalInteger((Integer) input);
        }
        if (input instanceof byte[]) {
            return marshalByteArray((byte[]) input);
        }
        throw new UnsupportedOperationException("unable to marshal object of type: " + input.getClass().getSimpleName());
    }

    public Map<String, AttributeValue> marshalTopLevelMap(final Map<String, ?> map) {
        final Map<String, AttributeValue> attributeValueMap = new HashMap<>(map.size());
        map.forEach((key, value) -> {
            final AttributeValue marshalledValue = marshal(value);
            attributeValueMap.put(key, marshalledValue);
        });
        return attributeValueMap;
    }

    private AttributeValue marshalString(final String string) {
        return AttributeValue.builder()
                .s(string)
                .build();
    }

    private AttributeValue marshalMap(final Map<String, ?> map) {
        final Map<String, AttributeValue> attributeValueMap = marshalTopLevelMap(map);
        return AttributeValue.builder()
                .m(attributeValueMap)
                .build();
    }

    private AttributeValue marshalList(final List<?> list) {
        final List<AttributeValue> attributeValues = list.stream()
                .map(this::marshal)
                .collect(toList());
        return AttributeValue.builder()
                .l(attributeValues)
                .build();
    }

    private AttributeValue marshalNull() {
        return AttributeValue.builder()
                .nul(true)
                .build();
    }

    private AttributeValue marshalBoolean(final Boolean b) {
        return AttributeValue.builder()
                .bool(b)
                .build();
    }

    private AttributeValue marshalInteger(final Integer i) {
        final Long l = Long.valueOf(i);
        return marshalLong(l);
    }

    private AttributeValue marshalLong(final Long l) {
        return AttributeValue.builder()
                .n(l.toString())
                .build();
    }

    private AttributeValue marshalByteArray(final byte[] bytes) {
        final SdkBytes sdkBytes = SdkBytes.fromByteArray(bytes);
        return AttributeValue.builder()
                .b(sdkBytes)
                .build();
    }
}
