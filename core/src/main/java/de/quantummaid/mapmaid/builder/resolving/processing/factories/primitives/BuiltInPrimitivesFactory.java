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

package de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives;

import de.quantummaid.mapmaid.builder.MapMaidConfiguration;
import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactoryResult.stateFactoryResult;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives.BuiltInPrimitiveDeserializer.builtInPrimitiveDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives.BuiltInPrimitiveSerializer.builtInPrimitiveSerializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualDeserializerSignal.addManualDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.processing.signals.AddManualSerializerSignal.addManualSerializer;
import static de.quantummaid.mapmaid.builder.resolving.states.detected.Unreasoned.unreasoned;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuiltInPrimitivesFactory implements StateFactory {

    public static BuiltInPrimitivesFactory builtInPrimitivesFactory() {
        return new BuiltInPrimitivesFactory();
    }

    @Override
    public Optional<StateFactoryResult> create(final TypeIdentifier type,
                                               final Context context,
                                               final MapMaidConfiguration configuration) {
        if (type.isVirtual()) {
            return empty();
        }

        final ResolvedType realType = type.getRealType();
        final Class<?> assignableType = realType.assignableType();
        if (!CUSTOM_PRIMITIVE_MAPPINGS.isPrimitiveType(assignableType)) {
            return empty();
        }
        final CustomPrimitiveSerializer serializer = builtInPrimitiveSerializer(assignableType);
        final CustomPrimitiveDeserializer deserializer = builtInPrimitiveDeserializer(assignableType);
        return Optional.of(stateFactoryResult(unreasoned(context), List.of(
                addManualSerializer(type, serializer),
                addManualDeserializer(type, deserializer)
        )));
    }
}
