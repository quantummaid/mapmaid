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

package de.quantummaid.mapmaid.jackson;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JacksonMarshallers implements Recipe {
    private final MarshallerAndUnmarshaller<String> marshallerAndUnmarshaller;

    public static JacksonMarshallers jacksonMarshallerJson() {
        return new JacksonMarshallers(new JacksonJsonMarshaller());
    }

    public static JacksonMarshallers jacksonMarshallerXml() {
        return new JacksonMarshallers(new JacksonXmlMarshaller());
    }

    public static JacksonMarshallers jacksonMarshallerYaml() {
        return new JacksonMarshallers(new JacksonYamlMarshaller());
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder) {
        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(this.marshallerAndUnmarshaller));
    }
}
