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

package de.quantummaid.mapmaid.builder.builder.customobjects;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface CustomObjectsBuilder {

    default <T> MapMaidBuilder serializingCustomObject(final Class<T> type,
                                                       final SerializationOnlyBuilder<T> builder) {
        final GenericType<T> genericType = genericType(type);
        return serializingCustomObject(genericType, builder);
    }

    <T> MapMaidBuilder serializingCustomObject(GenericType<T> type, SerializationOnlyBuilder<T> builder);

    default <T> MapMaidBuilder deserializingCustomObject(final Class<T> type,
                                                         final DeserializationOnlyBuilder<T> builder) {
        final GenericType<T> genericType = genericType(type);
        return deserializingCustomObject(genericType, builder);
    }

    <T> MapMaidBuilder deserializingCustomObject(GenericType<T> type, DeserializationOnlyBuilder<T> builder);

    default <T> MapMaidBuilder serializingAndDeserializingCustomObject(final Class<T> type,
                                                                       final DuplexBuilder<T> builder) {
        final GenericType<T> genericType = genericType(type);
        return serializingAndDeserializingCustomObject(genericType, builder);
    }

    <T> MapMaidBuilder serializingAndDeserializingCustomObject(GenericType<T> type, DuplexBuilder<T> builder);
}

