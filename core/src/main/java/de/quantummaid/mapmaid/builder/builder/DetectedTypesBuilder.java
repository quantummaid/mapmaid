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
import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.reflectmaid.GenericType;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;
import static de.quantummaid.reflectmaid.GenericType.genericType;

public interface DetectedTypesBuilder {

    default MapMaidBuilder serializing(final Class<?> type) {
        return serializing(genericType(type));
    }

    default MapMaidBuilder serializing(final GenericType<?> genericType) {
        return withType(genericType, serialization());
    }

    default MapMaidBuilder deserializing(final Class<?> type) {
        return deserializing(genericType(type));
    }

    default MapMaidBuilder deserializing(final GenericType<?> genericType) {
        return withType(genericType, deserialization());
    }

    default MapMaidBuilder serializingAndDeserializing(final Class<?> type) {
        return serializingAndDeserializing(genericType(type));
    }

    default MapMaidBuilder serializingAndDeserializing(final GenericType<?> genericType) {
        return withType(genericType, duplex());
    }

    MapMaidBuilder withType(GenericType<?> type, RequiredCapabilities capabilities);
}
