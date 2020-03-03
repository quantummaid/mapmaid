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

package de.quantummaid.mapmaid.builder.resolving.disambiguator;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.isNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializersAndDeserializers {
    private final List<TypeSerializer> serializers;
    private final List<TypeDeserializer> deserializers;

    public static SerializersAndDeserializers serializersOnly(final List<TypeSerializer> serializers) {
        validateNotNull(serializers, "serializers");
        return new SerializersAndDeserializers(serializers, null);
    }

    public static SerializersAndDeserializers deserializersOnly(final List<TypeDeserializer> deserializers) {
        validateNotNull(deserializers, "deserializers");
        return new SerializersAndDeserializers(null, deserializers);
    }

    public static SerializersAndDeserializers serializersAndDeserializers(final List<TypeSerializer> serializers,
                                                                          final List<TypeDeserializer> deserializers) {
        return new SerializersAndDeserializers(serializers, deserializers);
    }

    public boolean deserializationOnly() {
        return isNull(this.serializers);
    }

    public boolean serializationOnly() {
        return isNull(this.deserializers);
    }

    public List<TypeSerializer> serializers() {
        return this.serializers;
    }

    public List<TypeDeserializer> deserializers() {
        return this.deserializers;
    }
}
