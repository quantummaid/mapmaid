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

package de.quantummaid.mapmaid.builder.builder;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.builder.customtypes.customprimitive.CustomCustomPrimitiveSerializer;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface CustomPrimitivesBuilder {

    default <T> MapMaidBuilder serializingCustomPrimitive(final Class<T> type,
                                                          final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return serializingStringBasedCustomPrimitive(type, serializer);
    }

    default <T> MapMaidBuilder serializingCustomPrimitive(final GenericType<T> type,
                                                          final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return serializingStringBasedCustomPrimitive(type, serializer);
    }

    default <T> MapMaidBuilder deserializingCustomPrimitive(final Class<T> type,
                                                            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return deserializingStringBasedCustomPrimitive(type, deserializer);
    }

    default <T> MapMaidBuilder deserializingCustomPrimitive(final GenericType<T> type,
                                                            final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return deserializingStringBasedCustomPrimitive(type, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingCustomPrimitive(final Class<T> type,
                                                                          final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                                          final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return serializingAndDeserializingStringBasedCustomPrimitive(type, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingCustomPrimitive(final GenericType<T> type,
                                                                          final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                                          final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return serializingAndDeserializingStringBasedCustomPrimitive(type, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingStringBasedCustomPrimitive(final Class<T> type,
                                                                     final CustomCustomPrimitiveSerializer<T, String> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingStringBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingStringBasedCustomPrimitive(final GenericType<T> type,
                                                                     final CustomCustomPrimitiveSerializer<T, String> serializer) {
        return serializingCustomPrimitive(type, String.class, serializer);
    }

    default <T> MapMaidBuilder deserializingStringBasedCustomPrimitive(final Class<T> type,
                                                                       final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingStringBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingStringBasedCustomPrimitive(final GenericType<T> type,
                                                                       final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return deserializingCustomPrimitive(type, String.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingStringBasedCustomPrimitive(final Class<T> type,
                                                                                     final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                                                     final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingStringBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingStringBasedCustomPrimitive(final GenericType<T> type,
                                                                                     final CustomCustomPrimitiveSerializer<T, String> serializer,
                                                                                     final CustomCustomPrimitiveDeserializer<T, String> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, String.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingLongBasedCustomPrimitive(final Class<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Long> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingLongBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingLongBasedCustomPrimitive(final GenericType<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Long> serializer) {
        return serializingCustomPrimitive(type, Long.class, serializer);
    }

    default <T> MapMaidBuilder deserializingLongBasedCustomPrimitive(final Class<T> type,
                                                                     final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingLongBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingLongBasedCustomPrimitive(final GenericType<T> type,
                                                                     final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return deserializingCustomPrimitive(type, Long.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingLongBasedCustomPrimitive(final Class<T> type,
                                                                                   final CustomCustomPrimitiveSerializer<T, Long> serializer,
                                                                                   final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingLongBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingLongBasedCustomPrimitive(final GenericType<T> type,
                                                                                   final CustomCustomPrimitiveSerializer<T, Long> serializer,
                                                                                   final CustomCustomPrimitiveDeserializer<T, Long> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Long.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingIntBasedCustomPrimitive(final Class<T> type,
                                                                  final CustomCustomPrimitiveSerializer<T, Integer> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingIntBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingIntBasedCustomPrimitive(final GenericType<T> type,
                                                                  final CustomCustomPrimitiveSerializer<T, Integer> serializer) {
        return serializingCustomPrimitive(type, Integer.class, serializer);
    }

    default <T> MapMaidBuilder deserializingIntBasedCustomPrimitive(final Class<T> type,
                                                                    final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingIntBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingIntBasedCustomPrimitive(final GenericType<T> type,
                                                                    final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return deserializingCustomPrimitive(type, Integer.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingIntBasedCustomPrimitive(final Class<T> type,
                                                                                  final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                                                  final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingIntBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingIntBasedCustomPrimitive(final GenericType<T> type,
                                                                                  final CustomCustomPrimitiveSerializer<T, Integer> serializer,
                                                                                  final CustomCustomPrimitiveDeserializer<T, Integer> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Integer.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingShortBasedCustomPrimitive(final Class<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Short> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingShortBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingShortBasedCustomPrimitive(final GenericType<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Short> serializer) {
        return serializingCustomPrimitive(type, Short.class, serializer);
    }

    default <T> MapMaidBuilder deserializingShortBasedCustomPrimitive(final Class<T> type,
                                                                      final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingShortBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingShortBasedCustomPrimitive(final GenericType<T> type,
                                                                      final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return deserializingCustomPrimitive(type, Short.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingShortBasedCustomPrimitive(final Class<T> type,
                                                                                    final CustomCustomPrimitiveSerializer<T, Short> serializer,
                                                                                    final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingShortBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingShortBasedCustomPrimitive(final GenericType<T> type,
                                                                                    final CustomCustomPrimitiveSerializer<T, Short> serializer,
                                                                                    final CustomCustomPrimitiveDeserializer<T, Short> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Short.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingByteBasedCustomPrimitive(final Class<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Byte> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingByteBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingByteBasedCustomPrimitive(final GenericType<T> type,
                                                                   final CustomCustomPrimitiveSerializer<T, Byte> serializer) {
        return serializingCustomPrimitive(type, Byte.class, serializer);
    }

    default <T> MapMaidBuilder deserializingByteBasedCustomPrimitive(final Class<T> type,
                                                                     final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingByteBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingByteBasedCustomPrimitive(final GenericType<T> type,
                                                                     final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return deserializingCustomPrimitive(type, Byte.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingByteBasedCustomPrimitive(final Class<T> type,
                                                                                   final CustomCustomPrimitiveSerializer<T, Byte> serializer,
                                                                                   final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingByteBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingByteBasedCustomPrimitive(final GenericType<T> type,
                                                                                   final CustomCustomPrimitiveSerializer<T, Byte> serializer,
                                                                                   final CustomCustomPrimitiveDeserializer<T, Byte> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Byte.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingFloatBasedCustomPrimitive(final Class<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Float> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingFloatBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingFloatBasedCustomPrimitive(final GenericType<T> type,
                                                                    final CustomCustomPrimitiveSerializer<T, Float> serializer) {
        return serializingCustomPrimitive(type, Float.class, serializer);
    }

    default <T> MapMaidBuilder deserializingFloatBasedCustomPrimitive(final Class<T> type,
                                                                      final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingFloatBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingFloatBasedCustomPrimitive(final GenericType<T> type,
                                                                      final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return deserializingCustomPrimitive(type, Float.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingFloatBasedCustomPrimitive(final Class<T> type,
                                                                                    final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                                                    final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingFloatBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingFloatBasedCustomPrimitive(final GenericType<T> type,
                                                                                    final CustomCustomPrimitiveSerializer<T, Float> serializer,
                                                                                    final CustomCustomPrimitiveDeserializer<T, Float> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Float.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingDoubleBasedCustomPrimitive(final Class<T> type,
                                                                     final CustomCustomPrimitiveSerializer<T, Double> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingDoubleBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingDoubleBasedCustomPrimitive(final GenericType<T> type,
                                                                     final CustomCustomPrimitiveSerializer<T, Double> serializer) {
        return serializingCustomPrimitive(type, Double.class, serializer);
    }

    default <T> MapMaidBuilder deserializingDoubleBasedCustomPrimitive(final Class<T> type,
                                                                       final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingDoubleBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingDoubleBasedCustomPrimitive(final GenericType<T> type,
                                                                       final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return deserializingCustomPrimitive(type, Double.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingDoubleBasedCustomPrimitive(final Class<T> type,
                                                                                     final CustomCustomPrimitiveSerializer<T, Double> serializer,
                                                                                     final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingDoubleBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingDoubleBasedCustomPrimitive(final GenericType<T> type,
                                                                                     final CustomCustomPrimitiveSerializer<T, Double> serializer,
                                                                                     final CustomCustomPrimitiveDeserializer<T, Double> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Double.class, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingBooleanBasedCustomPrimitive(final Class<T> type,
                                                                      final CustomCustomPrimitiveSerializer<T, Boolean> serializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingBooleanBasedCustomPrimitive(genericType, serializer);
    }

    default <T> MapMaidBuilder serializingBooleanBasedCustomPrimitive(final GenericType<T> type,
                                                                      final CustomCustomPrimitiveSerializer<T, Boolean> serializer) {
        return serializingCustomPrimitive(type, Boolean.class, serializer);
    }

    default <T> MapMaidBuilder deserializingBooleanBasedCustomPrimitive(final Class<T> type,
                                                                        final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return deserializingBooleanBasedCustomPrimitive(genericType, deserializer);
    }

    default <T> MapMaidBuilder deserializingBooleanBasedCustomPrimitive(final GenericType<T> type,
                                                                        final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return deserializingCustomPrimitive(type, Boolean.class, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingBooleanBasedCustomPrimitive(final Class<T> type,
                                                                                      final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                                      final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingBooleanBasedCustomPrimitive(genericType, serializer, deserializer);
    }

    default <T> MapMaidBuilder serializingAndDeserializingBooleanBasedCustomPrimitive(final GenericType<T> type,
                                                                                      final CustomCustomPrimitiveSerializer<T, Boolean> serializer,
                                                                                      final CustomCustomPrimitiveDeserializer<T, Boolean> deserializer) {
        return serializingAndDeserializingCustomPrimitive(type, Boolean.class, serializer, deserializer);
    }

    <T, B> MapMaidBuilder serializingCustomPrimitive(
            GenericType<T> type,
            Class<B> baseType,
            CustomCustomPrimitiveSerializer<T, B> serializer);

    <T, B> MapMaidBuilder deserializingCustomPrimitive(
            GenericType<T> type,
            Class<B> baseType,
            CustomCustomPrimitiveDeserializer<T, B> deserializer);

    <T, B> MapMaidBuilder serializingAndDeserializingCustomPrimitive(
            GenericType<T> type,
            Class<B> baseType,
            CustomCustomPrimitiveSerializer<T, B> serializer,
            CustomCustomPrimitiveDeserializer<T, B> deserializer);
}
