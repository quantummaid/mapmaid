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
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirement.primaryRequirement;
import static de.quantummaid.mapmaid.builder.resolving.requirements.DetectionRequirement.secondaryRequirement;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionRequirementReasons {
    private final Map<RequirementName, DetectionRequirement> detectionRequirements;

    public static DetectionRequirementReasons empty(final List<RequirementName> primaryRequirements,
                                                    final List<RequirementName> secondaryRequirements) {
        final Map<RequirementName, DetectionRequirement> requirementMap = new LinkedHashMap<>();
        primaryRequirements.forEach(name -> {
            final DetectionRequirement requirement = primaryRequirement(name);
            requirementMap.put(name, requirement);
        });
        secondaryRequirements.forEach(name -> {
            final DetectionRequirement requirement = secondaryRequirement(name);
            requirementMap.put(name, requirement);
        });
        return new DetectionRequirementReasons(requirementMap);
    }

    public DetectionRequirementReasons addReason(final RequirementName requirement, final Reason reason) {
        return reduce(requirement, detectionRequirement -> detectionRequirement.addReason(reason));
    }

    public DetectionRequirementReasons removeReason(final Reason reason) {
        final Map<RequirementName, DetectionRequirement> newRequirements = detectionRequirements.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().removeReason(reason)));
        return new DetectionRequirementReasons(newRequirements);
    }

    private DetectionRequirementReasons reduce(final RequirementName requirement,
                                               final Function<DetectionRequirement, DetectionRequirement> reducer) {
        final Map<RequirementName, DetectionRequirement> newRequirements = new LinkedHashMap<>(detectionRequirements);
        final DetectionRequirement detectionRequirement = newRequirements.get(requirement);
        final DetectionRequirement changedRequirement = reducer.apply(detectionRequirement);
        newRequirements.put(requirement, changedRequirement);
        return new DetectionRequirementReasons(newRequirements);
    }

    public boolean hasChanged(final DetectionRequirementReasons old) {
        final Map<RequirementName, Boolean> currentDetectionRequirements = currentRequirements();
        final Map<RequirementName, Boolean> oldDetectionRequirements = old.currentRequirements();
        return !currentDetectionRequirements.equals(oldDetectionRequirements);
    }

    public Map<RequirementName, Boolean> currentRequirements() {
        return detectionRequirements.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().isRequired()));
    }

    public boolean isUnreasoned() {
        return detectionRequirements.values().stream()
                .filter(DetectionRequirement::isPrimary)
                .noneMatch(DetectionRequirement::isRequired);
    }

    public List<Reason> reasonsFor(final RequirementName requirementName) {
        final DetectionRequirement requirement = detectionRequirements.get(requirementName);
        return requirement.reasons();
    }

    public String summary() {
        return detectionRequirements.values().stream()
                .map(detectionRequirement -> detectionRequirement.name().value() + ": " + detectionRequirement.numberOfReasons())
                .collect(joining(", "));
    }
}
