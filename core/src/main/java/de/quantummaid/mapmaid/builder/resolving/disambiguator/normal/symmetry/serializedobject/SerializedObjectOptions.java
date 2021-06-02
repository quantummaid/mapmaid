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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject;

import de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult;
import de.quantummaid.mapmaid.builder.detection.serializedobject.SerializationFieldOptions;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguationContext;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences;
import de.quantummaid.mapmaid.builder.resolving.framework.requirements.DetectionRequirements;
import de.quantummaid.mapmaid.debug.ScanInformationBuilder;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.framework.states.DetectionResult.failure;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializedObjectOptions {
    private final SerializationFieldOptions serializationFieldOptions;
    private final List<TypeDeserializer> deserializers;

    public static SerializedObjectOptions serializedObjectOptions(final SerializationFieldOptions serializationFieldOptions,
                                                                  final List<TypeDeserializer> deserializers) {
        return new SerializedObjectOptions(serializationFieldOptions, deserializers);
    }

    public List<TypeDeserializer> deserializers() {
        return this.deserializers;
    }

    public SerializationFieldOptions serializationFieldOptions() {
        return this.serializationFieldOptions;
    }

    public DetectionResult<TypeSerializer> determineSerializer(final ResolvedType containingType,
                                                               final Preferences<SerializationField, DisambiguationContext> preferences,
                                                               final ScanInformationBuilder scanInformationBuilder,
                                                               final DetectionRequirements detectionRequirements,
                                                               final DisambiguationContext context) {
        if (this.serializationFieldOptions == null) {
            throw new UnsupportedOperationException("This should never happen");
        }
        if (this.serializationFieldOptions.isEmpty()) {
            return failure("No serialization fields");
        }
        return this.serializationFieldOptions.instantiateAll()
                .flatMap(instantiation -> instantiation.instantiate(
                        containingType,
                        preferences,
                        scanInformationBuilder,
                        detectionRequirements,
                        context)
                );
    }
}
