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

package de.quantummaid.mapmaid.mapper.serialization.universalmerger;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public final class FieldNormalizer {
    private static final String PADDING = "_";
    private static final Predicate<String> IS_PADDING = Pattern.compile("_*").asMatchPredicate();

    private FieldNormalizer() {
    }

    public static String determineField(final String base, final Collection<String> existingFields) {
        final int padLength = findTypeField(base, existingFields)
                .map(String::length)
                .map(length -> length - base.length())
                .map(length -> length + 1)
                .orElse(0);
        return PADDING.repeat(padLength) + base;
    }

    public static Optional<String> findTypeField(final String base, final Collection<String> fields) {
        return fields.stream()
                .filter(s -> isIdentifier(s, base))
                .max(comparing(String::length));
    }

    public static boolean isIdentifier(final String string, final String base) {
        if (!string.endsWith(base)) {
            return false;
        }
        final String withoutBase = string.substring(0, string.length() - base.length());
        return IS_PADDING.test(withoutBase);
    }
}
