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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.mapper.marshalling.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalCollection;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.mapper.universal.UniversalPrimitive;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static de.quantummaid.mapmaid.mapper.deserialization.UnmarshallingException.unmarshallingException;
import static de.quantummaid.mapmaid.mapper.universal.UniversalCollection.universalCollectionFromNativeList;
import static de.quantummaid.mapmaid.mapper.universal.UniversalNull.universalNull;
import static de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObjectFromNativeMap;
import static de.quantummaid.mapmaid.mapper.universal.UniversalPrimitive.universalPrimitive;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class Unmarshallers {
    private static final Pattern PATTERN = Pattern.compile("\"");

    private final MarshallerRegistry<Unmarshaller> unmarshallers;

    static Unmarshallers unmarshallers(final MarshallerRegistry<Unmarshaller> unmarshallers) {
        validateNotNull(unmarshallers, "unmarshallers");
        return new Unmarshallers(unmarshallers);
    }

    @SuppressWarnings("unchecked")
    Universal unmarshalTo(final Class<? extends Universal> universalType,
                          final String input,
                          final MarshallingType marshallingType) {
        validateNotNull(input, "input");
        if (input.isEmpty()) {
            return universalNull();
        }
        final Unmarshaller unmarshaller = this.unmarshallers.getForType(marshallingType);

        final String trimmedInput = input.trim();
        if (universalType == UniversalCollection.class) {
            try {
                return universalCollectionFromNativeList(unmarshaller.unmarshal(trimmedInput, List.class));
            } catch (final Exception e) {
                throw unmarshallingException("list", input, e);
            }
        } else if (universalType == UniversalObject.class) {
            try {
                return universalObjectFromNativeMap(unmarshaller.unmarshal(trimmedInput, Map.class));
            } catch (final Exception e) {
                throw unmarshallingException("map", input, e);
            }
        } else if (UniversalPrimitive.class.isAssignableFrom(universalType)) {
            return universalPrimitive(PATTERN.matcher(trimmedInput).replaceAll(""));
        } else {
            throw new UnsupportedOperationException("This should never happen. Unsupported universal type: " + universalType.getName());
        }
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> unmarshalToMap(final String input,
                                       final MarshallingType marshallingType) {
        validateNotNull(input, "input");
        validateNotNull(marshallingType, "marshallingType");
        return (Map<String, Object>) unmarshalTo(UniversalObject.class, input, marshallingType)
                .toNativeJava();
    }

    Set<MarshallingType> supportedMarshallingTypes() {
        return this.unmarshallers.supportedTypes();
    }
}
