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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.serialization_only.example2;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class SerializationOnlyExample {

    public static void main(String[] args) {
        // TODO
        final MapMaid mapMaid = aMapMaid()
                .withManuallyAddedType(AddALotRequest.class, deserializationOnly())
                .build();
    }

    @Test
    public void serializationOnlyExample() {
        scenarioBuilderFor(AddALotRequest.class)
                .withSerializedForm("" +
                        "{\n" +
                        "  \"name\": \"a\",\n" +
                        "  \"townNameA\": \"b\"\n" +
                        "}")
                .withDeserializedForm(AddALotRequest.EXAMPLE)
                .withSerializationOnly()
                .run();
    }
}
