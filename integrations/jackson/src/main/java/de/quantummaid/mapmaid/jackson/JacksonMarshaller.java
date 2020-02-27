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

package de.quantummaid.mapmaid.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.quantummaid.mapmaid.builder.DependencyRegistry;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.*;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonMarshaller implements Recipe {
    private final MarshallingType marshallingType;
    private final ObjectMapper objectMapper;

    public static JacksonMarshaller jacksonMarshallerFor(final MarshallingType marshallingType,
                                                         final ObjectMapper objectMapper) {
        validateNotNull(objectMapper, "objectMapper");
        final SimpleModule simpleModule = new SimpleModule();
        // TODO
        simpleModule.setDeserializerModifier(new AlwaysStringValueJacksonDeserializerModifier());
        // TODO
        objectMapper.setSerializationInclusion(NON_NULL);
        // TODO
        objectMapper.registerModule(simpleModule);
        return new JacksonMarshaller(marshallingType, objectMapper);
    }

    public static JacksonMarshaller jacksonMarshallerJson() {
        return jacksonMarshallerFor(json(), new ObjectMapper());
    }

    public static JacksonMarshaller jacksonMarshallerXml() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return jacksonMarshallerFor(xml(), xmlMapper);
    }

    public static JacksonMarshaller jacksonMarshallerYaml() {
        final YAMLMapper yamlMapper = new YAMLMapper();
        return jacksonMarshallerFor(yaml(), yamlMapper);
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder, final DependencyRegistry dependencyRegistry) {
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.setDeserializerModifier(new AlwaysStringValueJacksonDeserializerModifier());
        this.objectMapper.setSerializationInclusion(NON_NULL);
        this.objectMapper.registerModule(simpleModule);
        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(
                this.marshallingType,
                this.objectMapper::writeValueAsString,
                this.objectMapper::readValue));
    }
}
