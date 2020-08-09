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
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DetectionRequirementReasons {
    public final List<Reason> serializationReasons;
    public final List<Reason> deserializationReasons;
    private final List<Reason> objectEnforcingReasons;
    private final List<Reason> inlinedPrimitiveReasons;

    public static DetectionRequirementReasons empty() {
        return new DetectionRequirementReasons(emptyList(), emptyList(), emptyList(), emptyList());
    }

    public DetectionRequirementReasons addSerialization(final Reason reason) {
        final List<Reason> newSerializationReasons = new ArrayList<>(this.serializationReasons);
        newSerializationReasons.add(reason);
        return new DetectionRequirementReasons(
                newSerializationReasons,
                this.deserializationReasons,
                this.objectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public DetectionRequirementReasons removeSerialization(final Reason reason) {
        final List<Reason> newSerializationReasons = new ArrayList<>(this.serializationReasons);
        newSerializationReasons.remove(reason);
        return new DetectionRequirementReasons(
                newSerializationReasons,
                this.deserializationReasons,
                this.objectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public DetectionRequirementReasons addDeserialization(final Reason reason) {
        final List<Reason> newDeserializationReasons = new ArrayList<>(this.deserializationReasons);
        newDeserializationReasons.add(reason);
        return new DetectionRequirementReasons(
                this.serializationReasons,
                newDeserializationReasons,
                this.objectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public DetectionRequirementReasons removeDeserialization(final Reason reason) {
        final List<Reason> newDeserializationReasons = new ArrayList<>(this.deserializationReasons);
        newDeserializationReasons.remove(reason);
        return new DetectionRequirementReasons(
                this.serializationReasons,
                newDeserializationReasons,
                this.objectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public DetectionRequirementReasons enforceObject(final Reason reason) {
        final List<Reason> newObjectEnforcingReasons = new ArrayList<>(this.objectEnforcingReasons);
        newObjectEnforcingReasons.add(reason);
        return new DetectionRequirementReasons(
                this.serializationReasons,
                this.deserializationReasons,
                newObjectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public DetectionRequirementReasons removeObjectEnforcingReason(final Reason reason) {
        final List<Reason> newObjectEnforcingReasons = new ArrayList<>(this.objectEnforcingReasons);
        newObjectEnforcingReasons.remove(reason);
        return new DetectionRequirementReasons(
                this.serializationReasons,
                this.deserializationReasons,
                newObjectEnforcingReasons,
                this.inlinedPrimitiveReasons
        );
    }

    public boolean hasChanged(final DetectionRequirementReasons old) {
        final DetectionRequirements currentDetectionRequirements = detectionRequirements();
        final DetectionRequirements oldDetectionRequirements = old.detectionRequirements();
        return !currentDetectionRequirements.equals(oldDetectionRequirements);
    }

    public DetectionRequirements detectionRequirements() {
        return DetectionRequirements.detectionRequirements(
                !this.serializationReasons.isEmpty(),
                !this.deserializationReasons.isEmpty(),
                !this.objectEnforcingReasons.isEmpty(),
                !this.inlinedPrimitiveReasons.isEmpty()
        );
    }
}
