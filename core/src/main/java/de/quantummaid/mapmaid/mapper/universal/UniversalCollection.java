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

package de.quantummaid.mapmaid.mapper.universal;

import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniversalCollection implements Universal {
    private final List<Universal> list;

    public static UniversalCollection universalCollectionFromNativeList(final List<Object> list) {
        NotNullValidator.validateNotNull(list, "list");
        final List<Universal> mappedList = list.stream()
                .map(Universal::fromNativeJava)
                .collect(toList());
        return universalCollection(mappedList);
    }

    public static UniversalCollection universalCollection(final List<Universal> list) {
        NotNullValidator.validateNotNull(list, "list");
        return new UniversalCollection(list);
    }

    public List<Universal> content() {
        return unmodifiableList(this.list);
    }

    @Override
    public Object toNativeJava() {
        return this.list.stream()
                .map(Universal::toNativeJava)
                .collect(toList());
    }
}
