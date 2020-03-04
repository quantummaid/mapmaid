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

package de.quantummaid.mapmaid.debug;

public final class Lingo {
    public static final String SERIALIZATION_ONLY = "serialization-only";
    public static final String DESERIALIZATION_ONLY = "deserialization-only";
    public static final String DUPLEX = "duplex";
    public static final String DISAMBIGUATOR = "disambiguator";

    private Lingo() {
    }

    public static String mode(final boolean serializer, final boolean deserializer) {
        if (deserializer && !serializer) {
            return DESERIALIZATION_ONLY;
        } else if (serializer && !deserializer) {
            return SERIALIZATION_ONLY;
        } else if (serializer && deserializer) {
            return DUPLEX;
        } else {
            throw new UnsupportedOperationException("This should never happen. " +
                    "There is no mode that has neither serialization and deserialization.");
        }
    }
}
