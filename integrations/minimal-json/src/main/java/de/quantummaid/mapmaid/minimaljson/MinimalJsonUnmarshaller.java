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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MinimalJsonUnmarshaller implements Unmarshaller {

    public static MinimalJsonUnmarshaller minimalJsonUnmarshaller() {
        return new MinimalJsonUnmarshaller();
    }

    @Override
    public Object unmarshal(final String input) {
        final JsonValue json = Json.parse(input);
        return unmarshallRec(json);
    }

    private Object unmarshallRec(final JsonValue json) {
        if (json.isString()) {
            return json.asString();
        } else if (json.isObject()) {
            final JsonObject members = json.asObject();
            return members.names().stream()
                    .collect(Collectors.toMap(name -> name, name -> {
                        final JsonValue jsonValue = members.get(name);
                        return unmarshallRec(jsonValue);
                    }));
        } else if (json.isArray()) {
            return json.asArray().values().stream()
                    .map(this::unmarshallRec)
                    .collect(Collectors.toList());
        } else if (json.isNumber()) {
            return unmarshalNumber(json);
        } else if (json.isBoolean()) {
            return json.asBoolean();
        } else if (json.isNull()) {
            return unmarshalNull();
        } else {
            throw new IllegalArgumentException("json value of an unknown type: " +
                    json.toString(WriterConfig.PRETTY_PRINT));
        }
    }

    private Object unmarshalNumber(JsonValue json) {
        BigDecimal number = new BigDecimal(json.toString());
        if (asIntegerValue(number)) {
            return number.longValue();
        } else {
            return number.doubleValue();
        }
    }

    private Object unmarshalNull() {
        return null;
    }

    private boolean asIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }
}
