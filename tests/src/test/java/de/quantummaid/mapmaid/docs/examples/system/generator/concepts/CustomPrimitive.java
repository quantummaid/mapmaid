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

package de.quantummaid.mapmaid.docs.examples.system.generator.concepts;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

import static de.quantummaid.mapmaid.docs.examples.system.generator.lowlevel.ClassModel.classModel;
import static de.quantummaid.mapmaid.docs.examples.system.generator.lowlevel.FieldModel.field;
import static de.quantummaid.mapmaid.docs.examples.system.generator.lowlevel.MethodModel.method;
import static de.quantummaid.mapmaid.docs.examples.system.generator.lowlevel.ParameterModel.finalParameter;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomPrimitive {
    private final String name;
    private final String backingType;
    private final List<String> factories;

    public static CustomPrimitive customPrimitive(final String name, final String backingType) {
        return new CustomPrimitive(name, backingType, new LinkedList<>());
    }

    public String render() {
        return classModel(this.name)
                .withField(field("value", this.backingType)
                        .withModifiers("private", "final"))
                .withMethod(method("fromStringValue")
                        .withModifiers("public", "static")
                        .withReturnType(this.name)
                        .withParameter(finalParameter("value", this.backingType))
                        .withLine(format("return new %s(value);", this.backingType)))
                .withMethod(method("stringValue")
                        .withModifiers("public")
                        .withReturnType("String")
                        .withLine(format("return value;")))
                .render();
    }
}
