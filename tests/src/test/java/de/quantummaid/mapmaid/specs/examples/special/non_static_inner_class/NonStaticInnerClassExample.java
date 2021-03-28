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

package de.quantummaid.mapmaid.specs.examples.special.non_static_inner_class;

import de.quantummaid.mapmaid.specs.examples.special.non_static_inner_class.OuterClass.NonStaticInnerClass;
import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.specs.examples.Util.registerCustomPrimitive;
import static de.quantummaid.mapmaid.specs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class NonStaticInnerClassExample {

    @Test
    public void nonStaticInnerClassExample() {
        final OuterClass outerClass = new OuterClass();
        scenarioBuilderFor(NonStaticInnerClass.class)
                .withSerializedForm("\"foo\"")
                .withDeserializedForm(outerClass.new NonStaticInnerClass("foo"))
                .withSerializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.non_static_inner_class.OuterClass$NonStaticInnerClass' " +
                        "cannot be detected because it is a non-static inner class (you can still register it manually)")
                .withDeserializationFailing("type 'de.quantummaid.mapmaid.specs.examples.special.non_static_inner_class.OuterClass$NonStaticInnerClass' " +
                        "cannot be detected because it is a non-static inner class (you can still register it manually)")
                .withDuplexFailing("type 'de.quantummaid.mapmaid.specs.examples.special.non_static_inner_class.OuterClass$NonStaticInnerClass' " +
                        "cannot be detected because it is a non-static inner class (you can still register it manually)")
                .withManual((mapMaidBuilder, requiredCapabilities) -> registerCustomPrimitive(
                        requiredCapabilities,
                        mapMaidBuilder,
                        NonStaticInnerClass.class,
                        object -> "foo",
                        value -> outerClass.new NonStaticInnerClass(value))
                )
                .run();
    }
}
