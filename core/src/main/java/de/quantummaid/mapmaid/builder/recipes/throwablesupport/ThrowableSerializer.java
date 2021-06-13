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

package de.quantummaid.mapmaid.builder.recipes.throwablesupport;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.universal.UniversalCollection.universalCollection;
import static de.quantummaid.mapmaid.mapper.universal.UniversalNull.universalNull;
import static de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObject;
import static de.quantummaid.mapmaid.mapper.universal.UniversalString.universalString;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ThrowableSerializer implements TypeSerializer {
    private final TypeIdentifier throwableType;
    private final TypeIdentifier stackTraceType;

    public static ThrowableSerializer throwableSerializer(final TypeIdentifier throwableType,
                                                          final TypeIdentifier stackTraceType) {
        return new ThrowableSerializer(throwableType, stackTraceType);
    }

    @Override
    public Universal serialize(final Object object,
                               final SerializationCallback callback,
                               final SerializationTracker tracker,
                               final CustomPrimitiveMappings customPrimitiveMappings,
                               final DebugInformation debugInformation) {
        final Throwable throwable = (Throwable) object;
        final Throwable cause = throwable.getCause();

        final Universal mappedCause;
        if (cause != null) {
            mappedCause = callback.serializeDefinition(throwableType, cause, tracker);
        } else {
            mappedCause = null;
        }
        final Throwable[] suppressed = throwable.getSuppressed();
        final Universal frames = callback.serializeDefinition(stackTraceType, throwable.getStackTrace(), tracker);
        return exceptionMap(
                throwable.getMessage(),
                throwable.getClass().getCanonicalName(),
                frames,
                mappedCause,
                stream(suppressed)
                        .map(it -> callback.serializeDefinition(throwableType, it, tracker))
                        .collect(toList())
        );
    }

    @Override
    public Universal serializeAlreadySeenObject(final Object object,
                                                final SerializationCallback callback,
                                                final SerializationTracker tracker,
                                                final CustomPrimitiveMappings customPrimitiveMappings,
                                                final DebugInformation debugInformation) {
        final Throwable throwable = (Throwable) object;
        return exceptionMap(
                throwable.getMessage(),
                throwable.getClass().getCanonicalName(),
                null,
                null,
                emptyList()
        );
    }

    private static UniversalObject exceptionMap(
            final String message,
            final String type,
            final Universal frames,
            final Universal cause,
            final List<Universal> suppressed
    ) {
        final Map<String, Universal> map = new LinkedHashMap<>(5);
        if (message == null) {
            map.put("message", universalNull());
        } else {
            map.put("message", universalString(message));
        }
        map.put("type", universalString(type));
        if (frames != null) {
            map.put("frames", frames);
        } else {
            map.put("note", universalString("cyclic reference"));
        }
        if (cause != null) {
            map.put("cause", cause);
        }
        if (!suppressed.isEmpty()) {
            map.put("suppressed", universalCollection(suppressed));
        }
        return universalObject(map);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return List.of(throwableType, stackTraceType);
    }

    @Override
    public String description() {
        return "exception";
    }
}
