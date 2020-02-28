/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.processing.factories.collections;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionInformation {
    final Function<List<Object>, Collection<Object>> mapper;
    private final Class<?> type;

    public static Map<Class<?>, CollectionInformation> collectionInformations() {
        return Stream.of(
                collectionInformation(List.class, objects -> objects),
                collectionInformation(Collection.class, objects -> objects),
                collectionInformation(LinkedList.class, LinkedList::new),
                collectionInformation(Set.class, HashSet::new),
                collectionInformation(HashSet.class, HashSet::new),
                collectionInformation(ArrayList.class, ArrayList::new),
                collectionInformation(AbstractCollection.class, ArrayList::new),
                collectionInformation(AbstractList.class, ArrayList::new),
                collectionInformation(AbstractSequentialList.class, LinkedList::new),
                collectionInformation(LinkedHashSet.class, LinkedHashSet::new),
                collectionInformation(CopyOnWriteArraySet.class, CopyOnWriteArraySet::new),
                collectionInformation(CopyOnWriteArrayList.class, CopyOnWriteArrayList::new),
                collectionInformation(TreeSet.class, TreeSet::new),
                collectionInformation(Vector.class, Vector::new),
                collectionInformation(Stack.class, objects -> {
                    final Stack<Object> stack = new Stack<>();
                    stack.addAll(objects);
                    return stack;
                })
        )
                .collect(toMap(
                        collectionInformation -> collectionInformation.type,
                        collectionInformation -> collectionInformation
                ));
    }

    public static CollectionInformation collectionInformation(final Class<?> type,
                                                              final Function<List<Object>, Collection<Object>> mapper) {
        return new CollectionInformation(mapper, type);
    }
}
