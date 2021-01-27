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

import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TopLevelMapUnmarshaller<T> implements Unmarshaller<Map<String, T>> {
    private final Unmarshaller<T> lowerUnmarshaller;

    public static <T> TopLevelMapUnmarshaller<T> topLevelMapUnmarshaller(final Unmarshaller<T> lowerUnmarshaller) {
        return new TopLevelMapUnmarshaller<>(lowerUnmarshaller);
    }

    @Override
    public Object unmarshal(final Map<String, T> input) throws Exception {
        final Map<String, Object> result = new LinkedHashMap<>();
        for (final Map.Entry<String, T> entry : input.entrySet()) {
            final String key = entry.getKey();
            final T value = entry.getValue();
            final Object unmarshalledValue = lowerUnmarshaller.unmarshal(value);
            result.put(key, unmarshalledValue);
        }
        return result;
    }
}
