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

package de.quantummaid.mapmaid.minimaljson.bug;

import de.quantummaid.mapmaid.MapMaid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class BugThatDidNotAllowForNullToBeUnmarshalledSpecs {
    private static final MapMaid MAP_MAID = MapMaid.aMapMaid()
            .serializingAndDeserializingSubtypes(Message.class, Command.class, CommandResponse.class)
            .serializingAndDeserializing(ObjectWithNullableField.class)
            .build();

    @Test
    public void polymorphicScenario() {
        final String json = MAP_MAID.serializeToJson(new Command(null), Message.class);
        assertEquals("{\"nullable\":null,\"__type__\":\"de.quantummaid.mapmaid.minimaljson.bug.Command\"}", json);
        final var deserialized = MAP_MAID.deserializeJson(json, Message.class);
        assertEquals(new Command(null), deserialized);
    }

    @Test
    public void nonPolymorphicScenario() {
        final String json = MAP_MAID.serializeToJson(new ObjectWithNullableField(null));
        assertEquals("{\"nullable\":null}", json);
        final ObjectWithNullableField deserialized = MAP_MAID.deserializeJson(json, ObjectWithNullableField.class);
        assertEquals(new ObjectWithNullableField(null), deserialized);
    }
}
