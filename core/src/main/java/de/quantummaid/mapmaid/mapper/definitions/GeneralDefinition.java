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

package de.quantummaid.mapmaid.mapper.definitions;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeneralDefinition implements Definition {
    private final TypeIdentifier type;
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static Definition generalDefinition(final TypeIdentifier type,
                                               final TypeSerializer serializer,
                                               final TypeDeserializer deserializer) {
        validateNotNull(type, "type");
        if (serializer == null) {
            validateNotNull(deserializer, "deserializer");
        }
        if (deserializer == null) {
            validateNotNull(serializer, "serializer");
        }
        return new GeneralDefinition(type, serializer, deserializer);
    }

    @Override
    public Optional<TypeSerializer> serializer() {
        return ofNullable(this.serializer);
    }

    @Override
    public Optional<TypeDeserializer> deserializer() {
        return ofNullable(this.deserializer);
    }

    @Override
    public TypeIdentifier type() {
        return this.type;
    }

    @Override
    public Optional<TypeIdentifier> parent() {
        return Optional.empty();
    }
}
