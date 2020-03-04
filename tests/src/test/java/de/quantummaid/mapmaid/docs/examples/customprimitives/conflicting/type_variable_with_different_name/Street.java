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

package de.quantummaid.mapmaid.docs.examples.customprimitives.conflicting.type_variable_with_different_name;

import de.quantummaid.mapmaid.docs.examples.customprimitives.success.deserialization_only.example1.GroupName;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.docs.examples.customprimitives.success.deserialization_only.example1.GroupName.groupName;
import static de.quantummaid.mapmaid.docs.examples.system.WrongMethodCalledException.wrongMethodCalledException;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Street<T> {
    private final String value;

    public static <E> Street<E> street(final String value) {
        return new Street<>(value);
    }

    public String stringValue() {
        return this.value;
    }

    public String stringValue(final String encoding) {
        throw wrongMethodCalledException();
    }

    public String lowerCase() {
        throw wrongMethodCalledException();
    }

    public GroupName asGroupName() {
        return groupName(this.value);
    }
}
