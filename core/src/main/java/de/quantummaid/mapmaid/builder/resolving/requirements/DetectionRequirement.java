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

package de.quantummaid.mapmaid.builder.resolving.requirements;

import de.quantummaid.mapmaid.debug.Reason;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionRequirement {
    private final RequirementName name;
    private final List<Reason> reasons;
    private final boolean primary;

    public static DetectionRequirement primaryRequirement(final RequirementName name) {
        return detectionRequirement(name, true);
    }

    public static DetectionRequirement secondaryRequirement(final RequirementName name) {
        return detectionRequirement(name, false);
    }

    private static DetectionRequirement detectionRequirement(final RequirementName name,
                                                            final boolean primary) {
        return new DetectionRequirement(name, new ArrayList<>(), primary);
    }

    public boolean isRequired() {
        return !reasons.isEmpty();
    }

    public int numberOfReasons() {
        return reasons.size();
    }

    public List<Reason> reasons() {
        return reasons;
    }

    public RequirementName name() {
        return name;
    }

    public DetectionRequirement addReason(final Reason reason) {
        final List<Reason> newReasons = new ArrayList<>(reasons);
        newReasons.add(reason);
        return new DetectionRequirement(name, newReasons, primary);
    }

    public DetectionRequirement removeReason(final Reason reason) {
        final List<Reason> newReasons = new ArrayList<>(reasons);
        newReasons.remove(reason);
        return new DetectionRequirement(name, newReasons, primary);
    }

    public boolean isPrimary() {
        return primary;
    }
}
