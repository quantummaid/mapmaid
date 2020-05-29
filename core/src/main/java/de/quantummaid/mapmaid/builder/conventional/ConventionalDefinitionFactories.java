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

package de.quantummaid.mapmaid.builder.conventional;

import de.quantummaid.mapmaid.mapper.universal.UniversalBoolean;
import de.quantummaid.mapmaid.mapper.universal.UniversalDouble;
import de.quantummaid.mapmaid.mapper.universal.UniversalLong;
import de.quantummaid.mapmaid.mapper.universal.UniversalString;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;

import java.util.List;
import java.util.function.Function;

import static de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings.customPrimitiveMappings;
import static de.quantummaid.mapmaid.shared.mapping.UniversalTypeMapper.universalTypeMapper;

public final class ConventionalDefinitionFactories {

    @SuppressWarnings("java:S1905")
    public static final CustomPrimitiveMappings CUSTOM_PRIMITIVE_MAPPINGS = customPrimitiveMappings(
            universalTypeMapper(String.class, UniversalString.class,
                    UniversalString::universalString,
                    UniversalString::toNativeStringValue),
            universalTypeMapper(double.class, UniversalDouble.class,
                    UniversalDouble::universalDouble,
                    UniversalDouble::toNativeDouble),
            universalTypeMapper(Double.class, UniversalDouble.class,
                    UniversalDouble::universalDouble,
                    UniversalDouble::toNativeDouble),
            universalTypeMapper(boolean.class, UniversalBoolean.class,
                    UniversalBoolean::universalBoolean,
                    UniversalBoolean::toNativeBoolean),
            universalTypeMapper(Boolean.class, UniversalBoolean.class,
                    UniversalBoolean::universalBoolean,
                    UniversalBoolean::toNativeBoolean),
            universalTypeMapper(byte.class, UniversalLong.class,
                    (Function<Byte, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeByteExact),
            universalTypeMapper(Byte.class, UniversalLong.class,
                    (Function<Byte, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeByteExact),
            universalTypeMapper(short.class, UniversalLong.class,
                    (Function<Short, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeShortExact),
            universalTypeMapper(Short.class, UniversalLong.class,
                    (Function<Short, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeShortExact),
            universalTypeMapper(int.class, UniversalLong.class,
                    (Function<Integer, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeIntExact),
            universalTypeMapper(Integer.class, UniversalLong.class,
                    (Function<Integer, UniversalLong>) UniversalLong::universalLong,
                    UniversalLong::toNativeIntExact),
            universalTypeMapper(long.class, UniversalLong.class,
                    UniversalLong::universalLong,
                    UniversalLong::toNativeLong),
            universalTypeMapper(Long.class, UniversalLong.class,
                    UniversalLong::universalLong,
                    UniversalLong::toNativeLong),
            universalTypeMapper(float.class, UniversalDouble.class,
                    (Function<Float, UniversalDouble>) UniversalDouble::universalDouble,
                    UniversalDouble::toNativeFloatExact),
            universalTypeMapper(Float.class, UniversalDouble.class,
                    (Function<Float, UniversalDouble>) UniversalDouble::universalDouble,
                    UniversalDouble::toNativeFloatExact)
    );

    private ConventionalDefinitionFactories() {
    }
}
