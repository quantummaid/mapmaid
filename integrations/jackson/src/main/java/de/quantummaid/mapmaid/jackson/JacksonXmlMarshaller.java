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
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;

import static de.quantummaid.mapmaid.jackson.JacksonMarshaller.jacksonMarshaller;
import static de.quantummaid.mapmaid.jackson.JacksonUnmarshaller.jacksonUnmarshaller;
import static de.quantummaid.mapmaid.jackson.ObjectMappers.objectMapperXml;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.XML;

public final class JacksonXmlMarshaller implements MarshallerAndUnmarshaller<String> {
    private final ObjectMapper objectMapper = objectMapperXml();

    public static JacksonXmlMarshaller jacksonXmlMarshaller() {
        return new JacksonXmlMarshaller();
    }

    @Override
    public MarshallingType<String> marshallingType() {
        return XML;
    }

    @Override
    public Marshaller<String> marshaller() {
        return jacksonMarshaller(this.objectMapper);
    }

    @Override
    public Unmarshaller<String> unmarshaller() {
        return jacksonUnmarshaller(this.objectMapper);
    }
}
