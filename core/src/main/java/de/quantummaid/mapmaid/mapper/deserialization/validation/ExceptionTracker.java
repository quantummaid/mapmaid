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

package de.quantummaid.mapmaid.mapper.deserialization.validation;

import de.quantummaid.mapmaid.mapper.universal.Universal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionTracker {
    private final TrackingPosition position;
    private final ValidationMappings validationMappings;
    private final Universal originalInput;

    private final List<ValidationError> validationErrors = new LinkedList<>();
    private final List<ExceptionTracker> children = new LinkedList<>();

    public static ExceptionTracker emptyTracker(final Universal originalInput, final ValidationMappings validationMappings) {
        return initializedTracker(TrackingPosition.empty(), validationMappings, originalInput);
    }

    private static ExceptionTracker initializedTracker(final TrackingPosition position,
                                                       final ValidationMappings validationMappings,
                                                       final Universal originalInput) {
        return new ExceptionTracker(position, validationMappings, originalInput);
    }

    private static Throwable resolveThrowable(final Throwable raw) {
        if (raw instanceof InvocationTargetException) {
            return ((InvocationTargetException) raw).getTargetException();
        } else {
            return raw;
        }
    }

    public void track(final Throwable e, final String messageProvidingDebugInformation) {
        final Throwable resolvedThrowable = resolveThrowable(e);
        final ExceptionMappingList<Throwable> exceptionMapping = this.validationMappings.get(resolvedThrowable.getClass())
                .orElseThrow(() -> UnrecognizedExceptionOccurredException.fromException(
                        messageProvidingDebugInformation, this.position, resolvedThrowable, this.originalInput.toNativeJava()));
        final List<ValidationError> mapped = exceptionMapping.map(resolvedThrowable, this.position.render());
        this.validationErrors.addAll(mapped);
    }

    public ExceptionTracker stepInto(final String name) {
        final ExceptionTracker exceptionTracker = initializedTracker(
                this.position.next(name), this.validationMappings, this.originalInput);
        this.children.add(exceptionTracker);
        return exceptionTracker;
    }

    public ExceptionTracker stepIntoArray(final int index) {
        return this.stepInto(format("[%d]", index));
    }

    public ValidationResult validationResult() {
        return ValidationResult.validationResult(this.allValidationErrors());
    }

    private List<ValidationError> allValidationErrors() {
        final List<ValidationError> allValidationErrors = new LinkedList<>(this.validationErrors);
        this.children.forEach(child -> allValidationErrors.addAll(child.allValidationErrors()));
        return allValidationErrors;
    }

    public String getPosition() {
        return this.position.render();
    }
}
