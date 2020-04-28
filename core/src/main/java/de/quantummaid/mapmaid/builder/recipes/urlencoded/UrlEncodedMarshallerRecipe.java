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

package de.quantummaid.mapmaid.builder.recipes.urlencoded;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedUnmarshaller.urlEncodedUnmarshaller;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlEncodedMarshallerRecipe implements Recipe {

    public static MarshallingType urlEncoded() {
        return MarshallingType.marshallingType("urlencoded");
    }

    public static UrlEncodedMarshallerRecipe urlEncodedMarshaller() {
        return new UrlEncodedMarshallerRecipe();
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder) {
        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(
                urlEncoded(),
                UrlEncodedMarshaller.urlEncodedMarshaller(),
                urlEncodedUnmarshaller()
        ));
    }
}
