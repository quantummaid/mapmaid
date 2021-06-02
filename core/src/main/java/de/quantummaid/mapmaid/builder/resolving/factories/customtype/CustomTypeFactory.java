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

package de.quantummaid.mapmaid.builder.resolving.factories.customtype;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.builder.resolving.framework.Context;
import de.quantummaid.mapmaid.builder.resolving.framework.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.builder.resolving.framework.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.framework.states.StatefulDefinition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.disambiguationResult;
import static de.quantummaid.mapmaid.builder.resolving.framework.states.detected.Unreasoned.unreasoned;
import static java.lang.String.format;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomTypeFactory implements StateFactory<MapMaidTypeScannerResult> {
    private final CustomType<?> customType;
    private final RequiredCapabilities capabilities;

    public static CustomTypeFactory customTypeFactory(final CustomType<?> customType,
                                                      final RequiredCapabilities capabilities) {
        return new CustomTypeFactory(customType, capabilities);
    }

    @Override
    public Optional<StatefulDefinition<MapMaidTypeScannerResult>> create(final TypeIdentifier type,
                                                                         final Context<MapMaidTypeScannerResult> context) {
        final TypeIdentifier typeIdentifier = customType.type();
        if (!typeIdentifier.equals(type)) {
            return Optional.empty();
        }
        final StatefulDefinition<MapMaidTypeScannerResult> statefulDefinition = unreasoned(context);
        final TypeSerializer serializer;
        if (capabilities.hasSerialization()) {
            serializer = customType.serializer()
                    .orElseThrow(() -> new IllegalArgumentException(format(
                            "serializer is missing for type '%s'", typeIdentifier.description())));
        } else {
            serializer = null;
        }
        final TypeDeserializer deserializer;
        if (capabilities.hasDeserialization()) {
            deserializer = customType.deserializer()
                    .orElseThrow(() -> new IllegalArgumentException(format(
                            "deserializer is missing for type '%s'", typeIdentifier.description())));
        } else {
            deserializer = null;
        }
        context.setManuallyConfiguredResult(result(disambiguationResult(serializer, deserializer), typeIdentifier));
        return Optional.of(statefulDefinition);

    }
}
