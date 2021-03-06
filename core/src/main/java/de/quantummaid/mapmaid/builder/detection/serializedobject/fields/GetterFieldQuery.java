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

package de.quantummaid.mapmaid.builder.detection.serializedobject.fields;

import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.SerializationFieldQuery;
import de.quantummaid.reflectmaid.Executor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetterFieldQuery implements SerializationFieldQuery {
    private final ResolvedMethod method;
    private final Executor executor;

    public static SerializationFieldQuery getterFieldQuery(final ResolvedMethod method) {
        validateNotNull(method, "method");
        final Executor executor = method.createExecutor();
        return new GetterFieldQuery(method, executor);
    }

    @Override
    public Object query(final Object object) {
        return executor.execute(object, emptyList());
    }

    public ResolvedMethod method() {
        return this.method;
    }

    @Override
    public String describe() {
        return format("getter method '%s'", this.method.describe());
    }
}
