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

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SnakeYamlMarshaller implements Marshaller<String> {
    private final Yaml yaml;

    public static SnakeYamlMarshaller snakeYamlMarshaller(final Yaml yaml) {
        validateNotNull(yaml, "yaml");
        return new SnakeYamlMarshaller(yaml);
    }

    @Override
    public String marshal(final Object object) {
        return yaml.dump(object);
    }
}
