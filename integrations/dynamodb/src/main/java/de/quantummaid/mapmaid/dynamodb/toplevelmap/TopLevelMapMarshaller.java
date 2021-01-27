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

package de.quantummaid.mapmaid.dynamodb.toplevelmap;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TopLevelMapMarshaller<T> implements Marshaller<Map<String, T>> {
    private final Marshaller<T> lowerMarshaller;

    public static <T> TopLevelMapMarshaller<T> topLevelMapMarshaller(final Marshaller<T> lowerMarshaller) {
        return new TopLevelMapMarshaller<>(lowerMarshaller);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, T> marshal(final Object object) throws Exception {
        if (!(object instanceof Map)) {
            throw mapMaidException("can only marshall a Map but got '" + object.getClass() + "'");
        }
        final Map<String, Object> map = (Map<String, Object>) object;
        final Map<String, T> result = new LinkedHashMap<>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            final T marshalledValue = lowerMarshaller.marshal(value);
            result.put(key, marshalledValue);
        }
        return result;
    }
}
