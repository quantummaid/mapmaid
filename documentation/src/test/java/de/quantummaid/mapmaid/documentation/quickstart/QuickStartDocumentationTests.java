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

package de.quantummaid.mapmaid.documentation.quickstart;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Body;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Email;
import de.quantummaid.mapmaid.documentation.quickstart.domain.EmailAddress;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Subject;
import org.junit.jupiter.api.Test;

public final class QuickStartDocumentationTests {

    @Test
    public void quickStart() {
        //Showcase start instance
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
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
