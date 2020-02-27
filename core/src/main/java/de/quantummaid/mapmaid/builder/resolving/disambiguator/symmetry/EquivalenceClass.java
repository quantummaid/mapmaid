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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.symmetry;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EquivalenceClass {
    private final List<TypeSerializer> serializers;
    private final List<TypeDeserializer> deserializers;

    public static EquivalenceClass equivalenceClass() {
        return new EquivalenceClass(smallList(), smallList());
    }

    public void addSerializer(final TypeSerializer serializer) {
        validateNotNull(serializer, "serializer");
        this.serializers.add(serializer);
    }

    public void addDeserializer(final TypeDeserializer deserializer) {
        validateNotNull(deserializer, "deserializer");
        this.deserializers.add(deserializer);
    }

    public boolean fullySupported() {
        return !this.serializers.isEmpty() && !this.deserializers.isEmpty();
    }

    public List<TypeSerializer> serializers() {
        return this.serializers;
    }

    public List<TypeDeserializer> deserializers() {
        return this.deserializers;
    }
}
