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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import de.quantummaid.mapmaid.MapMaid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

import static de.quantummaid.mapmaid.testsupport.givenwhenthen.MapMaidInstances.theExampleMapMaidWithAllMarshallers;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Given {
    private final Supplier<MapMaid> mapMaid;

    public static Given given(final Supplier<MapMaid> mapMaid) {
        return new Given(mapMaid);
    }

    public static Given given(final MapMaid mapMaid) {
        return given(() -> mapMaid);
    }

    public static Given givenTheExampleMapMaidWithAllMarshallers() {
        return given(theExampleMapMaidWithAllMarshallers());
    }

    public When when() {
        return When.aWhen(this.mapMaid);
    }
}
