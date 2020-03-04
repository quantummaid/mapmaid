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

package de.quantummaid.mapmaid.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.models.conventional.Body;
import de.quantummaid.mapmaid.builder.models.conventional.Email;
import de.quantummaid.mapmaid.builder.models.conventional.EmailAddress;
import de.quantummaid.mapmaid.builder.models.conventional.Subject;
import de.quantummaid.mapmaid.builder.validation.CustomTypeValidationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class YamlBuilderTest {

    public static final String EMAIL_YAML = "---\n" +
            "receiver: \"receiver@example.com\"\n" +
            "body: \"Hello World!!!\"\n" +
            "sender: \"sender@example.com\"\n" +
            "subject: \"Hello\"\n";
    public static final Email EMAIL = Email.deserialize(
            EmailAddress.fromStringValue("sender@example.com"),
            EmailAddress.fromStringValue("receiver@example.com"),
            Subject.fromStringValue("Hello"),
            Body.fromStringValue("Hello World!!!")
    );

    public static MapMaid theYamlMapMaidInstance() {
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        return MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(objectMapper::writeValueAsString, objectMapper::readValue))
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
    }

    @Test
    public void testEmailSerialization() {
        final String result = theYamlMapMaidInstance().serializeToJson(EMAIL);
        assertThat(result, is(EMAIL_YAML));
    }

    @Test
    public void testEmailDeserialization() {
        final Email result = theYamlMapMaidInstance().deserializeJson(EMAIL_YAML, Email.class);
        assertThat(result, is(EMAIL));
    }
}
