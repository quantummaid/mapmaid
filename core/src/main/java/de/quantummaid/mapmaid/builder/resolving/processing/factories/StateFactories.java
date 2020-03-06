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

package de.quantummaid.mapmaid.builder.resolving.processing.factories;

import de.quantummaid.mapmaid.builder.resolving.Context;
import de.quantummaid.mapmaid.builder.resolving.states.StatefulDefinition;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.builder.resolving.processing.factories.UndetectedFactory.undetectedFactory;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.collections.ArrayCollectionDefinitionFactory.arrayFactory;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.collections.NativeJavaCollectionDefinitionFactory.nativeJavaCollectionsFactory;
import static de.quantummaid.mapmaid.builder.resolving.processing.factories.primitives.BuiltInPrimitivesFactory.builtInPrimitivesFactory;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StateFactories {
    private final List<StateFactory> stateFactories;

    public static StateFactories defaultStateFactories() {
        final List<StateFactory> stateFactories = List.of(
                builtInPrimitivesFactory(),
                arrayFactory(),
                nativeJavaCollectionsFactory(),
                undetectedFactory()
        );
        return new StateFactories(stateFactories);
    }

    public StatefulDefinition createState(final TypeIdentifier type,
                                          final Context context) {
        for (final StateFactory stateFactory : this.stateFactories) {
            final Optional<StatefulDefinition> statefulDefinition = stateFactory.create(type, context);
            if (statefulDefinition.isPresent()) {
                return statefulDefinition.get();
            }
        }
        throw new UnsupportedOperationException("This should never happen");
    }
}
