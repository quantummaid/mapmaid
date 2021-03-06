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

package de.quantummaid.mapmaid.builder.recipes.urlencoded;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlEncodedMarshaller implements Marshaller<String> {

    public static UrlEncodedMarshaller urlEncodedMarshaller() {
        return new UrlEncodedMarshaller();
    }

    @Override
    public String marshal(final Object object) {
        NotNullValidator.validateNotNull(object, "object");
        final List<KeyValue> parts = new ArrayList<>(10);
        marshal(Key.emptyKey(), parts, object);
        return parts.stream()
                .map(KeyValue::render)
                .collect(joining("&"));
    }

    @SuppressWarnings("unchecked")
    private void marshal(final Key keyPrefix,
                         final List<KeyValue> parts,
                         final Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof Map) {
            marshalMap(keyPrefix, parts, (Map<String, Object>) object);
        } else if (object instanceof List) {
            marshalList(keyPrefix, parts, (List<Object>) object);
        } else if (object.getClass().isArray()) {
            final List<Object> list = asList((Object[]) object);
            marshalList(keyPrefix, parts, list);
        } else if (object instanceof String) {
            marshalString(keyPrefix, parts, (String) object);
        } else if (object instanceof Long) {
            marshalLong(keyPrefix, parts, (Long) object);
        } else {
            throw new IllegalArgumentException(
                    format("Unable to marshal for url-encoded because the type of '%s' is not supported", object));
        }
    }

    private void marshalMap(final Key keyPrefix,
                            final List<KeyValue> parts,
                            final Map<String, Object> map) {
        map.keySet().forEach(key -> {
            final Key childKey = keyPrefix.child(key);
            marshal(childKey, parts, map.get(key));
        });
    }

    private void marshalList(final Key keyPrefix,
                             final List<KeyValue> parts,
                             final List<Object> list) {
        for (int i = 0; i < list.size(); ++i) {
            final Key childKey = keyPrefix.child(valueOf(i));
            final Object object = list.get(i);
            marshal(childKey, parts, object);
        }
    }

    private static void marshalString(final Key key,
                                      final List<KeyValue> parts,
                                      final String string) {
        parts.add(KeyValue.keyValue(key, string));
    }

    private static void marshalLong(final Key key,
                                    final List<KeyValue> parts,
                                    final long l) {
        parts.add(KeyValue.keyValue(key, Long.toString(l)));
    }
}
