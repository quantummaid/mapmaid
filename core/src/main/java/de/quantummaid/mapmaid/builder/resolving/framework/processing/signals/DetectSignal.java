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

package de.quantummaid.mapmaid.builder.resolving.framework.processing.signals;

import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.states.Detector;
import de.quantummaid.mapmaid.builder.resolving.framework.states.RequirementsDescriber;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectSignal<T> implements Signal<T> {
    private final Detector<T> detector;
    private final RequirementsDescriber requirementsDescriber;

    public static <T> Signal<T> detect(final Detector<T> detector, final RequirementsDescriber requirementsDescriber) {
        return new DetectSignal<>(detector, requirementsDescriber);
    }

    @Override
    public StatefulDefinition<T> handleState(final StatefulDefinition<T> definition) {
        return definition.detect(detector, requirementsDescriber);
    }

    @Override
    public Optional<TypeIdentifier> target() {
        return Optional.empty();
    }

    @Override
    public String description() {
        return "detect";
    }
}
