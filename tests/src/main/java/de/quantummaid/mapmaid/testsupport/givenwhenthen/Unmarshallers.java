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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

public final class Unmarshallers {

    private Unmarshallers() {
    }

    public static Unmarshaller jsonUnmarshaller() {
        final Gson gson = new Gson();
        return input -> gson.fromJson(input, Object.class);
    }

    public static Unmarshaller xmlUnmarshaller() {
        final XmlMapper xmlMapper = new XmlMapper();
        return input -> {
            try {
                return xmlMapper.readValue(input, Object.class);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static Unmarshaller yamlUnmarshaller() {
        final DumperOptions options = new DumperOptions();
        final Yaml yaml = new Yaml(options);
        return yaml::load;
    }
}
