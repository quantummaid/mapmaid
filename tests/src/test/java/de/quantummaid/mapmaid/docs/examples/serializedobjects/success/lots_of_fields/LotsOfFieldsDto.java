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

package de.quantummaid.mapmaid.docs.examples.serializedobjects.success.lots_of_fields;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class LotsOfFieldsDto {
    public final String field1;
    public final String field2;
    public final String field3;
    public final String field4;
    public final String field5;
    public final String field6;
    public final String field7;
    public final String field8;
    public final String field9;
    public final String field10;
    public final String field11;
    public final String field12;
    public final String field13;
    public final String field14;
    public final String field15;
    public final String field16;

    public static LotsOfFieldsDto lotsOfFieldsDto(
             final String field1,
             final String field2,
             final String field3,
             final String field4,
             final String field5,
             final String field6,
             final String field7,
             final String field8,
             final String field9,
             final String field10,
             final String field11,
             final String field12,
             final String field13,
             final String field14,
             final String field15,
             final String field16
    ) {
        return new LotsOfFieldsDto(
                field1,
                field2,
                field3,
                field4,
                field5,
                field6,
                field7,
                field8,
                field9,
                field10,
                field11,
                field12,
                field13,
                field14,
                field15,
                field16
        );
    }
}
