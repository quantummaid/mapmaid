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

package de.quantummaid.mapmaid.documentation.registration.registerinlinecollections;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType;
import de.quantummaid.mapmaid.documentation.registration.registercustomtypes.MyCustomPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegisterInlineCollectionTests {

    @Test
    void registeringDuplexInlineCollections() {
        //Showcase start inlineCollectionDuplexConfig
        final MapMaid mapMaid = aMapMaid()
                .serializingAndDeserializing(
                        DuplexType.inlinedCollection(
                                MyCustomCollection.class,
                                String.class,
                                MyCustomCollection::getValues,
                                MyCustomCollection::new)
                )
                .build();
        //Showcase end inlineCollectionDuplexConfig
        final String json = mapMaid.serializeToJson(new MyCustomCollection(List.of("a", "b", "c")));
        assertThat(json, is("[\"a\",\"b\",\"c\"]"));
        final MyCustomCollection deserialized = mapMaid.deserializeJson("[\"a\", \"b\", \"c\"]", MyCustomCollection.class);
        assertThat(deserialized, is(new MyCustomCollection(List.of("a", "b", "c"))));
    }

    @Test
    void registeringSerializingOnlyInlineCollections() {
        //Showcase start inlineCollectionSerializingConfig
        final MapMaid mapMaid = aMapMaid()
                .serializing(
                        SerializationOnlyType.inlinedCollection(
                                MyCustomCollection.class,
                                String.class,
                                MyCustomCollection::getValues)
                )
                .build();
        //Showcase end inlineCollectionSerializingConfig
        final String json = mapMaid.serializeToJson(new MyCustomCollection(List.of("a", "b", "c")));
        assertThat(json, is("[\"a\",\"b\",\"c\"]"));
    }

    @Test
    void registeringDeserializingInlineCollections() {
        //Showcase start inlineCollectionDeserializingConfig
        final MapMaid mapMaid = aMapMaid()
                .deserializing(
                        DeserializationOnlyType.inlinedCollection(
                                MyCustomCollection.class,
                                String.class,
                                MyCustomCollection::new)
                )
                .build();
        //Showcase end inlineCollectionDeserializingConfig
        final MyCustomCollection deserialized = mapMaid.deserializeJson("[\"a\", \"b\", \"c\"]", MyCustomCollection.class);
        assertThat(deserialized, is(new MyCustomCollection(List.of("a", "b", "c"))));
    }
}
