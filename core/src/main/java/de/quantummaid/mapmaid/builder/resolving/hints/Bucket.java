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

import de.quantummaid.mapmaid.builder.detection.DetectionResult;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.builder.detection.DetectionResult.success;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Bucket<T> {
    private final ResolvedType id;
    private final List<BucketElement<T>> elements;

    public static <T> Bucket<T> bucket(final ResolvedType id,
                                       final List<T> elements) {
        final List<BucketElement<T>> bucketElements = elements.stream()
                .map(BucketElement::bucketElement)
                .collect(toList());
        return new Bucket<>(id, bucketElements);
    }

    public ResolvedType id() {
        return this.id;
    }

    public List<T> elements() {
        return this.elements.stream()
                .map(BucketElement::element)
                .collect(toList());
    }

    public void add(final List<T> elements) {
        elements.stream()
                .map(BucketElement::bucketElement)
                .forEach(this.elements::add);
    }

    public void sorted(final Comparator<T> comparator) {
        this.elements.sort(comparing(BucketElement::element, comparator));
    }

    public void strikeAll(final String message) {
        this.elements.forEach(element -> element.strike(message));
    }

    public void strike(final Predicate<T> predicate, final String message) {
        this.elements.stream()
                .filter(element -> predicate.test(element.element()))
                .forEach(element -> element.strike(message));
    }

    public DetectionResult<T> topElement() {
        if (this.elements.isEmpty()) {
            return DetectionResult.failure("unknown2"); // TODO
        }
        return success(this.elements.get(0).element());
    }

    public DetectionResult<T> expectSingleElement() {
        final List<BucketElement<T>> list = this.elements.stream()
                .filter(BucketElement::isNotStriked)
                .collect(toList());
        if (list.isEmpty()) {

        }
        return null;
    }
}