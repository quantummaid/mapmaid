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

package de.quantummaid.mapmaid.docs;

import com.google.gson.Gson;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.models.conventional.Body;
import de.quantummaid.mapmaid.builder.models.conventional.Email;
import de.quantummaid.mapmaid.builder.models.conventional.EmailAddress;
import de.quantummaid.mapmaid.builder.models.conventional.Subject;
import org.junit.jupiter.api.Test;

public final class QuickStartExamples {
    private static final String YOUR_PACKAGE_TO_SCAN = Email.class.getPackageName();

    @Test
    public void quickStart() {
        //Showcase start instance
        final MapMaid mapMaid = MapMaid.aMapMaid(YOUR_PACKAGE_TO_SCAN)
                .usingJsonMarshaller(new Gson()::toJson, new Gson()::fromJson)
                .build();
        //Showcase end instance

        //Showcase start serialization
        final Email email = Email.deserialize(
                EmailAddress.fromStringValue("sender@example.com"),
                EmailAddress.fromStringValue("receiver@example.com"),
                Subject.fromStringValue("Hello"),
                Body.fromStringValue("Hello World!!!")
        );

        final String json = mapMaid.serializeToJson(email);
        //Showcase end serialization

        assert json.equals("{\"receiver\":\"receiver@example.com\",\"body\":\"Hello World!!!\",\"sender\":\"sender@example.com\",\"subject\":\"Hello\"}");

        //Showcase start deserialization
        final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
        //Showcase end deserialization

        assert deserializedEmail.equals(email);
    }
}
