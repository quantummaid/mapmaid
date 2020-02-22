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

package de.quantummaid.mapmaid.builder.resolving.hints.example;

import de.quantummaid.mapmaid.MapMaid;

import java.util.Map;

// TODO
public final class Example {

    public static void main(String[] args) {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .mapping(MyType.class)
                .build();

        final Map<String, Object> stringObjectMap = mapMaid.serializer().serializeToMap(MyType.myTypeFactory1("value1", "value2"));
        System.out.println("stringObjectMap = " + stringObjectMap);

        final MyType result = mapMaid.deserializer().deserializeFromMap(Map.of(
                "value1", "value1",
                "value2", "value2"), MyType.class);
        System.out.println("result = " + result);
    }
}
