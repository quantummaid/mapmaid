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

package de.quantummaid.mapmaid.testsupport.domain.valid;

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
    public final LinkedList<ANumber> linkedList;
    public final Set<ANumber> set;
    public final HashSet<ANumber> hashSet;
    public final ArrayList<ANumber> arrayList;
    public final AbstractCollection<ANumber> abstractCollection;
    public final AbstractList<ANumber> abstractList;
    public final AbstractSequentialList<ANumber> abstractSequentialList;
    public final CopyOnWriteArrayList<ANumber> copyOnWriteArrayList;
    public final CopyOnWriteArraySet<ANumber> copyOnWriteArraySet;
    public final LinkedHashSet<ANumber> linkedHashSet;
    public final Stack<ANumber> stack;
    public final TreeSet<ANumber> treeSet;
    public final Vector<ANumber> vector;

    public static AComplexTypeWithDifferentCollections deserialize(final Collection<ANumber> collection,
                                                                   final LinkedList<ANumber> linkedList,
                                                                   final Set<ANumber> set,
                                                                   final HashSet<ANumber> hashSet,
                                                                   final ArrayList<ANumber> arrayList,
                                                                   final AbstractCollection<ANumber> abstractCollection,
                                                                   final AbstractList<ANumber> abstractList,
                                                                   final AbstractSequentialList<ANumber> abstractSequentialList,
                                                                   final CopyOnWriteArrayList<ANumber> copyOnWriteArrayList,
                                                                   final CopyOnWriteArraySet<ANumber> copyOnWriteArraySet,
                                                                   final LinkedHashSet<ANumber> linkedHashSet,
                                                                   final Stack<ANumber> stack,
                                                                   final TreeSet<ANumber> treeSet,
                                                                   final Vector<ANumber> vector) {
        return new AComplexTypeWithDifferentCollections(collection,
                linkedList, set, hashSet, arrayList, abstractCollection,
                abstractList, abstractSequentialList, copyOnWriteArrayList,
                copyOnWriteArraySet, linkedHashSet, stack, treeSet, vector);
    }
}
