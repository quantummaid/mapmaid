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

package de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject;

import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.PublicFieldQuery;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.SerializationFieldQuery;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationField {
    private final ResolvedType type;
    private final String name;
    private final SerializationFieldQuery query;

    public static SerializationField serializationField(final ResolvedType type,
                                                        final String name,
                                                        final SerializationFieldQuery query) {
        validateNotNull(type, "type");
        validateNotNull(name, "name");
        validateNotNull(query, "query");
        return new SerializationField(type, name, query);
    }

    public static SerializationField fromField(final ResolvedType declaringType,
                                               final ResolvedField field) {
        validateNotNull(declaringType, "declaringType");
        validateNotNull(field, "field");
        final ResolvedType fullType = field.type();
        final String name = field.name();
        final SerializationFieldQuery query = PublicFieldQuery.publicFieldQuery(field);
        return serializationField(fullType, name, query);
    }

    public ResolvedType type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public Object query(final Object object) {
        validateNotNull(object, "object");
        return this.query.query(object);
    }

    public SerializationFieldQuery getQuery() {
        return this.query;
    }

    public String describe() {
        return format("%s [%s] via %s", this.name, this.type.simpleDescription(), this.query.describe());
    }
}
