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
import de.quantummaid.mapmaid.mapper.deserialization.DeserializerCallback;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalObject;
import de.quantummaid.mapmaid.mapper.universal.UniversalString;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.mapper.universal.UniversalString.universalString;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapDeserializer implements TypeDeserializer {
    private final TypeIdentifier keyType;
    private final TypeIdentifier valueType;

    public static MapDeserializer mapDeserializer(final TypeIdentifier keyType,
                                                  final TypeIdentifier valueType) {
        validateNotNull(keyType, "keyType");
        validateNotNull(valueType, "valueType");
        return new MapDeserializer(keyType, valueType);
    }

    @Override
    public List<TypeIdentifier> requiredTypes() {
        return List.of(keyType, valueType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final Universal input,
                             final ExceptionTracker exceptionTracker,
                             final Injector injector,
                             final DeserializerCallback callback,
                             final CustomPrimitiveMappings customPrimitiveMappings,
                             final TypeIdentifier typeIdentifier,
                             final DebugInformation debugInformation) {
        final UniversalObject universalObject = (UniversalObject) input;
        final Map<String, Universal> universalMap = universalObject.toUniversalMap();
        final Map<Object, Object> resultMap = new LinkedHashMap<>();
        universalMap.forEach((key, universalValue) -> {
            final UniversalString universalKey = universalString(key);
            final Object deserializedKey = callback.deserializeRecursive(universalKey, keyType, exceptionTracker, injector, debugInformation);
            final Object deserializedValue = callback.deserializeRecursive(universalValue, valueType, exceptionTracker.stepInto(key), injector, debugInformation);
            resultMap.put(deserializedKey, deserializedValue);
        });
        return (T) resultMap;
    }

    @Override
    public String description() {
        return "deserializing as map";
    }
}
