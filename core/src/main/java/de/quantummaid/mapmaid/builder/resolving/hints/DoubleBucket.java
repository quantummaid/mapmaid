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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoubleBucket {
    private final ResolvedType type;
    private final Bucket<TypeDeserializer> deserializers;
    private final Bucket<TypeSerializer> serializers;
    private final List<String> validations;

    public static DoubleBucket doubleBucket(final ResolvedType type,
                                            final Bucket<TypeDeserializer> deserializers,
                                            final Bucket<TypeSerializer> serializers) {
        validateNotNull(type, "type");
        validateNotNull(deserializers, "deserializers");
        validateNotNull(serializers, "serializers");
        return new DoubleBucket(type, deserializers, serializers, new ArrayList<>(3));
    }

    public ResolvedType getType() {
        return this.type;
    }

    public boolean bothPresent() {
        return nonNull(this.deserializers) && nonNull(this.serializers);
    }

    public void deserializerHint(final Hint<TypeDeserializer> hint) {
        // TODO validate present
        hint.apply(this.deserializers);
    }

    public Bucket<TypeDeserializer> deserializers() {
        return ofNullable(this.deserializers).orElseThrow(); // TODO
    }

    public void serializerHint(final Hint<TypeSerializer> hint) {
        // TODO validate present
        hint.apply(this.serializers);
    }

    public Bucket<TypeSerializer> serializers() {
        return ofNullable(this.serializers).orElseThrow(); // TODO
    }
}
