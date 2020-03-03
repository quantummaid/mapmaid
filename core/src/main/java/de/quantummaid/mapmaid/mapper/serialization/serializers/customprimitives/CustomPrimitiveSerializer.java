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

package de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives;

import de.quantummaid.mapmaid.mapper.serialization.SerializationCallback;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.tracker.SerializationTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;

import static java.util.Collections.emptyList;

public interface CustomPrimitiveSerializer extends TypeSerializer {

    static CustomPrimitiveSerializer constantSerializer(final String constant) {
        return new CustomPrimitiveSerializer() {
            @Override
            public Object serialize(final Object object) {
                return constant;
            }

            @Override
            public String description() {
                return constant;
            }
        };
    }

    @Override
    default List<ResolvedType> requiredTypes() {
        return emptyList();
    }

    Object serialize(Object object);

    @Override
    default Universal serialize(final Object object,
                                final SerializationCallback callback,
                                final SerializationTracker tracker,
                                final CustomPrimitiveMappings customPrimitiveMappings) {
        final Object serialized = serialize(object);
        return customPrimitiveMappings.toUniversal(serialized);
    }

    default Class<?> baseType() {
        return String.class;
    }

    @Override
    default String classification() {
        return "Custom Primitive";
    }
}
