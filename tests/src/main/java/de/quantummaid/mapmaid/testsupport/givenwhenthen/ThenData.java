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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThenData {
    private Object deserializationResult;
    private Object serializationResult;
    private Exception exception;
    private DebugInformation debugInformation;
    private String schema;

    private Set<MarshallingType> supportedMarshallingTypes;
    private Set<MarshallingType> supportedUnmarshallingTypes;

    public static ThenData thenData() {
        return new ThenData();
    }

    public ThenData withDeserializationResult(final Object deserializationResult) {
        this.deserializationResult = deserializationResult;
        return this;
    }

    public Object getDeserializationResult() {
        return this.deserializationResult;
    }

    public ThenData withSerializationResult(final Object serializationResult) {
        this.serializationResult = serializationResult;
        return this;
    }

    public Object getSerializationResult() {
        return this.serializationResult;
    }

    public ThenData withException(final Exception exception) {
        this.exception = exception;
        return this;
    }

    public Exception getException() {
        return this.exception;
    }

    public ThenData withDebugInformation(final DebugInformation debugInformation) {
        this.debugInformation = debugInformation;
        return this;
    }

    public DebugInformation getDebugInformation() {
        return this.debugInformation;
    }

    public ThenData withSchema(final String schema) {
        this.schema = schema;
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public ThenData withSupportedMarshallingTypes(
            final Set<MarshallingType> marshallingTypes,
            final Set<MarshallingType> unmarshallingTypes) {
        this.supportedMarshallingTypes = unmodifiableSet(marshallingTypes);
        this.supportedUnmarshallingTypes = unmodifiableSet(unmarshallingTypes);
        return this;
    }

    public Set<MarshallingType> getSupportedMarshallingTypes() {
        return unmodifiableSet(supportedMarshallingTypes);
    }

    public Set<MarshallingType> getSupportedUnmarshallingTypes() {
        return unmodifiableSet(supportedUnmarshallingTypes);
    }
}
