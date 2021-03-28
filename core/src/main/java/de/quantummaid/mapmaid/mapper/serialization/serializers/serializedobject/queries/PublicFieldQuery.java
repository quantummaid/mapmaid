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

package de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries;

import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublicFieldQuery implements SerializationFieldQuery {
    private final ResolvedField field;

    public static SerializationFieldQuery publicFieldQuery(final ResolvedField field) {
        return new PublicFieldQuery(field);
    }

    @Override
    public Object query(final Object object) {
        try {
            return this.field.getField().get(object);
        } catch (final IllegalAccessException e) {
            throw mapMaidException(format("Failed to query field '%s'", this.field.describe()), e);
        }
    }

    public ResolvedField field() {
        return this.field;
    }

    @Override
    public String describe() {
        return format("field '%s'", this.field.describe());
    }
}
