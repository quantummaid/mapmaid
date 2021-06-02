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

package de.quantummaid.mapmaid.builder.resolving.framework;

import de.quantummaid.mapmaid.builder.resolving.framework.processing.CollectionResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.isNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Report<T> {
    private final CollectionResult<T> result;
    private final String errorMessage;

    public static <T> Report<T> success(final CollectionResult<T> result) {
        validateNotNull(result, "result");
        return new Report<>(result, null);
    }

    public static <T> Report<T> failure(final CollectionResult<T> result, final String errorMessage) {
        validateNotNull(errorMessage, "errorMessage");
        return new Report<>(result, errorMessage);
    }

    public static <T> Report<T> empty() {
        return new Report<>(null, null);
    }

    public boolean isSuccess() {
        return isNull(errorMessage) && !isNull(result);
    }

    public boolean isEmpty() {
        return isNull(errorMessage) && isNull(result);
    }

    public CollectionResult<T> result() {
        validateNotNull(this.result, "result");
        return this.result;
    }

    public String errorMessage() {
        validateNotNull(this.errorMessage, "errorMessage");
        return this.errorMessage;
    }
}
