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

package de.quantummaid.mapmaid.builder.detection.serializedobject;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.stream;

public final class CodeNeedsToBeCompiledWithParameterNamesException extends RuntimeException {

    private CodeNeedsToBeCompiledWithParameterNamesException(final String message) {
        super(message);
    }

    public static void validateParameterNamesArePresent(final Class<?> type) {
        stream(type.getDeclaredMethods())
                .filter(method -> isPublic(method.getModifiers()))
                .forEach(CodeNeedsToBeCompiledWithParameterNamesException::validateParameterNamesArePresent);
    }

    private static void validateParameterNamesArePresent(final Executable method) {
        NotNullValidator.validateNotNull(method, "method");
        if (method.getParameterCount() == 0) {
            return;
        }
        final Parameter[] parameters = method.getParameters();
        final boolean allNamesArePresent = stream(parameters)
                .allMatch(Parameter::isNamePresent);
        if (!allNamesArePresent) {
            throw codeNeedsToBeCompiledWithParameterNamesException(method);
        }
    }

    private static CodeNeedsToBeCompiledWithParameterNamesException codeNeedsToBeCompiledWithParameterNamesException(
            final Executable method) {
        final String className = method.getDeclaringClass().getName();
        final String methodName = method.getName();
        final String message = String.format("" +
                        "The class '%s' has been compiled without the '-parameters' compiler option," +
                        " therefore its method '%s' cannot be used%n" +
                        "with MapMaid. MapMaid relies heavily on parameter names of the" +
                        " factory methods it needs to call.%n" +
                        "This is the only way to map values in formats like Json, XML or YAML" +
                        " to specific parameters in factory methods.%n " +
                        "In order to fix this, make sure that the '-parameters'" +
                        " compile option is set in your build system and IDE.",
                className, methodName
        );
        return new CodeNeedsToBeCompiledWithParameterNamesException(message);
    }
}
