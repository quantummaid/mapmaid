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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.builder.customtypes.DeserializationOnlyType;
import de.quantummaid.mapmaid.builder.customtypes.DuplexType;
import de.quantummaid.mapmaid.domain.ACustomCollection;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.customtypes.SerializationOnlyType.inlinedCollection;
import static de.quantummaid.mapmaid.domain.ACustomCollection.aCustomCollection;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Marshallers.jsonMarshaller;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Unmarshallers.jsonUnmarshaller;

public class CustomCollectionSpecs {

    @Test
    public void customCollectionRegisteredDuplexCanBeSerialized() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(DuplexType.inlinedCollection(ACustomCollection.class, String.class, ACustomCollection::getValues, ACustomCollection::aCustomCollection))
                        .build()
        )
                .when().mapMaidSerializes(aCustomCollection(List.of("a", "b", "c")))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "[\n" +
                        "  \"a\",\n" +
                        "  \"b\",\n" +
                        "  \"c\"\n" +
                        "]");
    }

    @Test
    public void customCollectionRegisteredDuplexCanBeDeserialized() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializingAndDeserializing(DuplexType.inlinedCollection(ACustomCollection.class, String.class, ACustomCollection::getValues, ACustomCollection::aCustomCollection))
                        .build()
        )
                .when().mapMaidDeserializes("[\n  \"a\",\n  \"b\",\n  \"c\"\n]")
                .from(MarshallingType.JSON)
                .toTheType(ACustomCollection.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(aCustomCollection(List.of("a", "b", "c")));
    }

    @Test
    public void customCollectionCanBeRegisteredSerializingOnly() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .serializing(inlinedCollection(ACustomCollection.class, String.class, ACustomCollection::getValues))
                        .build()
        )
                .when().mapMaidSerializes(aCustomCollection(List.of("a", "b", "c")))
                .withMarshallingType(MarshallingType.JSON)
                .noExceptionHasBeenThrown()
                .theSerializationResultWas("" +
                        "[\n" +
                        "  \"a\",\n" +
                        "  \"b\",\n" +
                        "  \"c\"\n" +
                        "]");
    }

    @Test
    public void customCollectionCanBeRegisteredDeserializingOnly() {
        given(
                aMapMaid()
                        .withAdvancedSettings(advancedBuilder -> advancedBuilder.usingJsonMarshaller(jsonMarshaller(), jsonUnmarshaller()))
                        .deserializing(DeserializationOnlyType.inlinedCollection(ACustomCollection.class, String.class, ACustomCollection::aCustomCollection))
                        .build()
        )
                .when().mapMaidDeserializes("[\n  \"a\",\n  \"b\",\n  \"c\"\n]")
                .from(MarshallingType.JSON)
                .toTheType(ACustomCollection.class)
                .noExceptionHasBeenThrown()
                .theDeserializedObjectIs(aCustomCollection(List.of("a", "b", "c")));
    }
}
