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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.models.conventional.Body;
import de.quantummaid.mapmaid.builder.models.conventional.Email;
import de.quantummaid.mapmaid.builder.models.conventional.EmailAddress;
import de.quantummaid.mapmaid.builder.models.conventional.Subject;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("unchecked")
public final class XmlBuilderTest {

    public static final String EMAIL_XML = "<root>\n" +
            "  <entry>\n" +
            "    <string>receiver</string>\n" +
            "    <string>receiver@example.com</string>\n" +
            "  </entry>\n" +
            "  <entry>\n" +
            "    <string>body</string>\n" +
            "    <string>Hello World!!!</string>\n" +
            "  </entry>\n" +
            "  <entry>\n" +
            "    <string>sender</string>\n" +
            "    <string>sender@example.com</string>\n" +
            "  </entry>\n" +
            "  <entry>\n" +
            "    <string>subject</string>\n" +
            "    <string>Hello</string>\n" +
            "  </entry>\n" +
            "</root>";
    public static final Email EMAIL = Email.deserialize(
            EmailAddress.fromStringValue("sender@example.com"),
            EmailAddress.fromStringValue("receiver@example.com"),
            Subject.fromStringValue("Hello"),
            Body.fromStringValue("Hello World!!!")
    );

    public static MapMaid theXmlMapMaidInstance() {
        final XStream xStream = new XStream(new DomDriver());
        xStream.alias("root", Map.class);

        return MapMaid.aMapMaid("de.quantummaid.mapmaid.builder.models.conventional")
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(xStream::toXML, new Unmarshaller() {
                    @Override
                    public <T> T unmarshal(final String input, final Class<T> type) {
                        return (T) xStream.fromXML(input, type);
                    }
                }))
                .build();
    }

    @Test
    public void testEmailSerialization() {
        final String result = theXmlMapMaidInstance().serializeToJson(EMAIL);
        assertThat(result, is(EMAIL_XML));
    }

    @Test
    public void testEmailDeserialization() {
        final Email result = theXmlMapMaidInstance().deserializeJson(EMAIL_XML, Email.class);
        assertThat(result, is(EMAIL));
    }
}
