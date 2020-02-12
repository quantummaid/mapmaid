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

package de.quantummaid.mapmaid.builder.detection.priority;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Priority implements Comparable<Priority> {
    public static final Priority USER_PROVIDED = priority(100);
    public static final Priority HARDCODED = priority(90);
    public static final Priority ANNOTATED = priority(87);
    public static final Priority PRIVILEGED_FACTORY = priority(85);
    public static final Priority FACTORY = priority(80);
    public static final Priority CONSTRUCTOR = priority(70);
    public static final Priority POJO = priority(60);

    private final Integer priority;

    public static Priority priority(final int priority) {
        return new Priority(priority);
    }

    public boolean higherThan(final Priority other) {
        return this.priority > other.priority;
    }

    public int value() {
        return this.priority;
    }

    @Override
    public int compareTo(final Priority o) {
        return this.priority.compareTo(o.priority) * -1;
    }
}
