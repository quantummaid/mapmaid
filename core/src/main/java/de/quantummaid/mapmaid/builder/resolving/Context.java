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

package de.quantummaid.mapmaid.builder.resolving;

import de.quantummaid.mapmaid.builder.resolving.processing.signals.Signal;
import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirementReasons;
import de.quantummaid.mapmaid.builder.resolving.requirements.RequirementsReducer;
import de.quantummaid.mapmaid.debug.RequiredAction;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.builder.resolving.Requirements.*;
import static de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirementReasons.empty;
import static de.quantummaid.mapmaid.debug.RequiredAction.*;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Context<T> {
    private final Consumer<Signal<T>> dispatcher;
    private final TypeIdentifier type;
    private T detectionResult;
    private T manuallyConfiguredResult;
    private DetectionRequirementReasons detectionRequirementReasons;
    private final ScanInformationBuilder scanInformationBuilder;

    public static <T> Context<T> emptyContext(final Consumer<Signal<T>> dispatcher,
                                              final TypeIdentifier type) {
        final ScanInformationBuilder scanInformationBuilder = ScanInformationBuilder.scanInformationBuilder(type);
        final Context<T> context = new Context<>(dispatcher, type, scanInformationBuilder);
        context.detectionRequirementReasons = empty(
                List.of(SERIALIZATION, DESERIALIZATION),
                List.of(OBJECT_ENFORCING, INLINED_PRIMITIVE)
        );
        return context;
    }

    public TypeIdentifier type() {
        return this.type;
    }

    public void dispatch(final Signal<T> signal) {
        this.dispatcher.accept(signal);
    }

    public ScanInformationBuilder scanInformationBuilder() {
        return this.scanInformationBuilder;
    }

    public void setDetectionResult(final T detectionResult) {
        this.detectionResult = detectionResult;
    }

    public Optional<T> detectionResult() {
        return Optional.ofNullable(detectionResult);
    }

    public void setManuallyConfiguredResult(final T manuallyConfiguredResult) {
        this.manuallyConfiguredResult = manuallyConfiguredResult;
    }

    public Optional<T> manuallyConfiguredResult() {
        return Optional.ofNullable(manuallyConfiguredResult);
    }

    public DetectionRequirementReasons detectionRequirements() {
        return detectionRequirementReasons;
    }

    public RequiredAction changeRequirements(final RequirementsReducer reducer) {
        final DetectionRequirementReasons oldReaons = detectionRequirementReasons;
        final DetectionRequirementReasons newReasons = reducer.reduce(oldReaons);
        this.detectionRequirementReasons = newReasons;
        if (detectionRequirementReasons.isUnreasoned()) {
            return unreasoned();
        }
        if (oldReaons.hasChanged(newReasons)) {
            return requirementsChanged();
        } else {
            return nothingChanged();
        }
    }
}
