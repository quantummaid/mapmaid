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

package de.quantummaid.mapmaid.minimaljson;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.reflectmaid.GenericType;
import de.quantummaid.reflectmaid.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.reflectmaid.GenericType.genericType;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AutoloadingSpecs {

    @Test
    public void minimalJsonMarshallerIsAutoloadable() {
        final GenericType<List<String>> type = genericType(new TypeToken<>() {
        });
        final MapMaid mapMaid = aMapMaid()
                .serializingAndDeserializing(type)
                .build();
        final String json = mapMaid.serializeToJson(List.of("a", "b", "c"), type);
        assertEquals("[\"a\",\"b\",\"c\"]", json);
        final List<String> deserialized = mapMaid.deserializeJson(json, type);
        assertEquals(List.of("a", "b", "c"), deserialized);
    }
}
