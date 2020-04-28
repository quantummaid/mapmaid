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

package de.quantummaid.mapmaid.documentation.detection;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class DetectionDocumentationTests {

    @Test
    public void gettersExample() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(GettersExample.class)
                .build();
        final String json = mapMaid.serializeToJson(new GettersExample());
        assertThat(json, is("{\"value2\":\"value2\",\"value1\":\"value1\",\"value3\":\"value3\"}"));
    }

    @Test
    public void mixedGettersAndPublicFieldsExample() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(MixedGettersAndPublicFieldsExample.class)
                .build();
        final String json = mapMaid.serializeToJson(new MixedGettersAndPublicFieldsExample());
        assertThat(json, is("{\"value2\":\"value2 from getter method\",\"value1\":\"value1 from public field\",\"value3\":\"value3 from getter method\"}"));
    }

    @Test
    public void gettersAndPublicFieldsExample() {
        final MapMaid mapMaid = MapMaid.aMapMaid()
                .serializing(GettersAndPublicFieldsExample.class)
                .build();
        final String json = mapMaid.serializeToJson(new GettersAndPublicFieldsExample());
        assertThat(json, is("{\"value2\":\"value2 from public field\",\"value1\":\"value1 from public field\",\"value3\":\"value3 from public field\"}"));
    }
}
