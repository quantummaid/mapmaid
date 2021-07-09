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

package de.quantummaid.mapmaid.builder.resolving.factories.maps;

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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.universal.UniversalObject.universalObject;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapSerializer implements TypeSerializer {
    private final TypeIdentifier keyType;
    private final TypeIdentifier valueType;

    public static MapSerializer mapSerializer(final TypeIdentifier keyType,
                                              final TypeIdentifier valueType) {
        validateNotNull(keyType, "keyType");
        validateNotNull(valueType, "valueType");
        return new MapSerializer(keyType, valueType);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return List.of(keyType, valueType);
    }

    @Override
    public Universal serialize(final Object object,
                               final SerializationCallback callback,
                               final SerializationTracker tracker,
                               final CustomPrimitiveMappings customPrimitiveMappings,
                               final DebugInformation debugInformation) {
        final Map<?, ?> map = (Map<?, ?>) object;
        final Map<String, Universal> resultMap = new LinkedHashMap<>();
        map.forEach((key, value) -> {
            final UniversalString serializedKey = (UniversalString) callback.serializeDefinition(keyType, key, tracker);
            final Universal serializedValue = callback.serializeDefinition(valueType, value, tracker);
            resultMap.put(serializedKey.toNativeStringValue(), serializedValue);
        });
        return universalObject(resultMap);
    }

    @Override
    public String description() {
        return "serializing as map";
    }
}
