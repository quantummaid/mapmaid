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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Body;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Email;
import de.quantummaid.mapmaid.documentation.quickstart.domain.EmailAddress;
import de.quantummaid.mapmaid.documentation.quickstart.domain.Subject;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class UsageDocumentationTests {

    private static final Email EMAIL = Email.deserialize(
            EmailAddress.fromStringValue("sender@example.com"),
            EmailAddress.fromStringValue("receiver@example.com"),
            Subject.fromStringValue("Hello"),
            Body.fromStringValue("Hello World!!!")
    );

    @Test
    public void usageWithJson() {
        //Showcase start jsonInstance
        final Gson gson = new Gson();
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(gson::toJson, input -> gson.fromJson(input, Object.class)))
                .build();
        //Showcase end jsonInstance

        //Showcase start serializeToJson
        final String json = mapMaid.serializeToJson(EMAIL);
        //Showcase end serializeToJson
        assertThat(json, is("{\"receiver\":\"receiver@example.com\",\"body\":\"Hello World!!!\",\"sender\":\"sender@example.com\",\"subject\":\"Hello\"}"));

        //Showcase start deserializeJson
        final Email deserializedEmail = mapMaid.deserializeJson(json, Email.class);
        //Showcase end deserializeJson
        assertThat(deserializedEmail, is(EMAIL));
    }

    @Test
    public void usageWithYaml() {
        //Showcase start yamlInstance
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingYamlMarshaller(objectMapper::writeValueAsString, input -> objectMapper.readValue(input, Object.class)))
                .build();
        //Showcase end yamlInstance

        //Showcase start serializeToYaml
        final String yaml = mapMaid.serializeToYaml(EMAIL);
        System.out.println(yaml);
        //Showcase end serializeToYaml
        assertThat(yaml, is("---\n" +
                "receiver: \"receiver@example.com\"\n" +
                "body: \"Hello World!!!\"\n" +
                "sender: \"sender@example.com\"\n" +
                "subject: \"Hello\"\n"));

        //Showcase start deserializeYaml
        final Email deserializedEmail = mapMaid.deserializeYaml(yaml, Email.class);
        //Showcase end deserializeYaml
        assertThat(deserializedEmail, is(EMAIL));
    }

    @Test
    public void usageWithXml() {
        //Showcase start xmlInstance
        final XStream xStream = new XStream(new DomDriver());
        xStream.alias("root", Map.class);
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder
                        .usingXmlMarshaller(xStream::toXML, xStream::fromXML))
                .build();
        //Showcase end xmlInstance

        //Showcase start serializeToXml
        final String xml = mapMaid.serializeToXml(EMAIL);
        //Showcase end serializeToXml
        assertThat(xml, is("" +
                "<root>\n" +
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
                "</root>"));

        //Showcase start deserializeXml
        final Email deserializedEmail = mapMaid.deserializeXml(xml, Email.class);
        //Showcase end deserializeXml
        assertThat(deserializedEmail, is(EMAIL));
    }

    @Test
    public void usageWithCustomFormat() {
        //Showcase start example1
        final Gson gson = new Gson();
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(Email.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder
                        .usingMarshaller(
                                MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"),
                                gson::toJson,
                                input -> {
                                    return gson.fromJson(input, Object.class);
                                }))
                .build();
        //Showcase end example1

        //Showcase start serializeToCustomFormat
        final String customFormat = mapMaid.serializeTo(EMAIL, MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"));
        //Showcase end serializeToCustomFormat
        assertThat(customFormat, is("{\"receiver\":\"receiver@example.com\",\"body\":\"Hello World!!!\",\"sender\":\"sender@example.com\",\"subject\":\"Hello\"}"));

        //Showcase start deserializeCustomFormat
        final Email deserializedEmail = mapMaid.deserialize(customFormat, Email.class, MarshallingType.marshallingType("YOUR_CUSTOM_FORMAT"));
        //Showcase end deserializeCustomFormat
        assertThat(deserializedEmail, is(EMAIL));
    }

    @Test
    public void showBuilder() {
        //Showcase start withoutPackageScanning
        MapMaid.aMapMaid()
                /* further configuration */
                .build();
        //Showcase end withoutPackageScanning
    }
}
