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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.mapper.definitions.Definition;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.nonNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Report {
    private final Definition definition;
    private final String errorMessage;

    public static Report success(final Definition definition) {
        validateNotNull(definition, "definition");
        return new Report(definition, null);
    }

    public static Report failure(final String errorMessage) {
        validateNotNull(errorMessage, "errorMessage");
        return new Report(null, errorMessage);
    }

    public boolean isSuccess() {
        return nonNull(this.definition);
    }

    public Definition definition() {
        validateNotNull(this.definition, "definition");
        return this.definition;
    }

    public String errorMessage() {
        validateNotNull(this.errorMessage, "errorMessage");
        return this.errorMessage;
    }
}
