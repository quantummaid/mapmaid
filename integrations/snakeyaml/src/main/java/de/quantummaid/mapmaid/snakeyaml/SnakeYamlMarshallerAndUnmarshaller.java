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

package de.quantummaid.mapmaid.snakeyaml;

import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.YAML;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static de.quantummaid.mapmaid.snakeyaml.SnakeYamlMarshaller.snakeYamlMarshaller;
import static de.quantummaid.mapmaid.snakeyaml.SnakeYamlUnmarshaller.snakeYamlUnmarshaller;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SnakeYamlMarshallerAndUnmarshaller implements MarshallerAndUnmarshaller<String> {
    private final SnakeYamlMarshaller marshaller;
    private final SnakeYamlUnmarshaller unmarshaller;

    public static SnakeYamlMarshallerAndUnmarshaller snakeYamlMarshallerAndUnmarshaller() {
        final DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        final Yaml yaml = new Yaml(dumperOptions);
        return snakeYamlMarshallerAndUnmarshaller(yaml);
    }

    public static SnakeYamlMarshallerAndUnmarshaller snakeYamlMarshallerAndUnmarshaller(final Yaml yaml) {
        validateNotNull(yaml, "yaml");
        final SnakeYamlMarshaller marshaller = snakeYamlMarshaller(yaml);
        final SnakeYamlUnmarshaller unmarshaller = snakeYamlUnmarshaller(yaml);
        return new SnakeYamlMarshallerAndUnmarshaller(marshaller, unmarshaller);
    }

    @Override
    public MarshallingType<String> marshallingType() {
        return YAML;
    }

    @Override
    public Marshaller<String> marshaller() {
        return marshaller;
    }

    @Override
    public Unmarshaller<String> unmarshaller() {
        return unmarshaller;
    }
}
