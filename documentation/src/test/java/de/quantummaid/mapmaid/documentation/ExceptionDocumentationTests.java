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

package de.quantummaid.mapmaid.documentation;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Email;
import de.quantummaid.mapmaid.documentation.validation.CustomTypeValidationException;
import de.quantummaid.mapmaid.mapper.deserialization.validation.AggregatedValidationException;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationError;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ExceptionDocumentationTests {
    private static final Gson GSON = new Gson();

    private static final String JSON = "" +
            "{\n" +
            "  \"sender\": \"not-a-valid-sender-value\",\n" +
            "  \"receiver\": \"not-a-valid-receiver-value\"\n" +
            "}";

    @Test
    public void aggregateException() {
        //Showcase start aggregateException
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
        //Showcase end aggregateException
        String message = null;
        try {
            mapMaid.deserializeJson(JSON, Email.class);
        } catch (final AggregatedValidationException e) {
            message = e.getMessage();
            assertThat(message, is("deserialization encountered validation errors. Validation error at 'receiver', " +
                    "Invalid email address: 'not-a-valid-receiver-value'; " +
                    "Validation error at 'sender', Invalid email address: 'not-a-valid-sender-value'; "));
        }
        assertThat(message, notNullValue());
    }

    @Test
    public void mappedExample() {
        //Showcase start mappedException
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(GSON::toJson, input -> GSON.fromJson(input, Object.class)))
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class,
                        (exception, propertyPath) -> new ValidationError("This is a custom message we are reporting about " + exception.getMessage(), propertyPath))
                .build();
        //Showcase end mappedException

        String message = null;
        try {
            mapMaid.deserializeJson(JSON, Email.class);
        } catch (final AggregatedValidationException e) {
            message = e.getMessage();
            assertThat(message, is("deserialization encountered validation errors. Validation error at 'receiver', " +
                    "This is a custom message we are reporting about Invalid email address: 'not-a-valid-receiver-value'; " +
                    "Validation error at 'sender', This is a custom message we are reporting about Invalid email address: 'not-a-valid-sender-value'; "));
        }
        assertThat(message, notNullValue());
    }
}
