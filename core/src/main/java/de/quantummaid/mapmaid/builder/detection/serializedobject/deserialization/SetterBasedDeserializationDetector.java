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

package de.quantummaid.mapmaid.builder.detection.serializedobject.deserialization;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import de.quantummaid.reflectmaid.resolvedtype.ClassType;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedConstructor;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MultipleMethodsSerializedObjectDeserializer.multipleMethodsSerializedObjectDeserializer;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.Locale.US;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SetterBasedDeserializationDetector implements SerializedObjectDeserializationDetector {

    public static SerializedObjectDeserializationDetector setterBasedDeserializationDetector() {
        return new SetterBasedDeserializationDetector();
    }

    @Override
    public List<SerializedObjectDeserializer> detect(final ResolvedType type) {
        if (!(type instanceof ClassType)) {
            return emptyList();
        }
        final ClassType classType = (ClassType) type;
        final Optional<ResolvedConstructor> zeroArgumentsConstructor = classType.constructors().stream()
                .filter(resolvedConstructor -> resolvedConstructor.getParameters().isEmpty())
                .findAny();
        if (zeroArgumentsConstructor.isEmpty()) {
            return emptyList();
        }

        final List<ResolvedMethod> setterMethods = classType.methods().stream()
                .filter(resolvedMethod -> resolvedMethod.getMethod().getName().startsWith("set"))
                .filter(resolvedMethod -> resolvedMethod.returnType().isEmpty())
                .filter(resolvedMethod -> resolvedMethod.getParameters().size() == 1)
                .collect(Collectors.toList());
        if (setterMethods.isEmpty()) {
            return emptyList();
        }

        final Map<String, ResolvedMethod> fieldMap = new HashMap<>();
        setterMethods.forEach(resolvedMethod -> {
            final String name = extractSetterFieldName(resolvedMethod.getMethod().getName());
            fieldMap.put(name, resolvedMethod);
        });

        return of(multipleMethodsSerializedObjectDeserializer(zeroArgumentsConstructor.get(), fieldMap));
    }

    private static String extractSetterFieldName(final String methodName) {
        final String withoutGet = methodName.substring(3);
        final String firstCharacter = valueOf(withoutGet.charAt(0));
        final String lowercaseFirstCharacter = firstCharacter.toLowerCase(US);
        return lowercaseFirstCharacter + withoutGet.substring(1);
    }
}
