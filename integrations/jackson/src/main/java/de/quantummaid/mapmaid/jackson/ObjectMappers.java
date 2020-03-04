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

package de.quantummaid.mapmaid.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

final class ObjectMappers {

    private ObjectMappers() {
    }

    static ObjectMapper objectMapperJson() {
        return objectMapperFor(new ObjectMapper());
    }

    public static ObjectMapper objectMapperXml() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapperFor(xmlMapper);
    }

    public static ObjectMapper objectMapperYaml() {
        final YAMLMapper yamlMapper = new YAMLMapper();
        return objectMapperFor(yamlMapper);
    }

    private static ObjectMapper objectMapperFor(final ObjectMapper objectMapper) {
        validateNotNull(objectMapper, "objectMapper");
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.setDeserializerModifier(new AlwaysStringValueJacksonDeserializerModifier());
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
