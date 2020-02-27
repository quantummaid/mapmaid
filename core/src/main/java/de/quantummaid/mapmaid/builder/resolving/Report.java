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

import de.quantummaid.mapmaid.builder.resolving.processing.CollectionResult;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.isNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Report {
    private final CollectionResult result;
    private final String errorMessage;

    public static Report success(final CollectionResult result) {
        validateNotNull(result, "result");
        return new Report(result, null);
    }

    public static Report failure(final CollectionResult result, final String errorMessage) {
        validateNotNull(errorMessage, "errorMessage");
        return new Report(result, errorMessage);
    }

    public boolean isSuccess() {
        return isNull(this.errorMessage);
    }

    public CollectionResult result() {
        validateNotNull(this.result, "result");
        return this.result;
    }

    public String errorMessage() {
        validateNotNull(this.errorMessage, "errorMessage");
        return this.errorMessage;
    }
}
