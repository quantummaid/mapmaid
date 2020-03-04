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

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.builder.resolving.states.fixed.resolved.FixedResolvedDuplex;
import de.quantummaid.mapmaid.builder.resolving.processing.factories.StateFactory;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives.PrimitiveInformation.primitiveInformations;
import static java.util.Optional.empty;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuiltInPrimitivesFactory implements StateFactory {
    private final Map<ResolvedType, PrimitiveInformation> definitions = primitiveInformations();

    public static BuiltInPrimitivesFactory builtInPrimitivesFactory() {
        return new BuiltInPrimitivesFactory();
    }

    @Override
    public Optional<StatefulDefinition> create(final ResolvedType type, final Context context) {
        if (!this.definitions.containsKey(type)) {
            return empty();
        }
        final PrimitiveInformation primitiveInformation = this.definitions.get(type);
        final CustomPrimitiveSerializer customPrimitiveSerializer = new CustomPrimitiveSerializer() {
            @Override
            public Object serialize(final Object object) {
                if (object != null) {
                    return String.valueOf(object);
                } else {
                    return null;
                }
            }

            @Override
            public String description() {
                return "toString()";
            }
        };
        context.setSerializer(customPrimitiveSerializer);

        final CustomPrimitiveDeserializer customPrimitiveDeserializer = new CustomPrimitiveDeserializer() {
            @Override
            public Object deserialize(final Object value) throws Exception {
                return primitiveInformation.deserializer.apply((String) value);
            }

            @Override
            public String description() {
                return primitiveInformation.description;
            }
        };
        context.setDeserializer(customPrimitiveDeserializer);

        return Optional.of(FixedResolvedDuplex.fixedResolvedDuplex(context));
    }
}
