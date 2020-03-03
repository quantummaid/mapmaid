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

package de.quantummaid.mapmaid.testsupport.domain.valid;

import de.quantummaid.mapmaid.testsupport.domain.exceptions.AnException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AComplexTypeWithValidations {
    private static final int MINIMUM_VALUE = 20;

    public final AString stringA;
    public final AString stringB;
    public final ANumber number1;
    public final ANumber number2;

    public static AComplexTypeWithValidations deserialize(
            final AString stringA,
            final AString stringB,
            final ANumber number1,
            final ANumber number2) {
        if (number1.isLowerThen(MINIMUM_VALUE)) {
            throw AnException.anException("number1 must not be lower then 20");
        }
        return new AComplexTypeWithValidations(stringA, stringB, number1, number2);
    }
}
