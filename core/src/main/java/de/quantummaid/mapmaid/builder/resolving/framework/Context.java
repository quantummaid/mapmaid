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

package de.quantummaid.mapmaid.builder.resolving.framework;

import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequiredAction;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.builder.resolving.framework.states.RequiredAction.*;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Context<T> {
    private final Consumer<Signal<T>> dispatcher;
    private final TypeIdentifier type;
    private DetectionResult<T> detectionResult;
    private T manuallyConfiguredResult;
    private DetectionRequirements detectionRequirements;

    public static <T> Context<T> emptyContext(final Consumer<Signal<T>> dispatcher,
                                              final TypeIdentifier type,
                                              final DetectionRequirements detectionRequirements) {
        final Context<T> context = new Context<>(dispatcher, type);
        context.detectionRequirements = detectionRequirements;
        return context;
    }

    public TypeIdentifier type() {
        return this.type;
    }

    public void dispatch(final Signal<T> signal) {
        this.dispatcher.accept(signal);
    }

    public void setDetectionResult(final DetectionResult<T> detectionResult) {
        this.detectionResult = detectionResult;
    }

    public DetectionResult<T> detectionResult() {
        return detectionResult;
    }

    public void setManuallyConfiguredResult(final T manuallyConfiguredResult) {
        this.manuallyConfiguredResult = manuallyConfiguredResult;
    }

    public Optional<T> manuallyConfiguredResult() {
        return Optional.ofNullable(manuallyConfiguredResult);
    }

    public DetectionRequirements detectionRequirements() {
        return detectionRequirements;
    }

    public RequiredAction changeRequirements(final RequirementsReducer reducer) {
        final DetectionRequirements oldReaons = detectionRequirements;
        final DetectionRequirements newReasons = reducer.reduce(oldReaons);
        this.detectionRequirements = newReasons;
        if (detectionRequirements.isUnreasoned()) {
            return unreasoned();
        }
        if (oldReaons.hasChanged(newReasons)) {
            return requirementsChanged();
        } else {
            return nothingChanged();
        }
    }
}
