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

package de.quantummaid.mapmaid.builder.lowlevel.withPrimitives;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("ClassWithTooManyFields")
public final class SerializedObjectWithPrimitives {
    public final int intPrimitive;
    public final Integer integerObject;
    public final long longPrimitive;
    public final Long longObject;
    public final short shortPrimitive;
    public final Short shortObject;
    public final double doublePrimitive;
    public final Double doubleObject;
    public final float floatPrimitive;
    public final Float floatObject;
    public final boolean booleanPrimitive;
    public final Boolean booleanObject;
    public final String stringObject;

    public static SerializedObjectWithPrimitives deserialize(
            final int intPrimitive, final Integer integerObject,
            final long longPrimitive, final Long longObject,
            final short shortPrimitive, final Short shortObject,
            final double doublePrimitive, final Double doubleObject,
            final float floatPrimitive, final Float floatObject,
            final boolean booleanPrimitive, final Boolean booleanObject,
            final String stringObject
    ) {
        return new SerializedObjectWithPrimitives(
                intPrimitive, integerObject,
                longPrimitive, longObject,
                shortPrimitive, shortObject,
                doublePrimitive, doubleObject,
                floatPrimitive, floatObject,
                booleanPrimitive, booleanObject,
                stringObject
        );
    }
}
