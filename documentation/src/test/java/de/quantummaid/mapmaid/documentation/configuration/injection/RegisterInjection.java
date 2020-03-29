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

package de.quantummaid.mapmaid.documentation.configuration.injection;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class RegisterInjection {

    @Test
    public void registeringNormalInjection() {
        //Showcase start normalInjection
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .injecting(MyInjectedValue.class)
                .build();
        //Showcase end normalInjection

        final MyInjectedValue deserialized = mapMaid.deserializeJson("{}", MyInjectedValue.class, injector -> injector.put(new MyInjectedValue("injected")));
        assertThat(deserialized, is(new MyInjectedValue("injected")));
    }

    @Test
    public void registeringFixedInjection() {
        //Showcase start fixedInjection
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .injecting(MyInjectedValue.class, () -> new MyInjectedValue("this is injected"))
                .build();
        //Showcase end fixedInjection

        final MyInjectedValue deserialized = mapMaid.deserializeJson("{}", MyInjectedValue.class);
        assertThat(deserialized, is(new MyInjectedValue("this is injected")));
    }
}
