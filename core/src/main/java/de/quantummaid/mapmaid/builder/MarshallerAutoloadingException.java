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

import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;

import java.util.Collection;
import java.util.stream.Collectors;

public final class MarshallerAutoloadingException extends RuntimeException {
    private MarshallerAutoloadingException(final String message) {
        super(message);
    }

    public static MarshallerAutoloadingException conflictingMarshallersForTypes(
            final MarshallingType marshallingType,
            final Collection<MarshallerAndUnmarshaller> marshallerAndUnmarshallers) {

        final String conflictingMarshallers = marshallerAndUnmarshallers.stream()
                .map(marshallerAndUnmarshaller -> marshallerAndUnmarshaller.getClass().getName())
                .collect(Collectors.joining(", ", "[", "]"));

        throw new MarshallerAutoloadingException(
                String.format("conflicting implementations for marshalling type '%s': %s",
                        marshallingType.internalValueForMapping(), conflictingMarshallers));
    }
}
