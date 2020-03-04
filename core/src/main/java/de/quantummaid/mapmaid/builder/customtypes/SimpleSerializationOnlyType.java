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

package de.quantummaid.mapmaid.builder.customtypes;

import de.quantummaid.mapmaid.builder.GenericType;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimpleSerializationOnlyType<T> implements SerializationOnlyType<T> {
    private final GenericType<T> type;
    private final TypeSerializer serializer;

    static <T> SimpleSerializationOnlyType<T> simpleSerializationOnlyType(final GenericType<T> type,
                                                                          final TypeSerializer serializer) {
        validateNotNull(type, "type");
        validateNotNull(serializer, "serializer");
        return new SimpleSerializationOnlyType<>(type, serializer);
    }

    @Override
    public GenericType<T> type() {
        return this.type;
    }

    @Override
    public TypeSerializer createSerializer() {
        return this.serializer;
    }
}
