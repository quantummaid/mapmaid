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

package de.quantummaid.mapmaid.mapper.schema;

import de.quantummaid.mapmaid.mapper.universal.Universal;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

public final class PrimitiveSchemaMappings {
    private static final Map<Class<?>, PrimitiveSchema> SCHEMATA = schemata();

    private PrimitiveSchemaMappings() {
    }

    public static Universal mapPrimitiveToSchema(final Class<?> primitive) {
        final PrimitiveSchema schema = SCHEMATA.get(primitive);
        if (schema == null) {
            throw new UnsupportedOperationException(format("Cannot map type '%s' to an OpenAPI type", primitive.getSimpleName()));
        }
        return schema.schema;
    }

    private static Map<Class<?>, PrimitiveSchema> schemata() {
        final Map<Class<?>, PrimitiveSchema> schemata = new LinkedHashMap<>();
        schemata.put(String.class, PrimitiveSchema.primitiveSchema("string"));
        schemata.put(int.class, PrimitiveSchema.primitiveSchema("integer", "int32"));
        schemata.put(Integer.class, PrimitiveSchema.primitiveSchema("integer", "int32"));
        schemata.put(long.class, PrimitiveSchema.primitiveSchema("integer", "int64"));
        schemata.put(Long.class, PrimitiveSchema.primitiveSchema("integer", "int64"));
        schemata.put(short.class, PrimitiveSchema.primitiveSchema("integer"));
        schemata.put(Short.class, PrimitiveSchema.primitiveSchema("integer"));
        schemata.put(byte.class, PrimitiveSchema.primitiveSchema("integer"));
        schemata.put(Byte.class, PrimitiveSchema.primitiveSchema("integer"));
        schemata.put(double.class, PrimitiveSchema.primitiveSchema("number", "double"));
        schemata.put(Double.class, PrimitiveSchema.primitiveSchema("number", "double"));
        schemata.put(float.class, PrimitiveSchema.primitiveSchema("number", "float"));
        schemata.put(Float.class, PrimitiveSchema.primitiveSchema("number", "float"));
        schemata.put(boolean.class, PrimitiveSchema.primitiveSchema("boolean"));
        schemata.put(Boolean.class, PrimitiveSchema.primitiveSchema("boolean"));
        return schemata;
    }

}
