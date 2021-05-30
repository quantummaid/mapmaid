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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.currentThread;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ExceptionWithPredictableStacktrace implements Runnable {
    private static final int MAX_WAIT_TIME = 1000;
    private static final Map<UUID, UnsupportedOperationException> EXCEPTIONS = new ConcurrentHashMap<>();

    private final UUID key;
    private final ExceptionBuilder builder;

    static UnsupportedOperationException withPredictableStacktrace(final ExceptionBuilder builder) {
        final UUID key = UUID.randomUUID();
        final Thread thread = new Thread(new ExceptionWithPredictableStacktrace(key, builder));
        thread.start();
        try {
            thread.join(MAX_WAIT_TIME);
        } catch (final InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException(e);
        }
        if (!EXCEPTIONS.containsKey(key)) {
            throw new IllegalStateException("this should never happen");
        }
        return EXCEPTIONS.remove(key);
    }

    @Override
    public void run() {
        final int depth = builder.depth();
        nest(depth);
    }

    private void nest(final int count) {
        if (count == 0) {
            final UnsupportedOperationException exception = builder.createException();
            EXCEPTIONS.put(key, exception);
        } else {
            nest(count - 1);
        }
    }
}
