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

package de.quantummaid.mapmaid.yaml;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

public final class YamlMarshallers {

    private YamlMarshallers() {
    }

    public static Marshaller yamlMarshaller() {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        final Yaml yaml = new Yaml(options);
        return yaml::dump;
    }

    public static Unmarshaller yamlUnmarshaller() {
        final DumperOptions options = new DumperOptions();
        final Yaml yaml = new Yaml(options);
        return new Unmarshaller() {
            @Override
            public <T> T unmarshal(final String input, final Class<T> type) {
                return yaml.load(input);
            }
        };
    }
}
