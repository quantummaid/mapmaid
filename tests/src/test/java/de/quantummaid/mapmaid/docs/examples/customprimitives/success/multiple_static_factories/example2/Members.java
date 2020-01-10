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

package de.quantummaid.mapmaid.docs.examples.customprimitives.success.multiple_static_factories.example2;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Members {
    private final List<String> members;

    public static Members empty() {
        return new Members(List.of());
    }

    public static Members fromSingleMember(final Member member) {
        return new Members(singletonList(member.member));
    }

    public static Members fromString(final String commaSeparatedMembers) {
        final List<String> members = asList(commaSeparatedMembers.split(","));
        return new Members(members);
    }

    public static Members members(final String... members) {
        return new Members(asList(members));
    }

    public String internalValueForMapping() {
        return join(",", this.members);
    }

    public boolean contains(final String member) {
        return this.members.contains(member);
    }
}
