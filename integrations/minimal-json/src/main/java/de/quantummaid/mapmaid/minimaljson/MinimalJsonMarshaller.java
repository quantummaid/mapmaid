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

import com.eclipsesource.json.*;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinimalJsonMarshaller implements Marshaller {

    public static MinimalJsonMarshaller minimalJsonMarshaller() {
        return new MinimalJsonMarshaller();
    }

    @Override
    public String marshal(final Object object) {
        final JsonValue jsonValue = marshallRec(object);
        return jsonValue.toString(WriterConfig.MINIMAL);
    }

    private JsonValue marshallRec(final Object object) {
        if (object instanceof String) {
            return Json.value((String) object);
        } else if (object instanceof Map) {
            final JsonObject jsonObject = Json.object();
            @SuppressWarnings("unchecked") final Map<String, ?> inputMap = (Map<String, ?>) object;
            inputMap.forEach((k, v) -> {
                final JsonValue jsonValue = marshallRec(v);
                jsonObject.add(k, jsonValue);
            });
            return jsonObject;
        } else if (object instanceof List) {
            final JsonArray arr = Json.array();
            final List<?> inputList = (List) object;
            inputList.stream()
                    .map(this::marshallRec)
                    .forEach(arr::add);
            return arr;
        } else if (object instanceof Double) {
            return Json.value((Double) object);
        } else if (object instanceof Long) {
            return Json.value((Long) object);
        } else if (object instanceof Boolean) {
            return Json.value((Boolean) object);
        } else if (Objects.isNull(object)) {
            return Json.value(null);
        } else {
            throw new IllegalArgumentException(String.format(
                    "unable to marshall object '%s' of type '%s' to json", object, object.getClass()));
        }
    }
}
