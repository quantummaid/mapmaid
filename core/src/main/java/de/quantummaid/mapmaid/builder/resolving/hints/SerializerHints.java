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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;

public final class SerializerHints {

    public static Hint<SerializationField> preferGettersOverFields() {
        throw new UnsupportedOperationException();
    }

    public static Hint<SerializationField> preferFieldsOverGetters() {
        throw new UnsupportedOperationException();
    }

    public static Hint<SerializationField> doNotUseGetters() {
        throw new UnsupportedOperationException();
    }

    public static Hint<SerializationField> doNotUseFields() {
        throw new UnsupportedOperationException();
    }

    // TODO non-getter methods?

    public static Hint<SerializationField> fieldByMethod(final String methodName, final String fieldName) {
        throw new UnsupportedOperationException();
    }
}
