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

package de.quantummaid.mapmaid.mapper.definitions.validation;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serializationOnly;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FullRequirements {
    private final Map<ResolvedType, RequiredCapabilities> requiredCapabilities;

    public static FullRequirements fullRequirements(final Map<Definition, RequiredCapabilities> partialRequirements,
                                                    final Definitions definitions) {
        final Map<ResolvedType, RequiredCapabilities> fullRequirements = new HashMap<>();
        partialRequirements.forEach((definition, capabilities) ->
                recurseType(definition.type(), definitions, capabilities, fullRequirements));
        return new FullRequirements(fullRequirements);
    }

    private static void recurseType(final ResolvedType type,
                                    final Definitions definitions,
                                    final RequiredCapabilities capabilities,
                                    final Map<ResolvedType, RequiredCapabilities> fullRequirements) {
        if (!fullRequirements.containsKey(type)) {
            fullRequirements.put(type, capabilities);
        } else {
            final RequiredCapabilities oldCapabilities = fullRequirements.get(type);
            if (oldCapabilities.contains(capabilities)) {
                return;
            }
            oldCapabilities.add(capabilities);
        }

        definitions.getOptionalDefinitionForType(type).ifPresent(definition -> {
            if (capabilities.hasSerialization()) {
                definition.serializer().ifPresent(typeSerializer ->
                        typeSerializer.requiredTypes().forEach(requiredType ->
                                recurseType(requiredType, definitions, serializationOnly(), fullRequirements)));
            }

            if (capabilities.hasDeserialization()) {
                definition.deserializer().ifPresent(typeDeserializer ->
                        typeDeserializer.requiredTypes().forEach(requiredType ->
                                recurseType(requiredType, definitions, deserializationOnly(), fullRequirements)));
            }
        });
    }

    public void validate(final Definitions definitions) {
        this.requiredCapabilities.forEach((type, capabilities) -> {
            final Definition definition = definitions.getOptionalDefinitionForType(type)
                    .orElseThrow(() -> new RuntimeException(format("Missing definition of type '%s'", type.description())));

            if (capabilities.hasDeserialization()) {
                if (definition.deserializer().isEmpty()) {
                    throw new RuntimeException(format("Type '%s' needs to be deserializable", type.description()));
                }
            } else {
                if (definition.deserializer().isPresent()) {
                    throw new RuntimeException(format("Type '%s' is illegally deserializable", type.description()));
                }
            }

            if (capabilities.hasSerialization()) {
                if (definition.serializer().isEmpty()) {
                    throw new RuntimeException(format("Type '%s' needs to be serializable", type.description()));
                }
            } else {
                if (definition.serializer().isPresent()) {
                    throw new RuntimeException(format("Type '%s' is illegally serializable", type.description()));
                }
            }
        });
    }
}
