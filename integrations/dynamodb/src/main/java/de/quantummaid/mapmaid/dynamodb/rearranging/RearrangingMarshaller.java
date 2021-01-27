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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RearrangingMarshaller implements Marshaller<Object> {
    private final List<Rearranger> rearrangers;

    public static RearrangingMarshaller rearrangingMarshaller(final List<Rearranger> rearrangers) {
        return new RearrangingMarshaller(rearrangers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> marshal(final Object object) throws Exception {
        if (!(object instanceof Map)) {
            throw mapMaidException("can only marshall a Map but got '" + object.getClass() + "'");
        }
        final Map<String, Object> map = (Map<String, Object>) object;
        final Map<String, Object> mutableMap = new LinkedHashMap<>(map);
        for (final Rearranger rearranger : rearrangers) {
            rearranger.rearrange(mutableMap);
        }
        return mutableMap;
    }
}
