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

import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EquivalenceSignature implements Comparable<EquivalenceSignature> {
    private final Map<String, ResolvedType> fields;

    public static EquivalenceSignature ofDeserializer(final SerializedObjectDeserializer deserializer) {
        final Map<String, ResolvedType> fields = deserializer.fields().fields();
        return new EquivalenceSignature(fields);
    }

    public Optional<TypeSerializer> match(final SerializationFieldOptions serializer) {
        return serializer.instantiate(this.fields);
    }

    public int size() {
        return this.fields.size();
    }

    @Override
    public int compareTo(final EquivalenceSignature other) {
        final Integer mySize = this.fields.size();
        final Integer otherSize = other.fields.size();
        return mySize.compareTo(otherSize) * -1;
    }
}
