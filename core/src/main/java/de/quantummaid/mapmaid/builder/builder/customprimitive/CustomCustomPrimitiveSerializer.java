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

package de.quantummaid.mapmaid.builder.builder.customprimitive;

import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;

public interface CustomCustomPrimitiveSerializer<T, B> {
    B serialize(T object);

    default TypeSerializer toTypeSerializer(final Class<B> baseType) {
        final TypeSerializer customPrimitiveSerializer = new CustomPrimitiveSerializer() {
            @SuppressWarnings("unchecked")
            @Override
            public Object serialize(final Object object) {
                return CustomCustomPrimitiveSerializer.this.serialize((T) object);
            }

            @Override
            public Class<?> baseType() {
                return baseType;
            }

            @Override
            public String description() {
                return "custom provided";
            }
        };
        return customPrimitiveSerializer;
    }
}
