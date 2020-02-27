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

package de.quantummaid.mapmaid.builder;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.models.excluded.Body;
import de.quantummaid.mapmaid.builder.models.excluded.Email;
import de.quantummaid.mapmaid.builder.models.excluded.EmailAddress;
import de.quantummaid.mapmaid.builder.models.excluded.Subject;
import de.quantummaid.mapmaid.builder.validation.CustomTypeValidationException;
import de.quantummaid.mapmaid.mapper.definitions.DefinitionNotFoundException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public final class ConventionalBuilderExclusionTest {

    public static final String EMAIL_JSON = "{" +
            "\"receiver\":\"receiver@example.com\"," +
            "\"body\":\"Hello World!!!\"," +
            "\"sender\":\"sender@example.com\"," +
            "\"subject\":\"Hello\"" +
            "}";
    public static final Email EMAIL = Email.deserialize(
            EmailAddress.fromStringValue("sender@example.com"),
            EmailAddress.fromStringValue("receiver@example.com"),
            Subject.fromStringValue("Hello"),
            Body.fromStringValue("Hello World!!!")
    );

    public static MapMaid theConventionalMapMaidInstance() {
        final Gson gson = new Gson();

        return MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(gson::toJson, gson::fromJson))
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
    }

    @Test
    public void testEmailSerialization() {
        Exception exception = null;
        try {
            theConventionalMapMaidInstance().serializer().serializeToJson(EMAIL);
        } catch (final DefinitionNotFoundException e) {
            exception = e;
        }
        assertThat(exception, instanceOf(DefinitionNotFoundException.class));
    }

    @Test
    public void testEmailDeserialization() {
        Exception exception = null;
        try {
            theConventionalMapMaidInstance().deserializer().deserializeJson(EMAIL_JSON, Email.class);
        } catch (final DefinitionNotFoundException e) {
            exception = e;
        }
        assertThat(exception, instanceOf(DefinitionNotFoundException.class));
    }
}
