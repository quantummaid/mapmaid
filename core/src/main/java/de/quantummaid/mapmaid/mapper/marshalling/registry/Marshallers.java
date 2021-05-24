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

package de.quantummaid.mapmaid.mapper.marshalling.registry;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.registry.modifier.MarshallingModifier;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Marshallers {
    private final MarshallerRegistry marshallerRegistry;
    private final List<MarshallingModifier> modifiers;

    public static Marshallers marshallers(final MarshallerRegistry marshallerRegistry,
                                          final List<MarshallingModifier> modifiers) {
        return new Marshallers(marshallerRegistry, modifiers);
    }

    public <T> T marshal(final MarshallingType<T> marshallingType, final Object object) {
        final Marshaller<T> marshaller = marshallerRegistry.getForType(marshallingType);
        final Object modified = modify(object);
        return doMarshal(marshaller, modified);
    }

    private Object modify(final Object input) {
        Object result = input;
        for (final MarshallingModifier modifier : modifiers) {
            result = modifier.modify(result);
        }
        return result;
    }

    private <T> T doMarshal(final Marshaller<T> marshaller, final Object object) {
        try {
            return marshaller.marshal(object);
        } catch (final Exception e) {
            throw UnexpectedExceptionThrownDuringMarshallingException.fromException(e, object);
        }
    }

    public MarshallerRegistry marshallerRegistry() {
        return marshallerRegistry;
    }
}
