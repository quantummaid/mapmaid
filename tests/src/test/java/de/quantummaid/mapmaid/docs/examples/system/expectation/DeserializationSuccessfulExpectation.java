/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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
public final class DeserializationSuccessfulExpectation implements Expectation {
    private final Object expectedDeserializationResult;

    public static Expectation deserializationWas(final Object expectedDeserializationResult) {
        ensureNotNull(expectedDeserializationResult, "expectedDeserializationResult");
        return new DeserializationSuccessfulExpectation(expectedDeserializationResult);
    }

    @Override
    public void ensure(final Result result) {
        assertThat("deserialization did provide a result", result.deserializationResult().isPresent(), is(true));
        final Object deserializationResult = result.deserializationResult().get();
        assertThat(deserializationResult, is(this.expectedDeserializationResult));
    }
}
