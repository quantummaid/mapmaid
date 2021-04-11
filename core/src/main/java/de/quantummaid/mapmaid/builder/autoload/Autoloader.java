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
import de.quantummaid.reflectmaid.Executor;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@SuppressWarnings("java:S2658")
@Slf4j
public final class Autoloader {

    private Autoloader() {
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> autoload(final String fullyQualifiedClassName, final ReflectMaid reflectMaid) {
        validateNotNull(fullyQualifiedClassName, "fullyQualifiedClassName");
        return loadClass(fullyQualifiedClassName, reflectMaid).map(clazz -> {
            final ResolvedMethod staticInitializer = findStaticInitializer(clazz);
            return (T) invoke(staticInitializer);
        });
    }

    private static Optional<ResolvedType> loadClass(final String fullyQualifiedClassName,
                                                    final ReflectMaid reflectMaid) {
        final ClassLoader classLoader = currentThread().getContextClassLoader();
        try {
            final Class<?> clazz = classLoader.loadClass(fullyQualifiedClassName);
            return of(reflectMaid.resolve(clazz));
        } catch (final ClassNotFoundException e) {
            log.trace("did not find class {} for autoloading", fullyQualifiedClassName, e);
            return empty();
        }
    }

    private static ResolvedMethod findStaticInitializer(final ResolvedType clazz) {
        final List<ResolvedMethod> methods = clazz.methods();
        return methods.stream()
                .filter(ResolvedMethod::isPublic)
                .filter(ResolvedMethod::isStatic)
                .filter(method -> method.returnType().isPresent())
                .filter(method -> clazz.equals(method.getReturnType()))
                .filter(method -> method.getParameters().isEmpty())
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(format(
                        "class '%s' needs a public static zero-parameter initializer to be autoloadable",
                        clazz)));
    }

    private static MarshallerAndUnmarshaller<?> invoke(final ResolvedMethod staticInitializer) {
        final Executor executor = staticInitializer.createExecutor();
        return (MarshallerAndUnmarshaller<?>) executor.execute(null, emptyList());
    }
}
