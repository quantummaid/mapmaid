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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilterResult {
    private final List<String> reasonsForDenial;

    public static FilterResult combined(final List<FilterResult> results) {
        final List<String> combinedReasons = results.stream()
                .map(FilterResult::reasonsForDenial)
                .flatMap(Collection::stream)
                .collect(toList());
        return new FilterResult(combinedReasons);
    }

    public static FilterResult allowed() {
        return new FilterResult(emptyList());
    }

    public static FilterResult denied(final String reasonForDenial) {
        validateNotNull(reasonForDenial, "reasonForDenial");
        return new FilterResult(List.of(reasonForDenial));
    }

    public boolean isAllowed() {
        return this.reasonsForDenial.isEmpty();
    }

    public List<String> reasonsForDenial() {
        return unmodifiableList(this.reasonsForDenial);
    }
}
