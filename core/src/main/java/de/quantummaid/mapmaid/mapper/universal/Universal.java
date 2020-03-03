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

package de.quantummaid.mapmaid.mapper.universal;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.universal.UniversalNull.universalNull;
import static java.lang.String.format;
import static java.util.Objects.isNull;

public interface Universal {

    static String describe(final Class<? extends Universal> type) {
        if (type == UniversalObject.class) {
            return "object";
        }
        if (type == UniversalCollection.class) {
            return "collection";
        }
        if (type == UniversalPrimitive.class) {
            return "string";
        }
        if (type == UniversalNull.class) {
            return "null";
        }
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    static Universal fromNativeJava(final Object object) {
        if (isNull(object)) {
            return universalNull();
        }
        if (object instanceof String) {
            return UniversalPrimitive.universalPrimitive(object);
        }
        if (object instanceof Double) {
            return UniversalPrimitive.universalPrimitive(object);
        }
        if(object instanceof Boolean) {
            return UniversalPrimitive.universalPrimitive(object);
        }
        if (object instanceof List) {
            return UniversalCollection.universalCollectionFromNativeList((List<Object>) object);
        }
        if (object instanceof Map) {
            return UniversalObject.universalObjectFromNativeMap((Map<String, Object>) object);
        }
        throw new UnsupportedOperationException(format("Object '%s' cannot be mapped to universal type", object));
    }

    Object toNativeJava();
}
