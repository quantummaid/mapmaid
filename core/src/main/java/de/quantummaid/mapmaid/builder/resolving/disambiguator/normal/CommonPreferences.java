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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.normal;

import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preference;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.PublicFieldQuery;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.SerializationFieldQuery;
import de.quantummaid.reflectmaid.resolver.ResolvedMethod;

import java.lang.reflect.Method;
import java.util.function.Predicate;

final class CommonPreferences {

    private CommonPreferences() {
    }

    static Preference<SerializationField> publicFields() {
        return field -> {
            final SerializationFieldQuery query = field.getQuery();
            return query instanceof PublicFieldQuery;
        };
    }

    static Preference<TypeDeserializer> serializedObjectFactoryNamed(final String name) {
        return serializedObjectFactoryThat(method -> method.getName().equals(name));
    }

    static Preference<TypeDeserializer> serializedObjectFactoryWithSameNameAsClass() {
        return serializedObjectFactoryThat(CommonPreferences::methodHasSameNameAsDeclaringClass);
    }

    static Preference<TypeDeserializer> serializedObjectFactoryThat(final Predicate<Method> filter) {
        return deserializer -> {
            if (!(deserializer instanceof MethodSerializedObjectDeserializer)) {
                return false;
            }
            final Method method = ((MethodSerializedObjectDeserializer) deserializer).method().method();
            return filter.test(method);
        };
    }

    static Preference<TypeSerializer> customPrimitiveSerializerNamed(final String name) {
        return serializer -> {
            if (!(serializer instanceof MethodCustomPrimitiveSerializer)) {
                return false;
            }
            final ResolvedMethod method = ((MethodCustomPrimitiveSerializer) serializer).method();
            return method.name().equals(name);
        };
    }

    static Preference<TypeDeserializer> customPrimitiveFactoryNamed(final String name) {
        return customPrimitiveFactoryThat(method -> method.getName().equals(name));
    }

    static Preference<TypeDeserializer> customPrimitiveFactoryWithSameNameAsClass() {
        return customPrimitiveFactoryThat(CommonPreferences::methodHasSameNameAsDeclaringClass);
    }

    private static Preference<TypeDeserializer> customPrimitiveFactoryThat(final Predicate<Method> filter) {
        return deserializer -> {
            if (!(deserializer instanceof CustomPrimitiveByMethodDeserializer)) {
                return false;
            }
            final Method method = ((CustomPrimitiveByMethodDeserializer) deserializer).method().method();
            return filter.test(method);
        };
    }

    private static boolean methodHasSameNameAsDeclaringClass(final Method method) {
        final String className = method.getDeclaringClass().getSimpleName().toLowerCase();
        return method.getName().equalsIgnoreCase(className);
    }
}
