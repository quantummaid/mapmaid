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

package de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import static java.lang.String.format;

public final class CustomPrimitiveSerializationMethodCallException extends RuntimeException {
    private CustomPrimitiveSerializationMethodCallException(final String msg, final Throwable exception) {
        super(msg, exception);
    }

    private CustomPrimitiveSerializationMethodCallException(final String msg) {
        super(msg);
    }

    public static CustomPrimitiveSerializationMethodCallException
    customPrimitiveSerializationMethodCallException(final String msg, final Throwable exception) {
        return new CustomPrimitiveSerializationMethodCallException(msg, exception);
    }

    public static CustomPrimitiveSerializationMethodCallException
    customPrimitiveSerializationMethodCallException(final String msg) {
        return new CustomPrimitiveSerializationMethodCallException(msg);
    }

    public static CustomPrimitiveSerializationMethodCallException fromThrowable(
            final String description,
            final Throwable exception,
            final Class<?> type,
            final MethodHandle methodHandle,
            final Object instance) {
        final String msg = format("%s " +
                        "type: %s. " +
                        "method: %s. " +
                        "instance: %s.",
                description,
                type,
                methodHandle,
                instance);
        return customPrimitiveSerializationMethodCallException(msg, exception);
    }

    public static CustomPrimitiveSerializationMethodCallException fromThrowable(
            final String description,
            final Throwable exception,
            final Class<?> type,
            final Method method,
            final Object instance) {
        final String msg = format("%s " +
                "type: %s. " +
                "method: %s. " +
                "instance: %s.", description, type, method, instance);
        return customPrimitiveSerializationMethodCallException(msg, exception);
    }
}
