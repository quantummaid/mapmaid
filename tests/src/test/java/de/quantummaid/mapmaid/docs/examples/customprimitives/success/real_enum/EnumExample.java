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

package de.quantummaid.mapmaid.docs.examples.customprimitives.success.real_enum;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static de.quantummaid.mapmaid.docs.examples.system.ScenarioBuilder.scenarioBuilderFor;

public final class EnumExample {

    public static void main(String[] args) {
        final String name = DoorStatus.OPEN.name();
        DoorStatus.values();

        final Enum<?>[] values = values(DoorStatus.class);

        System.out.println("name = " + name);
    }

    private static Enum<?>[] values(final Class<? extends Enum<?>> enumType) {
        try {
            final Method method = DoorStatus.class.getDeclaredMethod("values");
            return (Enum<?>[]) method.invoke(null);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new UnsupportedOperationException("This should never happen", e);
        }
    }

    @Test
    public void enumExample() {
        scenarioBuilderFor(DoorStatus.class)
                .withSerializedForm("\"OPEN\"")
                .withDeserializedForm(DoorStatus.OPEN)
                .withAllScenariosSuccessful()
                .run();
    }
}
