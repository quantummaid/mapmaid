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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.registry.UnmarshallerRegistry;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

import static de.quantummaid.mapmaid.mapper.deserialization.InternalUnmarshallingException.internalUnmarshallingException;
import static de.quantummaid.mapmaid.mapper.universal.Universal.fromNativeJava;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("java:S1452")
final class Unmarshallers {
    private final UnmarshallerRegistry unmarshallers;

    static Unmarshallers unmarshallers(final UnmarshallerRegistry unmarshallers) {
        validateNotNull(unmarshallers, "unmarshallers");
        return new Unmarshallers(unmarshallers);
    }

    <M> Universal unmarshall(final M input,
                             final MarshallingType<M> marshallingType) {
        final Unmarshaller<M> unmarshaller = unmarshallers.getForType(marshallingType);
        if (!unmarshaller.acceptsNull()) {
            validateNotNull(input, "input");
        }
        try {
            final Object unmarshalled = unmarshaller.unmarshal(input);
            return fromNativeJava(unmarshalled);
        } catch (final Exception e) {
            throw internalUnmarshallingException(input, e);
        }
    }

    Set<MarshallingType<?>> supportedMarshallingTypes() {
        return unmarshallers.supportedTypes();
    }
}
