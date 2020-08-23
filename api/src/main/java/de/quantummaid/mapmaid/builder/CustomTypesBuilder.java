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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.RequiredCapabilities;
import de.quantummaid.mapmaid.customtypes.CustomType;
import de.quantummaid.mapmaid.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.customtypes.DuplexType;
import de.quantummaid.mapmaid.customtypes.SerializationOnlyType;

import static de.quantummaid.mapmaid.RequiredCapabilities.*;

public interface CustomTypesBuilder<T extends CustomTypesBuilder<T>> {

    default <X> T serializing(final SerializationOnlyType<X> customType) {
        return serializing((CustomType<X>) customType);
    }

    default <X> T serializing(final CustomType<X> customType) {
        return withCustomType(serialization(), customType);
    }

    default <X> T deserializing(final DeserializationOnlyType<X> customType) {
        return deserializing((CustomType<X>) customType);
    }

    default <X> T deserializing(final CustomType<X> customType) {
        return withCustomType(deserialization(), customType);
    }

    default <X> T serializingAndDeserializing(final DuplexType<X> customType) {
        return serializingAndDeserializing((CustomType<X>) customType);
    }

    default <X> T serializingAndDeserializing(final CustomType<X> customType) {
        return withCustomType(duplex(), customType);
    }

    <X> T withCustomType(RequiredCapabilities capabilities, CustomType<X> customType);
}
