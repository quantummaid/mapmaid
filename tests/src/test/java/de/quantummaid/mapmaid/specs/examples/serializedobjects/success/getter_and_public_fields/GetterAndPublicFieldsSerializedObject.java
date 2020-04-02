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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.getter_and_public_fields;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetterAndPublicFieldsSerializedObject {
    public final String fieldA;
    public final String fieldB;
    public final String fieldC;
    public final String fieldD;

    public static GetterAndPublicFieldsSerializedObject getterAndPublicFieldsSerializedObject(final String fieldA,
                                                                                              final String fieldB,
                                                                                              final String fieldC,
                                                                                              final String fieldD) {
        return new GetterAndPublicFieldsSerializedObject(fieldA, fieldB, fieldC, fieldD);
    }

    public String getFieldA() {
        return this.fieldA;
    }

    public String getFieldB() {
        return this.fieldB;
    }

    public String getFieldC() {
        return this.fieldC;
    }

    public String getFieldD() {
        return this.fieldD;
    }
}
