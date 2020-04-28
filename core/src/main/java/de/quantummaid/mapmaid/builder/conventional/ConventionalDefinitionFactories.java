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
import de.quantummaid.mapmaid.mapper.universal.UniversalNumber;
import de.quantummaid.mapmaid.mapper.universal.UniversalString;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;

import java.util.function.Function;

import static de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings.customPrimitiveMappings;
import static de.quantummaid.mapmaid.shared.mapping.UniversalTypeMapper.universalTypeMapper;

public final class ConventionalDefinitionFactories {

    public static final CustomPrimitiveMappings CUSTOM_PRIMITIVE_MAPPINGS = customPrimitiveMappings(
            universalTypeMapper(String.class, UniversalString.class),
            universalTypeMapper(double.class, UniversalNumber.class),
            universalTypeMapper(Double.class, UniversalNumber.class),
            universalTypeMapper(boolean.class, UniversalBoolean.class),
            universalTypeMapper(Boolean.class, UniversalBoolean.class),
            universalTypeMapper(int.class, UniversalNumber.class,
                    (Function<Integer, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).intValue()),
            universalTypeMapper(Integer.class, UniversalNumber.class,
                    (Function<Integer, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).intValue()),
            universalTypeMapper(long.class, UniversalNumber.class,
                    (Function<Long, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).longValue()),
            universalTypeMapper(Long.class, UniversalNumber.class,
                    (Function<Long, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).longValue()),
            universalTypeMapper(float.class, UniversalNumber.class,
                    (Function<Float, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).floatValue()),
            universalTypeMapper(Float.class, UniversalNumber.class,
                    (Function<Float, UniversalNumber>) UniversalNumber::universalNumber, // NOSONAR
                    universalNumber -> ((Double) universalNumber.toNativeJava()).floatValue())
    );

    private ConventionalDefinitionFactories() {
    }
}
