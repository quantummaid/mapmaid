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

package de.quantummaid.mapmaid.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AComplexTypeWithDifferentCollections {
    public final Collection<ANumber> collection;
    public final LinkedList<ANumber> linkedList; // NOSONAR
    public final Set<ANumber> set; // NOSONAR
    public final HashSet<ANumber> hashSet; // NOSONAR
    public final ArrayList<ANumber> arrayList; // NOSONAR
    public final AbstractCollection<ANumber> abstractCollection; // NOSONAR
    public final AbstractList<ANumber> abstractList; // NOSONAR
    public final AbstractSequentialList<ANumber> abstractSequentialList; // NOSONAR
    public final CopyOnWriteArrayList<ANumber> copyOnWriteArrayList; // NOSONAR
    public final CopyOnWriteArraySet<ANumber> copyOnWriteArraySet; // NOSONAR
    public final LinkedHashSet<ANumber> linkedHashSet; // NOSONAR
    public final Stack<ANumber> stack; // NOSONAR
    public final TreeSet<ANumber> treeSet; // NOSONAR
    public final Vector<ANumber> vector; // NOSONAR

    public static AComplexTypeWithDifferentCollections deserialize(final Collection<ANumber> collection, // NOSONAR
                                                                   final LinkedList<ANumber> linkedList, // NOSONAR
                                                                   final Set<ANumber> set, // NOSONAR
                                                                   final HashSet<ANumber> hashSet, // NOSONAR
                                                                   final ArrayList<ANumber> arrayList, // NOSONAR
                                                                   final AbstractCollection<ANumber> abstractCollection, // NOSONAR
                                                                   final AbstractList<ANumber> abstractList, // NOSONAR
                                                                   final AbstractSequentialList<ANumber> abstractSequentialList, // NOSONAR
                                                                   final CopyOnWriteArrayList<ANumber> copyOnWriteArrayList, // NOSONAR
                                                                   final CopyOnWriteArraySet<ANumber> copyOnWriteArraySet, // NOSONAR
                                                                   final LinkedHashSet<ANumber> linkedHashSet, // NOSONAR
                                                                   final Stack<ANumber> stack, // NOSONAR
                                                                   final TreeSet<ANumber> treeSet, // NOSONAR
                                                                   final Vector<ANumber> vector) { // NOSONAR
        return new AComplexTypeWithDifferentCollections(collection, // NOSONAR
                linkedList, set, hashSet, arrayList, abstractCollection, // NOSONAR
                abstractList, abstractSequentialList, copyOnWriteArrayList, // NOSONAR
                copyOnWriteArraySet, linkedHashSet, stack, treeSet, vector); // NOSONAR
    }
}
