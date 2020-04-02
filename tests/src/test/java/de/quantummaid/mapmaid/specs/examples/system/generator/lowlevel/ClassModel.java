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

import static de.quantummaid.mapmaid.Collection.smallList;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClassModel {
    private final String name;
    private final List<FieldModel> fields;
    private final List<MethodModel> methods;

    public static ClassModel classModel(final String name) {
        return new ClassModel(name, smallList(), smallList());
    }

    public ClassModel withField(final FieldModel field) {
        this.fields.add(field);
        return this;
    }

    public ClassModel withMethod(final MethodModel method) {
        this.methods.add(method);
        return this;
    }

    public String render() {
        final StringJoiner joiner = new StringJoiner("\n");
        joiner.add(format("public final class %s {", this.name));

        this.fields.stream()
                .map(FieldModel::render)
                .forEach(joiner::add);

        joiner.add("");

        this.methods.stream()
                .map(MethodModel::render)
                .forEach(joiner::add);

        joiner.add("}");
        return joiner.toString();
    }
}
