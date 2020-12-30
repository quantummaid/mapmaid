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

package de.quantummaid.mapmaid.builder.resolving.processing.log;

import de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirementReasons;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.String.format;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggedState {
    private final TypeIdentifier type;
    private final Class<?> state;
    private final DetectionRequirementReasons detectionRequirementReasons;

    public static LoggedState loggedState(final TypeIdentifier type,
                                          final Class<?> state,
                                          final DetectionRequirementReasons detectionRequirementReasons) {
        validateNotNull(type, "type");
        validateNotNull(state, "state");
        validateNotNull(detectionRequirementReasons, "detectionRequirementReasons");
        return new LoggedState(type, state, detectionRequirementReasons);
    }

    public String getType() {
        return type.description();
    }

    public String getState() {
        return state.getSimpleName();
    }

    public String getDetectionRequirementReasons() {
        return detectionRequirementReasons.summary();
    }

    String dump() {
        return format(
                "%s: %s (%s)",
                type.simpleDescription(),
                state,
                detectionRequirementReasons.summary()
        );
    }
}
