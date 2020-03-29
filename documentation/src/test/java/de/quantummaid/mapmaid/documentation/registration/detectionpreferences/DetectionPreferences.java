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

package de.quantummaid.mapmaid.documentation.registration.detectionpreferences;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class DetectionPreferences {

    @Test
    public void preferredCustomPrimitiveFactoryName() {
        //Showcase start preferredCustomPrimitiveFactoryName
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> {
                    advancedBuilder.withPreferredCustomPrimitiveFactoryName("instantiate");
                })
                .build();
        //Showcase end preferredCustomPrimitiveFactoryName
        assertThat(mapMaid, notNullValue());
    }

    @Test
    public void preferredCustomPrimitiveSerializationMethodName() {
        //Showcase start preferredCustomPrimitiveSerializationMethodName
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> {
                    advancedBuilder.withPreferredCustomPrimitiveSerializationMethodName("serializeToString");
                })
                .build();
        //Showcase end preferredCustomPrimitiveSerializationMethodName
        assertThat(mapMaid, notNullValue());
    }

    @Test
    public void preferredSerializedObjectFactoryName() {
        //Showcase start preferredSerializedObjectFactoryName
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .withAdvancedSettings(advancedBuilder -> {
                    advancedBuilder.withPreferredSerializedObjectFactoryName("instantiate");
                })
                .build();
        //Showcase end preferredSerializedObjectFactoryName
        assertThat(mapMaid, notNullValue());
    }
}
