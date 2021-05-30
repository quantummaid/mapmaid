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

package de.quantummaid.mapmaid.specs;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.anUnsupportedOperationException;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class ThrowableMappingSpecs {

    @Test
    public void stackTraceCanBeSerialized() {
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .build();

        given(
                aMapMaid()
                        .serializing(StackTraceElement[].class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception.getStackTrace(), StackTraceElement[].class)
                .theSerializationResultWas(List.of(
                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                        "java.base/java.lang.Thread.run(Thread.java:834)"
                ));
    }

    @Test
    public void throwableCanBeSerialized() {
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .build();

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                "java.base/java.lang.Thread.run(Thread.java:834)"
                        )
                ));
    }

    @Test
    public void stackTraceSizeIsLimitedByDefault() {
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withStackTraceDepth(100)
                .build();

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "...[72 more]"
                        )
                ));
    }

    @Test
    public void stackTraceSizeIsLimitCanBeConfigured() {
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withStackTraceDepth(100)
                .build();

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.withMaximumNumberOfStackFramesWhenSerializingExceptions(5))
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:68)",
                                "...[99 more]"
                        )
                ));
    }

    @Test
    public void throwableContainsCause() {
        final UnsupportedOperationException cause = anUnsupportedOperationException()
                .withMessage("i am the cause")
                .build();
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withCause(cause)
                .build();

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                "java.base/java.lang.Thread.run(Thread.java:834)"
                        ),
                        "cause", Map.of(
                                "message", "i am the cause",
                                "type", "java.lang.UnsupportedOperationException",
                                "frames", List.of(
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                        "java.base/java.lang.Thread.run(Thread.java:834)"
                                )
                        )
                ));
    }

    @Test
    public void throwableCanHandleInfiniteLoopInCause() {
        final UnsupportedOperationException cause = anUnsupportedOperationException()
                .withMessage("i am the cause")
                .build();
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withCause(cause)
                .build();
        cause.initCause(exception);

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                "java.base/java.lang.Thread.run(Thread.java:834)"
                        ),
                        "cause", Map.of(
                                "message", "i am the cause",
                                "type", "java.lang.UnsupportedOperationException",
                                "frames", List.of(
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                        "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                        "java.base/java.lang.Thread.run(Thread.java:834)"
                                ),
                                "cause", Map.of(
                                        "message", "foo",
                                        "type", "java.lang.UnsupportedOperationException",
                                        "note", "cyclic reference"
                                )

                        )
                ));
    }

    @Test
    public void throwableContainsSuppressed() {
        final UnsupportedOperationException suppressed0 = anUnsupportedOperationException()
                .withMessage("i am suppressed 0")
                .build();
        final UnsupportedOperationException suppressed1 = anUnsupportedOperationException()
                .withMessage("i am suppressed 1")
                .build();
        final UnsupportedOperationException suppressed2 = anUnsupportedOperationException()
                .withMessage("i am suppressed 2")
                .build();
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withSuppressed(suppressed0, suppressed1, suppressed2)
                .build();

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                "java.base/java.lang.Thread.run(Thread.java:834)"
                        ),
                        "suppressed", List.of(
                                Map.of(
                                        "message", "i am suppressed 0",
                                        "type", "java.lang.UnsupportedOperationException",
                                        "frames", List.of(
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                                "java.base/java.lang.Thread.run(Thread.java:834)"
                                        )
                                ),
                                Map.of(
                                        "message", "i am suppressed 1",
                                        "type", "java.lang.UnsupportedOperationException",
                                        "frames", List.of(
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                                "java.base/java.lang.Thread.run(Thread.java:834)"
                                        )
                                ),
                                Map.of(
                                        "message", "i am suppressed 2",
                                        "type", "java.lang.UnsupportedOperationException",
                                        "frames", List.of(
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                                "java.base/java.lang.Thread.run(Thread.java:834)"
                                        )
                                )
                        )
                ));
    }

    @Test
    public void throwableCanHandleInfiniteLoopInSuppressed() {
        final UnsupportedOperationException suppressed = anUnsupportedOperationException()
                .withMessage("i am suppressed")
                .build();
        final UnsupportedOperationException exception = anUnsupportedOperationException()
                .withMessage("foo")
                .withSuppressed(suppressed)
                .build();
        suppressed.addSuppressed(exception);

        given(
                aMapMaid()
                        .serializing(Throwable.class)
                        .build()
        )
                .when().mapMaidSerializesToUniversalObject(exception, Throwable.class)
                .theSerializationResultWas(Map.of(
                        "message", "foo",
                        "type", "java.lang.UnsupportedOperationException",
                        "frames", List.of(
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                "java.base/java.lang.Thread.run(Thread.java:834)"
                        ),
                        "suppressed", List.of(
                                Map.of(
                                        "message", "i am suppressed",
                                        "type", "java.lang.UnsupportedOperationException",
                                        "frames", List.of(
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionBuilder.createException(ExceptionBuilder.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.nest(ExceptionWithPredictableStacktrace.java:65)",
                                                "de.quantummaid.mapmaid.specs.exceptions.exceptionbuilder.ExceptionWithPredictableStacktrace.run(ExceptionWithPredictableStacktrace.java:60)",
                                                "java.base/java.lang.Thread.run(Thread.java:834)"
                                        ),
                                        "suppressed", List.of(
                                                Map.of(
                                                        "message", "foo",
                                                        "type", "java.lang.UnsupportedOperationException",
                                                        "note", "cyclic reference"
                                                )
                                        )
                                )
                        )
                ));
    }
}
