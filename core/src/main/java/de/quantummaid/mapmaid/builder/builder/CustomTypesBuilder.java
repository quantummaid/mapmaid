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
import de.quantummaid.mapmaid.builder.customtypes.CustomType;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;

import static de.quantummaid.mapmaid.builder.RequiredCapabilities.*;

public interface CustomTypesBuilder {

    default <T> MapMaidBuilder serializing(final SerializationOnlyType<T> customType) {
        return serializing((CustomType<T>) customType);
    }

    default <T> MapMaidBuilder serializing(final CustomType<T> customType) {
        return withCustomType(serialization(), customType);
    }

    default <T> MapMaidBuilder deserializing(final DeserializationOnlyType<T> customType) {
        return deserializing((CustomType<T>) customType);
    }

    default <T> MapMaidBuilder deserializing(final CustomType<T> customType) {
        return withCustomType(deserialization(), customType);
    }

    default <T> MapMaidBuilder serializingAndDeserializing(final DuplexType<T> customType) {
        return serializingAndDeserializing((CustomType<T>) customType);
    }

    default <T> MapMaidBuilder serializingAndDeserializing(final CustomType<T> customType) {
        return withCustomType(duplex(), customType);
    }

    <T> MapMaidBuilder withCustomType(RequiredCapabilities capabilities, CustomType<T> customType);
}
