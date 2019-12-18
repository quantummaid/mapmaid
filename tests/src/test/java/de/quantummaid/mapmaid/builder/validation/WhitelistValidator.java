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

package de.quantummaid.mapmaid.builder.validation;

import java.util.Collection;

import static de.quantummaid.mapmaid.builder.validation.CustomTypeValidationException.customTypeValidationException;
import static java.util.stream.Collectors.joining;

public final class WhitelistValidator {
    private WhitelistValidator() {
    }

    public static String ensureOneOf(final String value, final Collection<String> whitelist, final String description) {
        final String sanitized = SanityValidator.sanitized(value, description);
        final String lowerCase = sanitized.toLowerCase();
        for (final String s : whitelist) {
            if (s.toLowerCase().equals(lowerCase)) {
                return s;
            }
        }
        final String whitelistAsString = whitelist.stream()
                .map(String::valueOf)
                .collect(joining(", "));
        throw customTypeValidationException("%s must be one of the %s", description, whitelistAsString);
    }
}
