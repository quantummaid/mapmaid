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

package de.quantummaid.mapmaid.specs.examples.system.generator.lowlevel;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.collections.Collection.smallList;
import static java.util.Arrays.asList;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FieldModel {
    private final String name;
    private final String type;
    private final List<String> modifiers;

    public static FieldModel field(final String name, final String type) {
        return new FieldModel(name, type, smallList());
    }

    public FieldModel withModifier(final String modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public FieldModel withModifiers(final String... modifiers) {
        this.modifiers.addAll(asList(modifiers));
        return this;
    }

    public String render() {
        final StringJoiner joiner = new StringJoiner(" ", "\t", ";");
        this.modifiers.forEach(joiner::add);
        joiner.add(this.type);
        joiner.add(this.name);
        return joiner.toString();
    }
}
