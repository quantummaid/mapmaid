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

package de.quantummaid.mapmaid.builder.resolving.processing.signals;

import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddManualSerializerSignal implements Signal {
    private final TypeIdentifier target;
    private final TypeSerializer serializer;

    public static Signal addManualSerializer(final TypeIdentifier target, final TypeSerializer serializer) {
        return new AddManualSerializerSignal(target, serializer);
    }

    @Override
    public String description() {
        return "add manual serializer";
    }

    @Override
    public StatefulDefinition handleState(final StatefulDefinition definition) {
        return definition.changeRequirements(current -> current.setManuallyConfiguredSerializer(serializer));
    }

    @Override
    public Optional<TypeIdentifier> target() {
        return Optional.of(target);
    }
}