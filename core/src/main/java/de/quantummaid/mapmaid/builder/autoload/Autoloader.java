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

package de.quantummaid.mapmaid.builder.autoload;

import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.builder.autoload.AutoloadingException.autoloadingException;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class Autoloader {

    private Autoloader() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> autoload(final String fullyQualifiedClassName) {
        validateNotNull(fullyQualifiedClassName, "fullyQualifiedClassName");
        return loadClass(fullyQualifiedClassName).map(clazz -> {
            final Method staticInitializer = findStaticInitializer(clazz);
            return (T) invoke(staticInitializer);
        });
    }

    private static Optional<Class<?>> loadClass(final String fullyQualifiedClassName) {
        final ClassLoader classLoader = currentThread().getContextClassLoader();
        try {
            final Class<?> clazz = classLoader.loadClass(fullyQualifiedClassName);
            return of(clazz);
        } catch (final ClassNotFoundException e) {
            return empty();
        }
    }

    private static Method findStaticInitializer(final Class<?> clazz) {
        final Method[] methods = clazz.getMethods();
        return stream(methods)
                .filter(method -> isPublic(method.getModifiers()))
                .filter(method -> isStatic(method.getModifiers()))
                .filter(method -> method.getReturnType().equals(clazz))
                .filter(method -> method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(format(
                        "class '%s' needs a public static zero-parameter initializer to be autoloadable",
                        clazz)));
    }

    private static MarshallerAndUnmarshaller invoke(final Method staticInitializer) {
        try {
            return (MarshallerAndUnmarshaller) staticInitializer.invoke(null);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw autoloadingException(e);
        }
    }
}
