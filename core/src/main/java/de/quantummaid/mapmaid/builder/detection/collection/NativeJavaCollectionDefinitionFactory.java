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

package de.quantummaid.mapmaid.builder.detection.collection;

import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.builder.detection.DefinitionFactory;
import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.CollectionDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.collections.CollectionSerializer;
import de.quantummaid.mapmaid.shared.types.ClassType;
import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

import static de.quantummaid.mapmaid.mapper.definitions.GeneralDefinition.generalDefinition;
import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.collections.ListCollectionDeserializer.listDeserializer;
import static de.quantummaid.mapmaid.mapper.serialization.serializers.collections.ListCollectionSerializer.listSerializer;
import static de.quantummaid.mapmaid.shared.types.TypeVariableName.typeVariableName;
import static de.quantummaid.mapmaid.shared.types.unresolved.UnresolvedType.unresolvedType;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class NativeJavaCollectionDefinitionFactory implements DefinitionFactory {
    private static final Map<Class<?>, Function<ResolvedType, Definition>> FACTORIES = new HashMap<>(20);

    static {
        addFactory(List.class, objects -> objects);
        addFactory(Collection.class, objects -> objects);
        addFactory(LinkedList.class, LinkedList::new);
        addFactory(Set.class, HashSet::new);
        addFactory(HashSet.class, HashSet::new);
        addFactory(ArrayList.class, ArrayList::new);
        addFactory(AbstractCollection.class, ArrayList::new);
        addFactory(AbstractList.class, ArrayList::new);
        addFactory(AbstractSequentialList.class, LinkedList::new);
        addFactory(LinkedHashSet.class, LinkedHashSet::new);
        addFactory(CopyOnWriteArraySet.class, CopyOnWriteArraySet::new);
        addFactory(CopyOnWriteArrayList.class, CopyOnWriteArrayList::new);
        addFactory(TreeSet.class, TreeSet::new);
        addFactory(Vector.class, Vector::new);
        addFactory(Stack.class, objects -> {
            final Stack<Object> stack = new Stack<>();
            stack.addAll(objects);
            return stack;
        });
    }

    public static DefinitionFactory nativeJavaCollectionsFactory() {
        return new NativeJavaCollectionDefinitionFactory();
    }

    @Override
    public Optional<Definition> analyze(final ResolvedType type,
                                        final RequiredCapabilities capabilities) {
        if (!FACTORIES.containsKey(type.assignableType())) {
            return empty();
        }
        if (type.typeParameters().size() != 1) {
            throw new UnsupportedOperationException(format(
                    "This should never happen. A collection of type '%s' has more than one type parameter", type.description()));
        }
        final ResolvedType genericType = ((ClassType) type).typeParameter(typeVariableName("E"));
        final Definition definition = FACTORIES.get(type.assignableType()).apply(genericType);
        return of(definition);
    }

    @SuppressWarnings("rawtypes")
    private static void addFactory(final Class<? extends Collection> collectionType,
                                   final Function<List<Object>, Collection<Object>> mapper) {
        final Function<ResolvedType, Definition> factory = genericType -> {
            final CollectionSerializer serializer = listSerializer(genericType);
            final CollectionDeserializer deserializer = listDeserializer(genericType, mapper);
            final ResolvedType fullType = unresolvedType(collectionType).resolve(genericType);
            return generalDefinition(fullType, serializer, deserializer);
        };
        FACTORIES.put(collectionType, factory);
    }
}
