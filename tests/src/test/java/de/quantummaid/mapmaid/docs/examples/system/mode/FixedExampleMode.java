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

package de.quantummaid.mapmaid.docs.examples.system.mode;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.RequiredCapabilities;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.function.BiConsumer;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FixedExampleMode implements ExampleMode {
    private final BiConsumer<MapMaidBuilder, RequiredCapabilities> fix;
    private final RequiredCapabilities capabilities;

    public static ExampleMode fixedWithAllCapabilities(final BiConsumer<MapMaidBuilder, RequiredCapabilities> fix) {
        return new FixedExampleMode(fix, RequiredCapabilities.duplex());
    }

    public static ExampleMode fixedDeserializationOnly(final BiConsumer<MapMaidBuilder, RequiredCapabilities> fix) {
        return new FixedExampleMode(fix, RequiredCapabilities.deserialization());
    }

    public static ExampleMode fixedSerializationOnly(final BiConsumer<MapMaidBuilder, RequiredCapabilities> fix) {
        return new FixedExampleMode(fix, RequiredCapabilities.serialization());
    }

    @Override
    public MapMaid provideMapMaid(final ResolvedType type) {
        final MapMaidBuilder mapMaidBuilder = aMapMaid()
                .withAdvancedSettings(advancedBuilder ->
                        advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()));
        this.fix.accept(mapMaidBuilder, this.capabilities);
        return mapMaidBuilder.build();
    }

    @Override
    public boolean serialize() {
        return this.capabilities.hasSerialization();
    }

    @Override
    public boolean deserialize() {
        return this.capabilities.hasDeserialization();
    }
}
