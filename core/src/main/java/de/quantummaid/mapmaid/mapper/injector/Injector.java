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

package de.quantummaid.mapmaid.mapper.injector;

import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalPrimitive;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static de.quantummaid.mapmaid.Collection.smallList;
import static de.quantummaid.mapmaid.mapper.injector.NamedDirectInjection.namedDirectInjection;
import static de.quantummaid.mapmaid.mapper.injector.PropertyName.propertyName;
import static de.quantummaid.mapmaid.mapper.injector.TypedDirectInjection.typedDirectInjection;
import static de.quantummaid.mapmaid.mapper.injector.UniversalInjection.universalInjection;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Injector {
    private final List<UniversalInjection> universalInjections = smallList();
    private final List<NamedDirectInjection> namedDirectInjections = smallList();
    private final List<TypedDirectInjection> typedDirectInjections = smallList();

    static Injector empty() {
        return new Injector();
    }

    public Injector put(final String propertyName, final String value) {
        this.universalInjections.add(universalInjection(propertyName(propertyName), UniversalPrimitive.universalPrimitive(value)));
        return this;
    }

    public Injector put(final String propertyName, final Object instance) {
        this.namedDirectInjections.add(namedDirectInjection(propertyName(propertyName), instance));
        return this;
    }

    public Injector put(final Object instance) {
        return put(ClassType.typeOfObject(instance), instance);
    }

    public Injector put(final Class<?> type, final Object instance) {
        return put(ClassType.fromClassWithoutGenerics(type), instance);
    }

    public Injector put(final ResolvedType type, final Object instance) {
        this.typedDirectInjections.add(typedDirectInjection(type, instance));
        return this;
    }

    public Optional<Universal> getUniversalInjectionFor(final String position) {
        final PropertyName propertyName = propertyName(position);
        return this.universalInjections.stream()
                .filter(injection -> injection.propertyName().equals(propertyName))
                .findFirst()
                .map(UniversalInjection::value);
    }

    public Optional<Object> getDirectInjectionForPropertyPath(final String position) {
        final PropertyName propertyName = propertyName(position);
        return this.namedDirectInjections.stream()
                .filter(injection -> injection.propertyName().equals(propertyName))
                .findFirst()
                .map(NamedDirectInjection::value);
    }

    public Optional<Object> getDirectInjectionForType(final TypeIdentifier typeIdentifier) {
        if (typeIdentifier.isVirtual()) {
            return Optional.empty();
        }
        final ResolvedType type = typeIdentifier.getRealType();
        final Class<?> clazz = type.assignableType();
        return this.typedDirectInjections.stream()
                .filter(injection -> clazz.isAssignableFrom(injection.type().assignableType()))
                .findFirst()
                .map(TypedDirectInjection::value);
    }
}
