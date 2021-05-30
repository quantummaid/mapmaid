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

package de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder;

import java.util.Arrays;
import java.util.List;

import static de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.withPredictableStacktrace;
import static java.util.Collections.emptyList;

public final class ExceptionBuilder {
    private String message;
    private Throwable cause;
    private List<Throwable> suppressed = emptyList();
    private int depth = 0;

    public static ExceptionBuilder anUnsupportedOperationException() {
        return new ExceptionBuilder();
    }

    public ExceptionBuilder withMessage(final String message) {
        this.message = message;
        return this;
    }

    public ExceptionBuilder withCause(final Throwable cause) {
        this.cause = cause;
        return this;
    }

    public ExceptionBuilder withSuppressed(final Throwable... suppressed) {
        this.suppressed = Arrays.asList(suppressed);
        return this;
    }

    public ExceptionBuilder withStackTraceDepth(final int depth) {
        this.depth = depth;
        return this;
    }

    public UnsupportedOperationException build() {
        return withPredictableStacktrace(this);
    }

    UnsupportedOperationException createException() {
        final UnsupportedOperationException exception = new UnsupportedOperationException(message);
        if (cause != null) {
            exception.initCause(cause);
        }
        suppressed.forEach(exception::addSuppressed);
        return exception;
    }

    int depth() {
        return depth;
    }
}
