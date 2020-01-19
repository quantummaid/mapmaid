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

package de.quantummaid.mapmaid.mapper.definitions.validation.statemachine;

import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.mapper.definitions.validation.statemachine.SerializationAndDeserialization.serializationAndDeserialization;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationOnly implements DefinitionRequirements {

    public static DefinitionRequirements serializationOnly() {
        return new SerializationOnly();
    }

    @Override
    public DefinitionRequirements addDeserialization() {
        return serializationAndDeserialization();
    }

    @Override
    public DefinitionRequirements addSerialization() {
        return this;
    }

    @Override
    public void validate(final Definition definition) {
        final ResolvedType type = definition.type();
        if (definition.deserializer().isPresent()) {
            throw new RuntimeException(format("Type '%s' is illegally deserializable", type.description()));
        }
        if (definition.serializer().isEmpty()) {
            throw new RuntimeException(format("Type '%s' needs to be serializable", type.description()));
        }
    }
}
