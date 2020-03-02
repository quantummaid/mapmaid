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

package de.quantummaid.mapmaid.builder.validation;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.validation.CustomTypeValidationException.customTypeValidationException;

@ToString
@EqualsAndHashCode
public final class NumericValidator {
    private NumericValidator() {
    }

    public static double ensureDouble(final String value,
                                      final String description) {
        final String sanitized = SanityValidator.sanitized(value, description);
        try {
            return Double.parseDouble(sanitized);
        } catch (final NumberFormatException nfm) {
            throw customTypeValidationException(nfm, "%s is not a double.",
                    description);

        }
    }

    public static int ensurePositiveInteger(final String value, final String description) {
        final String sanitized = SanityValidator.sanitized(value, description);
        try {
            final int parsedValue = Integer.parseInt(sanitized);
            if (parsedValue < 0) {
                throw customTypeValidationException("%s is negative. Only positivity allowed here.", description);
            }
            return parsedValue;
        } catch (final NumberFormatException exception) {
            throw customTypeValidationException("%s not a number",
                    description);
        }
    }
}
