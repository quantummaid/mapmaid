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

package de.quantummaid.mapmaid.mapper.serialization.tracker;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.mapper.serialization.tracker.CircularReferenceException.circularReferenceException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializationTracker {
    private final List<TrackedObject> visitedObjects;

    public static SerializationTracker serializationTracker() {
        return new SerializationTracker(new LinkedList<>());
    }

    public SerializationTracker trackToProhibitCyclicReferences(final Object object) {
        NotNullValidator.validateNotNull(object, "object");
        final TrackedObject trackedObject = TrackedObject.trackedObject(object);
        if (this.visitedObjects.contains(trackedObject)) {
            throw circularReferenceException(object);
        }
        final List<TrackedObject> newVisitedObjects = new LinkedList<>(this.visitedObjects);
        newVisitedObjects.add(trackedObject);
        return new SerializationTracker(newVisitedObjects);
    }
}
