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

package de.quantummaid.mapmaid.builder.customtypes.serializedobject;

public interface Deserializer05<X, A, B, C, D, E> extends InvocableDeserializer<X> {
    X deserialize(A field1, B field2, C field3, D field4, E field5);

    @SuppressWarnings("unchecked")
    @Override
    default X invoke(final Object[] arguments) {
        return deserialize((A) arguments[0], (B) arguments[1], (C) arguments[2], (D) arguments[3], (E) arguments[4]);
    }
}
