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

package de.quantummaid.mapmaid.specs.examples;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.reflectmaid.GenericType;

public final class Util {

    private Util() {
    }

    public static <T> void registerCustomPrimitive(final RequiredCapabilities capabilities,
                                                   final MapMaidBuilder builder,
                                                   final Class<T> type,
                                                   final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                   final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        final GenericType<T> genericType = GenericType.genericType(type);
        registerCustomPrimitive(capabilities, builder, genericType, serializer, deserializer);
    }

    public static <T> void registerCustomPrimitive(final RequiredCapabilities capabilities,
                                                   final MapMaidBuilder builder,
                                                   final GenericType<T> type,
                                                   final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                   final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        if (capabilities.hasDeserialization() && capabilities.hasSerialization()) {
            builder.serializingAndDeserializingCustomPrimitive(type, serializer, deserializer);
        } else if (capabilities.hasSerialization()) {
            builder.serializingCustomPrimitive(type, serializer);
        } else if (capabilities.hasDeserialization()) {
            builder.deserializingCustomPrimitive(type, deserializer);
        }
    }

    public static <T> void registerIntBasedCustomPrimitive(final RequiredCapabilities capabilities,
                                                           final MapMaidBuilder builder,
                                                           final Class<T> type,
                                                           final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                           final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        if (capabilities.hasDeserialization() && capabilities.hasSerialization()) {
            builder.serializingAndDeserializingIntBasedCustomPrimitive(type, serializer, deserializer);
        } else if (capabilities.hasSerialization()) {
            builder.serializingIntBasedCustomPrimitive(type, serializer);
        } else if (capabilities.hasDeserialization()) {
            builder.deserializingIntBasedCustomPrimitive(type, deserializer);
        }
    }
}
