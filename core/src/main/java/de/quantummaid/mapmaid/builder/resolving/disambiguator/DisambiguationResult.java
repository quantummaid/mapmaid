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

package de.quantummaid.mapmaid.builder.resolving.disambiguator;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DisambiguationResult {
    private final TypeSerializer serializer;
    private final TypeDeserializer deserializer;

    public static DisambiguationResult serializationOnlyResult(final TypeSerializer serializer) {
        validateNotNull(serializer, "serializer");
        return new DisambiguationResult(serializer, null);
    }

    public static DisambiguationResult deserializationOnlyResult(final TypeDeserializer deserializer) {
        validateNotNull(deserializer, "deserializer");
        return new DisambiguationResult(null, deserializer);
    }

    public static DisambiguationResult duplexResult(final TypeSerializer serializer,
                                                    final TypeDeserializer deserializer) {
        validateNotNull(serializer, "serializer");
        validateNotNull(deserializer, "deserializer");
        return new DisambiguationResult(serializer, deserializer);
    }

    public TypeSerializer serializer() {
        return this.serializer;
    }

    public TypeDeserializer deserializer() {
        return this.deserializer;
    }
}
