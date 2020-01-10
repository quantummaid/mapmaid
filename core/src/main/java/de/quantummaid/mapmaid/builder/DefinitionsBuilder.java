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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.builder.contextlog.BuildContextLog;
import de.quantummaid.mapmaid.builder.detection.Detector;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.quantummaid.mapmaid.builder.DefinitionsCollector.definitionsCollector;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.serializationOnly;
import static de.quantummaid.mapmaid.mapper.definitions.Definitions.definitions;
import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefinitionsBuilder {
    private final DefinitionsCollector<TypeDeserializer> deserializers = definitionsCollector("deserializer");
    private final DefinitionsCollector<TypeSerializer> serializers = definitionsCollector("serializer");

    private final BuildContextLog contextLog;
    private final Detector detector;

    public static DefinitionsBuilder definitionsBuilder(final Detector detector,
                                                        final BuildContextLog contextLog) {
        return new DefinitionsBuilder(contextLog.stepInto(DefinitionsBuilder.class), detector);
    }

    public void addSerializer(final ResolvedType type,
                              final TypeSerializer serializer) {
        this.serializers.add(type, serializer);
    }

    public void addDeserializer(final ResolvedType type,
                                final TypeDeserializer deserializer) {
        this.deserializers.add(type, deserializer);
    }

    public void resolveRecursively(final Detector detector) {
        this.serializers.values().forEach(serializer -> diveIntoSerializerChildren(serializer, detector, this.contextLog));
        this.deserializers.values().forEach(deserializer -> diveIntoDeserializerChildren(deserializer, detector, this.contextLog));
    }

    private void recurseDeserializers(final ResolvedType type,
                                      final Detector detector,
                                      final BuildContextLog contextLog) {
        if (this.deserializers.isPresent(type)) {
            return;
        }
        detector.detect(type, RequiredCapabilities.deserializationOnly(), contextLog).ifPresent(definition -> {
            contextLog.log(type, "added because it is a dependency");
            definition.deserializer().ifPresent(deserializer -> {
                this.deserializers.add(type, deserializer);
                diveIntoDeserializerChildren(deserializer, detector, contextLog);
            });
        });
    }

    private void diveIntoDeserializerChildren(final TypeDeserializer deserializer,
                                              final Detector detector,
                                              final BuildContextLog contextLog) {
        deserializer.requiredTypes().forEach(requiredType -> recurseDeserializers(requiredType, detector, contextLog.stepInto(requiredType.assignableType())));
    }

    private void recurseSerializers(final ResolvedType type,
                                    final Detector detector,
                                    final BuildContextLog contextLog) {
        if (this.serializers.isPresent(type)) {
            return;
        }
        detector.detect(type, serializationOnly(), contextLog).ifPresent(definition -> {
            contextLog.log(type, "added because it is a dependency");
            definition.serializer().ifPresent(serializer -> {
                this.serializers.add(type, serializer);
                diveIntoSerializerChildren(serializer, detector, contextLog);
            });
        });
    }

    private void diveIntoSerializerChildren(final TypeSerializer serializer,
                                            final Detector detector,
                                            final BuildContextLog contextLog) {
        serializer.requiredTypes().forEach(requiredType ->
                recurseSerializers(requiredType, detector, contextLog.stepInto(requiredType.assignableType())));
    }

    public Definitions build(final Map<Definition, RequiredCapabilities> partialRequirements) {
        final Set<ResolvedType> allTypes = new HashSet<>();
        allTypes.addAll(this.deserializers.keys());
        allTypes.addAll(this.serializers.keys());

        final Map<ResolvedType, Definition> definitions = allTypes.stream()
                .map(this::definitionForType)
                .collect(toMap(Definition::type, definition -> definition));
        return definitions(this.contextLog, partialRequirements, definitions);
    }

    private Definition definitionForType(final ResolvedType type) {
        final TypeDeserializer deserializer = this.deserializers.get(type).orElse(null);
        final TypeSerializer serializer = this.serializers.get(type).orElse(null);

        if (deserializer != null || serializer != null) {
            return generalDefinition(type, serializer, deserializer);
        }

        throw new UnsupportedOperationException("It is not clear what type of definition to assign to " + type.description());
    }
}
