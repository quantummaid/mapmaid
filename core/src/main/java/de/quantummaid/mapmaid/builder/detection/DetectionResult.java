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

import java.util.function.BiFunction;
import java.util.function.Function;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Objects.nonNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionResult<T> {
    private final T result;
    private final String reasonForFailure;

    public static <A, B, C> DetectionResult<C> combine(final DetectionResult<A> a,
                                                       final DetectionResult<B> b,
                                                       final BiFunction<A, B, C> combinator) {
        if (!a.isFailure() && !b.isFailure()) {
            final C combination = combinator.apply(a.result, b.result);
            return success(combination);
        }
        final StringBuilder reasonBuilder = new StringBuilder();
        if (a.isFailure()) {
            reasonBuilder.append(a.reasonForFailure).append("\n");
        }
        if (b.isFailure()) {
            reasonBuilder.append(b.reasonForFailure).append("\n");
        }
        return failure(reasonBuilder.toString());
    }

    public static <T> DetectionResult<T> success(final T result) {
        validateNotNull(result, "result");
        return new DetectionResult<>(result, null);
    }

    public static <T> DetectionResult<T> failure(final String reasonForFailure) {
        validateNotNull(reasonForFailure, "reasonForFailure");
        return new DetectionResult<>(null, reasonForFailure);
    }

    public boolean isFailure() {
        return nonNull(this.reasonForFailure);
    }

    public String reasonForFailure() {
        return this.reasonForFailure;
    }

    public T result() {
        return this.result;
    }

    public <X> DetectionResult<X> map(final Function<T, X> mapper) {
        if (isFailure()) {
            return (DetectionResult<X>) this;
        }
        final X mapped = mapper.apply(this.result);
        return success(mapped);
    }
}
