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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.symmetry.serializedobject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public final class Combinations {

    private Combinations() {
    }

    public static <T> List<List<T>> allCombinations(final List<T> requiredElements,
                                                    final List<T> optionalElements) {
        final List<List<T>> powerSetOfOptionalElements = recursivePowerSet(optionalElements);
        return powerSetOfOptionalElements.stream()
                .map(optionalSubset -> combine(requiredElements, optionalSubset))
                .collect(toList());
    }

    private static <T> List<List<T>> recursivePowerSet(final List<T> list) {
        if (list.isEmpty()) {
            return singletonList(list);
        }
        final T element = list.iterator().next();
        final List<T> withoutElement = withoutElement(list, element);
        final List<List<T>> powerSetSubSetWithoutElement = recursivePowerSet(withoutElement);
        final List<List<T>> powerSetSubSetWithElement = addToAll(powerSetSubSetWithoutElement, element);
        return combine(powerSetSubSetWithoutElement, powerSetSubSetWithElement);
    }

    private static <T> List<T> combine(final List<T> a, final List<T> b) {
        final int size = a.size() + b.size();
        final List<T> combination = new ArrayList<>(size);
        combination.addAll(a);
        combination.addAll(b);
        return combination;
    }

    private static <T> List<List<T>> addToAll(final List<List<T>> lists,
                                              final T element) {
        return lists.stream()
                .map(list -> withElement(list, element))
                .collect(toList());
    }

    private static <T> List<T> withoutElement(final List<T> list,
                                              final T element) {
        final List<T> copy = new ArrayList<>(list);
        copy.remove(element);
        return copy;
    }

    private static <T> List<T> withElement(final List<T> list,
                                           final T element) {
        final List<T> copy = new ArrayList<>(list);
        copy.add(element);
        return copy;
    }
}
