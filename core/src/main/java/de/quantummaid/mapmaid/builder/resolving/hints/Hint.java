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

package de.quantummaid.mapmaid.builder.resolving.hints;

import de.quantummaid.mapmaid.shared.types.ResolvedType;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;


// TODO Disambiguator
public interface Hint<T> {
    void apply(final Bucket<T> bucket);

    default String id() {
        return UUID.randomUUID().toString(); // TODO
    }

    // TODO only unstriked to next hint
    static <T> Hint<T> concat(final Hint<T>... hints) {
        return bucket -> stream(hints).forEach(hint -> hint.apply(bucket));
    }

    static <T> Hint<T> onlyForType(final ResolvedType type, final Hint<T> hint) {
        return onlyFor(bucket -> bucket.id().equals(type), hint);
    }

    static <T> Hint<T> onlyFor(final Predicate<Bucket<T>> predicate,
                               final Hint<T> hint) {
        return bucket -> {
            if (!predicate.test(bucket)) {
                hint.apply(bucket);
            }
        };
    }

    static <T> Hint<T> choosing(final T choice) {
        return bucket -> {
            bucket.strikeAll("overwritten by user");
            bucket.add(singletonList(choice));
        };
    }

    static <T> Hint<T> prefering(final Predicate<T> predicate) {
        return bucket -> bucket.sorted(comparingInt(value -> {
            if (predicate.test(value)) {
                return 0;
            } else {
                return 1;
            }
        }));
    }

    static <T> Hint<T> preferringTypes(final Class<?>... types) {
        final List<Class<?>> list = asList(types);
        return prefering(value -> list.contains(value.getClass()));
    }

    static <T, U> Hint<T> preferringType(final Class<U> type, final Predicate<U> predicate) {
        return prefering(value -> {
            if (!(type.isInstance(value))) {
                return false;
            }
            return predicate.test((U) value);
        });
    }

    static <T, U> Hint<T> preferringType(final Class<U> type) {
        return preferringType(type, value -> true);
    }

    static <T> Hint<T> discriminatingTypes(final Class<?>... types) {
        final List<Class<?>> list = asList(types);
        return prefering(value -> !list.contains(value.getClass()));
    }

    // TODO
    static <T, U> Hint<T> discriminatingType(final Class<U> type, final Predicate<U> predicate) {
        return prefering(value -> {
            if (!(type.isInstance(value))) {
                return true;
            }
            return !predicate.test((U) value);
        });
    }

    static <T, U> Hint<T> discriminatingType(final Class<U> type) {
        return discriminatingType(type, value -> true);
    }
}
