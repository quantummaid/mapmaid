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

package de.quantummaid.mapmaid.builder.resolving.factories.primitives;

import de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.CustomPrimitiveSerializer;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.typescanner.Context;
import de.quantummaid.reflectmaid.typescanner.TypeIdentifier;
import de.quantummaid.reflectmaid.typescanner.factories.StateFactory;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.builder.conventional.ConventionalDefinitionFactories.CUSTOM_PRIMITIVE_MAPPINGS;
import static de.quantummaid.mapmaid.builder.resolving.MapMaidTypeScannerResult.result;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.DisambiguationResult.duplexResult;
import static de.quantummaid.mapmaid.builder.resolving.factories.primitives.BuiltInPrimitiveDeserializer.builtInPrimitiveDeserializer;
import static de.quantummaid.mapmaid.builder.resolving.factories.primitives.BuiltInPrimitiveSerializer.builtInPrimitiveSerializer;
import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuiltInPrimitivesFactory implements StateFactory<MapMaidTypeScannerResult> {
    private static final Map<Class<?>, Class<?>> ALSO_REGISTER = new LinkedHashMap<>();

    static {
        ALSO_REGISTER.put(double.class, Double.class);
        ALSO_REGISTER.put(Double.class, double.class);
        ALSO_REGISTER.put(float.class, Float.class);
        ALSO_REGISTER.put(Float.class, float.class);
        ALSO_REGISTER.put(long.class, Long.class);
        ALSO_REGISTER.put(Long.class, long.class);
        ALSO_REGISTER.put(int.class, Integer.class);
        ALSO_REGISTER.put(Integer.class, int.class);
        ALSO_REGISTER.put(short.class, Short.class);
        ALSO_REGISTER.put(Short.class, short.class);
        ALSO_REGISTER.put(byte.class, Byte.class);
        ALSO_REGISTER.put(Byte.class, byte.class);
        ALSO_REGISTER.put(char.class, Character.class);
        ALSO_REGISTER.put(Character.class, char.class);
    }

    private final ReflectMaid reflectMaid;

    public static BuiltInPrimitivesFactory builtInPrimitivesFactory(final ReflectMaid reflectMaid) {
        return new BuiltInPrimitivesFactory(reflectMaid);
    }

    @Override
    public boolean applies(@NotNull final TypeIdentifier type) {
        if (type.isVirtual()) {
            return false;
        }
        final ResolvedType realType = type.realType();
        final Class<?> assignableType = realType.assignableType();
        return CUSTOM_PRIMITIVE_MAPPINGS.isPrimitiveType(assignableType);
    }

    @Override
    public void create(final TypeIdentifier type,
                       final Context<MapMaidTypeScannerResult> context) {
        final ResolvedType realType = type.realType();
        final Class<?> assignableType = realType.assignableType();
        final List<TypeIdentifier> alsoRegister = ofNullable(ALSO_REGISTER.get(assignableType))
                .map(reflectMaid::resolve)
                .map(TypeIdentifier::typeIdentifierFor)
                .map(List::of)
                .orElseGet(List::of);
        final CustomPrimitiveSerializer serializer = builtInPrimitiveSerializer(assignableType, alsoRegister);
        final CustomPrimitiveDeserializer deserializer = builtInPrimitiveDeserializer(assignableType, alsoRegister);
        context.setManuallyConfiguredResult(result(duplexResult(serializer, deserializer), type));
    }
}
