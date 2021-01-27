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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Combination implements Rearranger {
    private final List<String> sourceKeys;
    private final String targetKey;

    public static Combination combine(final List<String> sourceKeys,
                                      final String targetKey) {
        return new Combination(sourceKeys, targetKey);
    }

    @Override
    public void rearrange(final Map<String, Object> mutableMap) {
        final Map<String, Object> combinationMap = new LinkedHashMap<>();
        sourceKeys.stream()
                .filter(mutableMap::containsKey)
                .forEach(key -> {
                    final Object value = mutableMap.remove(key);
                    combinationMap.put(key, value);
                });
        mutableMap.put(targetKey, combinationMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reverse(final Map<String, Object> mutableMap) {
        final Map<String, Object> combinationMap = (Map<String, Object>) mutableMap.remove(targetKey);
        sourceKeys.stream()
                .filter(combinationMap::containsKey)
                .forEach(key -> {
                    final Object value = combinationMap.get(key);
                    mutableMap.put(key, value);
                });
    }
}
