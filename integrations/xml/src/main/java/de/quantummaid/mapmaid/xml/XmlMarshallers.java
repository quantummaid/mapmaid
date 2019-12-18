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

package de.quantummaid.mapmaid.xml;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public final class XmlMarshallers {

    private XmlMarshallers() {
    }

    public static Marshaller xmlMarshaller() {
        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return object -> {
            try {
                return xmlMapper.writeValueAsString(object);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Unmarshaller xmlUnmarshaller() {
        final XmlMapper xmlMapper = new XmlMapper();
        return new Unmarshaller() {
            @Override
            public <T> T unmarshal(final String input, final Class<T> type) {
                try {
                    return xmlMapper.readValue(input, type);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
