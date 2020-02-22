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
import de.quantummaid.mapmaid.builder.models.conventional.Email;
import de.quantummaid.mapmaid.builder.models.customconvention.Body;
import de.quantummaid.mapmaid.builder.models.customconvention.EmailAddress;
import de.quantummaid.mapmaid.builder.models.customconvention.Subject;
import de.quantummaid.mapmaid.builder.validation.CustomTypeValidationException;
import de.quantummaid.mapmaid.mapper.deserialization.Deserializer;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.builder.recipes.manualregistry.ManualRegistry.manuallyRegisteredTypes;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.fixed.builder.serializedobject.SerializedObjectBuilder.serializedObjectOfType;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This test describes 2 ways of adding Custom Primitives and Serialized Objects individually to the MapMaid instance.
 * If you chose to follow the standards we have decided, which are described in README.md#default-conventions-explained
 * then you can use the method described in
 * {@link #theIndividuallyAddedTypesMapMaidConventional() theIndividuallyAddedTypesMapMaidConventional}
 * and simply register your class. The methods will then be auto-discovered for you.
 * <p>
 * If, however, you chose to change those, like shown in the package
 * {@code de.quantummaid.mapmaid.builder.models.customconvention}
 * take a look at {@link #theIndividuallyAddedTypesMapMaid() theIndividuallyAddedTypesMapMaid} where you can see
 * how you can add Custom Primitives and Serialized Objects, together with their serialization and deserialization
 * methods.
 */
public final class IndividuallyAddedModelsBuilderTest {
    public static final String EMAIL_JSON = "{" +
            "\"receiver\":\"receiver@example.com\"," +
            "\"body\":\"Hello World!!!\"," +
            "\"sender\":\"sender@example.com\"," +
            "\"subject\":\"Hello\"" +
            "}";

    public static final Email CONVENTIONAL_EMAIL =
            Email.deserialize(
                    de.quantummaid.mapmaid.builder.models.conventional.EmailAddress.fromStringValue("sender@example.com"),
                    de.quantummaid.mapmaid.builder.models.conventional.EmailAddress.fromStringValue("receiver@example.com"),
                    de.quantummaid.mapmaid.builder.models.conventional.Subject.fromStringValue("Hello"),
                    de.quantummaid.mapmaid.builder.models.conventional.Body.fromStringValue("Hello World!!!")
            );

    public static final de.quantummaid.mapmaid.builder.models.customconvention.Email EMAIL =
            de.quantummaid.mapmaid.builder.models.customconvention.Email.restore(
                    EmailAddress.deserialize("sender@example.com"),
                    EmailAddress.deserialize("receiver@example.com"),
                    Subject.deserialize("Hello"),
                    Body.deserialize("Hello World!!!")
            );

    private static final Gson GSON = new Gson();

    public static MapMaid theIndividuallyAddedTypesMapMaidConventional() {
        return MapMaid.aMapMaid()
                .usingRecipe(manuallyRegisteredTypes()
                        .withSerializedObjects(
                                Email.class
                        )
                        .withCustomPrimitives(
                                de.quantummaid.mapmaid.builder.models.conventional.EmailAddress.class,
                                de.quantummaid.mapmaid.builder.models.conventional.Subject.class,
                                de.quantummaid.mapmaid.builder.models.conventional.Body.class)
                )
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(GSON::toJson, GSON::fromJson))
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
    }

    public static MapMaid theIndividuallyAddedTypesMapMaid() {
        final Class<Body>
                customConventionBody = Body.class;
        return MapMaid.aMapMaid()
                .withManuallyAddedDefinition(
                        serializedObjectOfType(de.quantummaid.mapmaid.builder.models.customconvention.Email.class)
                                // TODO
                                .withField("sender", EmailAddress.class, object -> object.sender)
                                .withField("receiver", EmailAddress.class, object -> object.receiver)
                                .withField("subject", Subject.class, object -> object.subject)
                                .withField("body", Body.class, object -> object.body)
                                .deserializedUsing(de.quantummaid.mapmaid.builder.models.customconvention.Email::restore)
                                .create()
                )

                .usingRecipe(manuallyRegisteredTypes()
                        /*
                        TODO
                        .withSerializedObject(de.quantummaid.mapmaid.builder.models.customconvention.Email.class,
                                de.quantummaid.mapmaid.builder.models.customconvention.Email.class.getFields(),
                                "restore")
                         */
                        .withCustomPrimitive(EmailAddress.class, EmailAddress::serialize, EmailAddress::deserialize)
                        .withCustomPrimitive(Subject.class, Subject::serialize, Subject::deserialize)
                        .withCustomPrimitive(customConventionBody, Body::serialize, Body::deserialize)
                )
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(GSON::toJson, GSON::fromJson))
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
    }

    public static MapMaid theIndividuallyAddedTypesMapMaid1() {
        final Gson gson = new Gson();

        throw new UnsupportedOperationException();
        /*
        TODO
        return MapMaid.aMapMaid()
                .withDetector(conventionalDetector(
                        "serialize", "" +
                                "deserialize",
                        "restore",
                        ".*")
                )
                .usingRecipe(manuallyRegisteredTypes()
                        .withSerializedObjects(de.quantummaid.mapmaid.builder.models.customconvention.Email.class)
                        .withCustomPrimitives(EmailAddress.class, Subject.class, Body.class)
                )
                .usingJsonMarshaller(gson::toJson, gson::fromJson)
                .withExceptionIndicatingValidationError(CustomTypeValidationException.class)
                .build();
         */
    }

    @Test
    public void testEmailSerializationConventional() {
        final String result = theIndividuallyAddedTypesMapMaidConventional()
                .serializer()
                .serializeToJson(CONVENTIONAL_EMAIL);
        assertThat(result, is(EMAIL_JSON));
    }

    @Test
    public void testEmailDeserializationConventional() {
        final Email result = theIndividuallyAddedTypesMapMaidConventional()
                .deserializer()
                .deserializeJson(EMAIL_JSON, Email.class);
        assertThat(result, is(CONVENTIONAL_EMAIL));
    }

    @Test
    public void testEmailSerialization() {
        final String result = theIndividuallyAddedTypesMapMaid().serializer().serializeToJson(EMAIL);
        assertThat(result, is(EMAIL_JSON));
    }

    @Test
    public void testEmailDeserialization() {
        final Deserializer deserializer = theIndividuallyAddedTypesMapMaid().deserializer();
        final de.quantummaid.mapmaid.builder.models.customconvention.Email result = deserializer.deserializeJson(EMAIL_JSON, de.quantummaid.mapmaid.builder.models.customconvention.Email.class);
        assertThat(result, is(EMAIL));
    }

    @Test
    public void testEmailSerialization1() {
        final String result = theIndividuallyAddedTypesMapMaid1().serializer().serializeToJson(EMAIL);
        assertThat(result, is(EMAIL_JSON));
    }

    @Test
    public void testEmailDeserialization1() {
        final Deserializer deserializer = theIndividuallyAddedTypesMapMaid1().deserializer();
        final de.quantummaid.mapmaid.builder.models.customconvention.Email result = deserializer.deserializeJson(EMAIL_JSON, de.quantummaid.mapmaid.builder.models.customconvention.Email.class);
        assertThat(result, is(EMAIL));
    }
}
