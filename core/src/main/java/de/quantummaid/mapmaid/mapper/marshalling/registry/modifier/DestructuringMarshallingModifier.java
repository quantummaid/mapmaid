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

package de.quantummaid.mapmaid.mapper.marshalling.registry.modifier;

import java.util.List;
import java.util.Map;

public interface DestructuringMarshallingModifier extends MarshallingModifier {

    Object modifyMap(Map<String, Object> input);

    Object modifyList(List<Object> input);

    Object modifyScalar(Object input);

    @SuppressWarnings("unchecked")
    @Override
    default Object modify(final Object input) {
        if (input instanceof Map) {
            return modifyMap((Map<String, Object>) input);
        }
        if (input instanceof List) {
            return modifyList((List<Object>) input);
        }
        return modifyScalar(input);
    }
}
