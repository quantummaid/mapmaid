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

package de.quantummaid.mapmaid.documentation.marshalling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe;
import de.quantummaid.mapmaid.documentation.marshalling.domain.*;
import de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshallerAndUnmarshaller;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe.urlEncoded;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.UNIVERSAL_OBJECT;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public final class MarshallingDocumentationTests {

    @SuppressWarnings("unchecked")
    @Test
    public void urlEncodedConfig() {
        //Showcase start urlencoded
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                .build();
        //Showcase end urlencoded
        assertThat(mapMaid.deserializer().supportedMarshallingTypes(), containsInAnyOrder(urlEncoded(), UNIVERSAL_OBJECT));
        assertThat(mapMaid.serializer().supportedMarshallingTypes(), containsInAnyOrder(urlEncoded(), UNIVERSAL_OBJECT));
    }

    @Test
    public void urlEncodedExample() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .usingRecipe(UrlEncodedMarshallerRecipe.urlEncodedMarshaller())
                .build();

        final ComplexPerson object = ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                )));

        //Showcase start urlencodedusage
        final String urlEncoded = mapMaid.serializeTo(object, urlEncoded());
        //Showcase end urlencodedusage

        assertThat(urlEncoded, is("addresses[0][houseNumber]=7a&addresses[0][zipCode]=423423&addresses[0][country]=USA&" +
                "addresses[0][streetName]=Nulla+Street&addresses[0][region]=Mississippi&addresses[0][city]=Mankato&" +
                "firstNames[0]=Aaron&firstNames[1]=Adam"));
    }

    @Test
    public void attributeValue() {
        //Showcase start attributeValue
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(AttributeValueMarshallerAndUnmarshaller.attributeValueMarshallerAndUnmarshaller()))
                .build();
        //Showcase end attributeValue

        final ComplexPerson object = ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                )));

        //Showcase start attributeValueSerialization
        final AttributeValue attributeValue = mapMaid.serializeTo(object, AttributeValueMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE);
        //Showcase end attributeValueSerialization

        //Showcase start attributeValueDeserialization
        final ComplexPerson deserialized = mapMaid.deserialize(attributeValue, ComplexPerson.class, AttributeValueMarshallerAndUnmarshaller.DYNAMODB_ATTRIBUTEVALUE);
        //Showcase end attributeValueDeserialization

        assertThat(deserialized.firstNames, is(object.firstNames));
    }

    @Test
    public void jsonWithGsonExample() {
        //Showcase start jsonWithGson
        final Gson gson = new Gson(); // can be further configured depending on your needs.
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(gson::toJson, input -> gson.fromJson(input, Object.class)))
                .build();
        //Showcase end jsonWithGson

        final String json = mapMaid.serializeToJson(ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                ))));
        assertThat(json, is("{\"addresses\":[{\"houseNumber\":\"7a\",\"zipCode\":\"423423\",\"country\":\"USA\"," +
                "\"streetName\":\"Nulla Street\",\"region\":\"Mississippi\",\"city\":\"Mankato\"}],\"firstNames\":[\"Aaron\",\"Adam\"]}"));
    }

    @Test
    public void jsonWithObjectMapperExample() {
        //Showcase start jsonWithObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper();
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(objectMapper::writeValueAsString, input -> objectMapper.readValue(input, Object.class)))
                .build();
        //Showcase end jsonWithObjectMapper

        final String json = mapMaid.serializeToJson(ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                ))));
        assertThat(json, is("{\"addresses\":[{\"houseNumber\":\"7a\",\"zipCode\":\"423423\",\"country\":\"USA\"," +
                "\"streetName\":\"Nulla Street\",\"region\":\"Mississippi\",\"city\":\"Mankato\"}],\"firstNames\":[\"Aaron\",\"Adam\"]}"));
    }

    @Test
    public void xmlWithXStream() {
        //Showcase start xmlWithXStream
        final XStream xStream = new XStream(new DomDriver());
        xStream.alias("root", Map.class);

        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingXmlMarshaller(xStream::toXML, xStream::fromXML))
                .build();
        //Showcase end xmlWithXStream

        final String xml = mapMaid.serializeToXml(ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                ))));
        assertThat(xml, is("" +
                "<root>\n" +
                "  <entry>\n" +
                "    <string>addresses</string>\n" +
                "    <list>\n" +
                "      <root>\n" +
                "        <entry>\n" +
                "          <string>houseNumber</string>\n" +
                "          <string>7a</string>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>zipCode</string>\n" +
                "          <string>423423</string>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>country</string>\n" +
                "          <string>USA</string>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>streetName</string>\n" +
                "          <string>Nulla Street</string>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>region</string>\n" +
                "          <string>Mississippi</string>\n" +
                "        </entry>\n" +
                "        <entry>\n" +
                "          <string>city</string>\n" +
                "          <string>Mankato</string>\n" +
                "        </entry>\n" +
                "      </root>\n" +
                "    </list>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>firstNames</string>\n" +
                "    <list>\n" +
                "      <string>Aaron</string>\n" +
                "      <string>Adam</string>\n" +
                "    </list>\n" +
                "  </entry>\n" +
                "</root>"));
    }

    @Test
    public void yamlWithObjectMapper() {
        //Showcase start yamlWithObjectMapper
        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializingAndDeserializing(ComplexPerson.class)
                .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingYamlMarshaller(objectMapper::writeValueAsString, input -> {
                    return objectMapper.readValue(input, Object.class);
                }))
                .build();
        //Showcase end yamlWithObjectMapper

        final String yaml = mapMaid.serializeToYaml(ComplexPerson.deserialize(
                asList(FirstName.fromStringValue("Aaron"), FirstName.fromStringValue("Adam")),
                singletonList(Address.deserialize(
                        StreetName.fromStringValue("Nulla Street"),
                        HouseNumber.fromStringValue("7a"),
                        ZipCode.fromStringValue("423423"),
                        CityName.fromStringValue("Mankato"),
                        Region.fromStringValue("Mississippi"),
                        Country.fromStringValue("USA")
                ))));
        assertThat(yaml, is("" +
                "---\n" +
                "addresses:\n" +
                "- houseNumber: \"7a\"\n" +
                "  zipCode: \"423423\"\n" +
                "  country: \"USA\"\n" +
                "  streetName: \"Nulla Street\"\n" +
                "  region: \"Mississippi\"\n" +
                "  city: \"Mankato\"\n" +
                "firstNames:\n" +
                "- \"Aaron\"\n" +
                "- \"Adam\"\n"));
    }
}
