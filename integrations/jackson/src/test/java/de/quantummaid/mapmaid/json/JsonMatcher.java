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

package de.quantummaid.mapmaid.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

@ToString
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonMatcher extends TypeSafeMatcher<String> {
    private final String expected;

    public static TypeSafeMatcher<String> isJson(final String expected) {
        return new JsonMatcher(expected);
    }

    @Override
    protected boolean matchesSafely(final String actual) {
        final JsonNode actualJson = parseJson(actual);
        final JsonNode expectedJson = parseJson(this.expected);
        return actualJson.equals(expectedJson);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(this.expected);
    }

    private static JsonNode parseJson(final String json) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
