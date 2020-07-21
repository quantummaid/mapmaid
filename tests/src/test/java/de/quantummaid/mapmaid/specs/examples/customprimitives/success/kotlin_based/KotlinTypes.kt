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

package de.quantummaid.mapmaid.specs.examples.customprimitives.success.kotlin_based

typealias Validator<T> = (T) -> T

abstract class Primitive<T>(value: T, validator: Validator<T>) {
    init {
        validator.invoke(value)
    }
}

fun maxLength(maxLength: Int): (String) -> String = {
    if (it.length > maxLength) {
        throw IllegalArgumentException();
    }
    it
}

data class KotlinCustomPrimitive(val value: String) : Primitive<String>(value, maxLength(10))

data class KotlinDto(val field1: KotlinCustomPrimitive,
                     val field2: String,
                     val field3: Int,
                     val field4: KotlinCustomPrimitive)

data class KotlinDtoWithGeneric<S>(
        val field1: List<String>,
        val field2: List<List<Int>>,
        val field3: Collection<S>,
        val field4: Collection<Collection<S>>)