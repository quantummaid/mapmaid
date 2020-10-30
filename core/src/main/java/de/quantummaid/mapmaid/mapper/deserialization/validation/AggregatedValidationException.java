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

package de.quantummaid.mapmaid.mapper.deserialization.validation;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public final class AggregatedValidationException extends RuntimeException {
    private final List<ValidationError> validationErrors;

    private AggregatedValidationException(final String msg, final List<ValidationError> validationErrors) {
        super(msg);
        this.validationErrors = unmodifiableList(validationErrors);
    }

    public static AggregatedValidationException fromList(final List<ValidationError> validationErrors) {
        final StringBuilder sb = new StringBuilder("deserialization encountered validation errors. ");
        for (final ValidationError entry : validationErrors) {
            sb.append("Validation error at '" + entry.propertyPath + "', " + entry.message + "; ");
        }

        return new AggregatedValidationException(sb.toString(), validationErrors);
    }

    public List<ValidationError> getValidationErrors() {
        return unmodifiableList(this.validationErrors);
    }
}
