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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.preferred_factory.class_name;

import de.quantummaid.mapmaid.docs.examples.system.WrongMethodCalledException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PreferredFactorySerializedObject {
    public final String value1;
    public final String value2;
    public final String value3;
    public final String value4;

    public static PreferredFactorySerializedObject factoryA(final String value1,
                                                            final String value2,
                                                            final String value3,
                                                            final String value4) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactorySerializedObject factoryB(final String value1,
                                                            final String value2,
                                                            final String value3,
                                                            final String value4) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactorySerializedObject preferredFactorySerializedObject(final String value1,
                                                                final String value2,
                                                                final String value3,
                                                                final String value4) {
        return new PreferredFactorySerializedObject(value1, value2, value3, value4);
    }

    public static PreferredFactorySerializedObject factoryC(final String value1,
                                                            final String value2,
                                                            final String value3,
                                                            final String value4) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }

    public static PreferredFactorySerializedObject factoryD(final String value1,
                                                            final String value2,
                                                            final String value3,
                                                            final String value4) {
        throw WrongMethodCalledException.wrongMethodCalledException();
    }
}
