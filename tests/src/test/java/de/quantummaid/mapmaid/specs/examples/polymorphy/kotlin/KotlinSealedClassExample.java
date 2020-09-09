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

package de.quantummaid.mapmaid.specs.examples.polymorphy.kotlin;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.polymorphy.PolymorphicCustomType.fromKotlinSealedClass;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;
import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;

public final class KotlinSealedClassExample {

    @Test
    public void kotlinSealedClassExample() {
        scenarioBuilderFor(MyKotlinSealedClass.class)
                .withSerializedForm("" +
                        "{" +
                        "\"type\":\"de.quantummaid.mapmaid.specs.examples.polymorphy.kotlin.KotlinSealedSubclass1\"," +
                        "\"field1\":\"foo\"," +
                        "\"field2\":\"bar\"" +
                        "}")
                .withDeserializedForm(new KotlinSealedSubclass1("foo", "bar"))
                .withManualDeserialization(mapMaidBuilder ->
                        mapMaidBuilder.deserializing(fromKotlinSealedClass(getKotlinClass(MyKotlinSealedClass.class)))
                )
                .withManualSerialization(mapMaidBuilder ->
                        mapMaidBuilder.serializing(fromKotlinSealedClass(getKotlinClass(MyKotlinSealedClass.class))))
                .withManualDuplex(mapMaidBuilder ->
                        mapMaidBuilder.serializingAndDeserializing(fromKotlinSealedClass(getKotlinClass(MyKotlinSealedClass.class))))
                .run();
    }
}
