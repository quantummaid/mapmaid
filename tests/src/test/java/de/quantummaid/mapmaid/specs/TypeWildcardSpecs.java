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

package de.quantummaid.mapmaid.specs;

import de.quantummaid.mapmaid.testsupport.domain.wildcards.AComplexTypeWithTypeWildcards;
import de.quantummaid.mapmaid.testsupport.domain.wildcards.AComplexTypeWithWildcardedCollection;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.MapMaid.aMapMaid;
import static de.quantummaid.mapmaid.builder.RequiredCapabilities.deserializationOnly;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.Given.given;

public final class TypeWildcardSpecs {

    @Test
    public void collectionsWithTypeWildcardsAreIgnored() {
        given(
                () -> aMapMaid()
                        .withManuallyAddedType(AComplexTypeWithWildcardedCollection.class, deserializationOnly())
                        .build())
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("?: unable to detect duplex: no duplex detected:\n" +
                        "type '?' is not supported because it contains wildcard generics (\"?\")\n" +
                        "\n" +
                        "?:\n" +
                        "Mode: duplex\n" +
                        "How it is serialized:\n" +
                        "\tNo serializer available\n" +
                        "Why it needs to be serializable:\n" +
                        "\t- because of java.util.List<?>\n" +
                        "Ignored features for serialization:\n" +
                        "How it is deserialized:\n" +
                        "\tNo deserializer available\n" +
                        "Why it needs to be deserializable:\n" +
                        "\t- because of java.util.List<?>\n" +
                        "Ignored features for deserialization:");
    }

    @Test
    public void classesWithWildcardGenericsAreIgnored() {
        given(
                () -> aMapMaid()
                        .withManuallyAddedType(AComplexTypeWithTypeWildcards.class)
                        .build()
        )
                .when().mapMaidIsInstantiated()
                .anExceptionIsThrownWithAMessageContaining("?: unable to detect duplex: no duplex detected:\n" +
                        "type '?' is not supported because it contains wildcard generics (\"?\")\n" +
                        "\n" +
                        "?:\n" +
                        "Mode: duplex\n" +
                        "How it is serialized:\n" +
                        "\tNo serializer available\n" +
                        "Why it needs to be serializable:\n" +
                        "\t- because of java.util.List<?>\n" +
                        "Ignored features for serialization:\n" +
                        "How it is deserialized:\n" +
                        "\tNo deserializer available\n" +
                        "Why it needs to be deserializable:\n" +
                        "\t- because of java.util.List<?>\n" +
                        "Ignored features for deserialization:");
    }
}
