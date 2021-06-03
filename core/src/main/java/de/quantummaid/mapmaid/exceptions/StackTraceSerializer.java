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

package de.quantummaid.mapmaid.exceptions;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalString;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static de.quantummaid.mapmaid.mapper.universal.UniversalCollection.universalCollection;
import static de.quantummaid.mapmaid.mapper.universal.UniversalString.universalString;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StackTraceSerializer implements TypeSerializer {
    private final TypeIdentifier stackTraceType;
    private final int maxStackFrameCount;

    public static StackTraceSerializer stackTraceSerializer(final TypeIdentifier stackTraceType,
                                                            final int maxStackFrameCount) {
        return new StackTraceSerializer(stackTraceType, maxStackFrameCount);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return emptyList();
    }

    @Override
    public Universal serialize(final Object object,
                               final SerializationCallback callback,
                               final SerializationTracker tracker,
                               final CustomPrimitiveMappings customPrimitiveMappings,
                               final DebugInformation debugInformation) {
        final StackTraceElement[] stackTraceElementsArray = (StackTraceElement[]) object;
        final List<StackTraceElement> stackTraceElements = asList(stackTraceElementsArray);
        final int stackTraceLength = Integer.min(stackTraceElements.size(), maxStackFrameCount);
        final List<StackTraceElement> relevantElements = stackTraceElements.subList(0, stackTraceLength);
        final List<Universal> frames = relevantElements.stream()
                .map(StackTraceElement::toString)
                .map(UniversalString::universalString)
                .collect(toList());
        final List<Universal> mutableFrames = new ArrayList<>(frames);
        if (stackTraceElements.size() > frames.size()) {
            final int difference = stackTraceElements.size() - frames.size();
            mutableFrames.add(universalString("...[" + difference + " more]"));
        }
        return universalCollection(mutableFrames);
    }

    @Override
    public String description() {
        return null;
    }
}
