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

package de.quantummaid.mapmaid.builder.detection;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionResult<T> {
    private final T result;
    private final List<String> reasonsForFailure;

    public static <A, B, C> DetectionResult<C> combine(final DetectionResult<A> a,
                                                       final DetectionResult<B> b,
                                                       final BiFunction<A, B, C> combinator) {
        if (!a.isFailure() && !b.isFailure()) {
            final C combination = combinator.apply(a.result, b.result);
            return success(combination);
        }
        final List<String> combinedReasons = smallList();
        combinedReasons.addAll(a.reasonsForFailure);
        combinedReasons.addAll(b.reasonsForFailure);
        return failure(combinedReasons);
    }

    public static <T> DetectionResult<T> success(final T result) {
        validateNotNull(result, "result");
        return new DetectionResult<>(result, emptyList());
    }

    public static <T> DetectionResult<T> failure(final String reasonForFailure) {
        validateNotNull(reasonForFailure, "reasonForFailure");
        return failure(singletonList(reasonForFailure));
    }

    public static <T> DetectionResult<T> failure(final List<String> reasonsForFailure) {
        validateNotNull(reasonsForFailure, "reasonsForFailure");
        return new DetectionResult<>(null, reasonsForFailure);
    }

    public static <T> DetectionResult<T> followUpFailure(final DetectionResult<?> detectionResult) {
        if (!detectionResult.isFailure()) {
            throw new IllegalArgumentException("Can only follow up on failures");
        }
        return failure(detectionResult.reasonsForFailure());
    }

    public boolean isFailure() {
        return !this.reasonsForFailure.isEmpty();
    }

    public String reasonForFailure() {
        // TODO
        return this.reasonsForFailure.stream()
                .collect(joining("\n", "[", "]"));
    }

    public List<String> reasonsForFailure() {
        return this.reasonsForFailure;
    }

    public T result() {
        return this.result;
    }

    @SuppressWarnings("unchecked")
    public <X> DetectionResult<X> map(final Function<T, X> mapper) {
        if (isFailure()) {
            return (DetectionResult<X>) this;
        }
        final X mapped = mapper.apply(this.result);
        return success(mapped);
    }

    @SuppressWarnings("unchecked")
    public <X> DetectionResult<X> flatMap(final Function<T, DetectionResult<X>> mapper) {
        if (isFailure()) {
            return (DetectionResult<X>) this;
        }
        return mapper.apply(this.result);
    }

    public DetectionResult<T> or(final Supplier<DetectionResult<T>> alternative) {
        if (isFailure()) {
            return alternative.get();
        }
        return this;
    }
}
