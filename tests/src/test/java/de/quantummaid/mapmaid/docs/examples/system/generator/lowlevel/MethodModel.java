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

package de.quantummaid.mapmaid.docs.examples.system.generator.lowlevel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.StringJoiner;

import static de.quantummaid.mapmaid.Collection.smallList;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class MethodModel {
    private final String name;
    private String returnType;
    private final List<String> modifiers;
    private final List<ParameterModel> parameters;
    private final List<String> lines;

    public static MethodModel method(final String name) {
        return new MethodModel(name,
                "void",
                smallList(),
                smallList(),
                smallList()
        );
    }

    public MethodModel withReturnType(final String returnType) {
        this.returnType = returnType;
        return this;
    }

    public MethodModel withModifier(final String modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public MethodModel withModifiers(final String... modifiers) {
        this.modifiers.addAll(asList(modifiers));
        return this;
    }

    public MethodModel withParameter(final ParameterModel parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public MethodModel withParameters(final ParameterModel... parameters) {
        this.parameters.addAll(asList(parameters));
        return this;
    }

    public MethodModel withLine(final String line) {
        this.lines.add(line);
        return this;
    }

    public String render() {
        final StringJoiner headJoiner = new StringJoiner(" ", "\t", "");
        this.modifiers.forEach(headJoiner::add);
        headJoiner.add(this.returnType);
        final String parametersString = this.parameters.stream()
                .map(ParameterModel::render)
                .collect(joining(", ", "(", ")"));
        headJoiner.add(this.name + parametersString);
        headJoiner.add("{");
        final String head = headJoiner.toString();

        final StringJoiner fullJoiner = new StringJoiner("\n");
        fullJoiner.add(head);
        this.lines.forEach(line -> fullJoiner.add(format("\t\t%s", line)));
        fullJoiner.add("\t}");
        return fullJoiner.toString();
    }
}
