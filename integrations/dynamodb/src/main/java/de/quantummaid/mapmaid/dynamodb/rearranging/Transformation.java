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

package de.quantummaid.mapmaid.dynamodb.rearranging;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transformation<T> implements Rearranger {
    private final String key;
    private final Marshaller<T> transformingMarshaller;
    private final Unmarshaller<T> reversingMarshaller;

    public static <T> Transformation<T> transform(final String key,
                                                  final Marshaller<T> transformatingMarshaller,
                                                  final Unmarshaller<T> reversingMarshaller) {
        return new Transformation<>(key, transformatingMarshaller, reversingMarshaller);
    }

    @Override
    public void rearrange(final Map<String, Object> mutableMap) throws Exception {
        final Object value = mutableMap.remove(this.key);
        if (value == null) {
            return;
        }
        final T marshalled = transformingMarshaller.marshal(value);
        mutableMap.put(key, marshalled);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reverse(final Map<String, Object> mutableMap) throws Exception {
        final T value = (T) mutableMap.remove(this.key);
        if (value == null) {
            return;
        }
        final Object unmarshalled = reversingMarshaller.unmarshal(value);
        mutableMap.put(key, unmarshalled);
    }
}
