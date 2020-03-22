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

package de.quantummaid.mapmaid.docs.examples.system.expectation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.quantummaid.mapmaid.docs.examples.system.Result;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.validation.NotNullValidator.ensureNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationSuccessfulExpectation implements Expectation {
    private final String expectedSerializationResult;

    public static Expectation serializationWas(final String expectedSerializationResult) {
        ensureNotNull(expectedSerializationResult, "expectedSerializationResult");
        return new SerializationSuccessfulExpectation(expectedSerializationResult);
    }

    @Override
    public void ensure(final Result result) {
        assertThat("serialization could be completed", result.serializationResult().isPresent(), is(true));
        final String serializationResult = result.serializationResult().get();
        assertThat(parseJson(serializationResult), is(parseJson(this.expectedSerializationResult)));
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
