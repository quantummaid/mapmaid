/*
 * Copyright (c) 2019 Richard Hauswald - https://quantummaid.de/.
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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator;

import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Filter;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Filters;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Preference;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.Preferences;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveAsEnumDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.EnumCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.PublicFieldQuery;
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.DefaultDisambiguator.defaultDisambiguator;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.FilterResult.allowed;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.preferences.FilterResult.denied;
import static java.lang.String.format;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultDisambiguatorBuilder {
    private String preferredCustomPrimitiveFactoryName = "fromStringValue";
    private String preferredCustomPrimitiveSerializationMethodName = "stringValue";
    private String preferredSerializedObjectFactoryName = "deserialize";

    public static DefaultDisambiguatorBuilder defaultDisambiguatorBuilder() {
        return new DefaultDisambiguatorBuilder();
    }

    public void setPreferredCustomPrimitiveFactoryName(final String preferredCustomPrimitiveFactoryName) {
        this.preferredCustomPrimitiveFactoryName = preferredCustomPrimitiveFactoryName;
    }

    public void setPreferredCustomPrimitiveSerializationMethodName(final String preferredCustomPrimitiveSerializationMethodName) {
        this.preferredCustomPrimitiveSerializationMethodName = preferredCustomPrimitiveSerializationMethodName;
    }

    public void setPreferredSerializedObjectFactoryName(final String preferredSerializedObjectFactoryName) {
        this.preferredSerializedObjectFactoryName = preferredSerializedObjectFactoryName;
    }

    public DefaultDisambiguator build() {
        final Preferences<TypeDeserializer> customPrimitiveDeserializerPreferences = Preferences.preferences(List.of(
                deserializer -> deserializer instanceof CustomPrimitiveAsEnumDeserializer,
                customPrimitiveFactoryNamed(this.preferredCustomPrimitiveFactoryName),
                customPrimitiveFactoryWithSameNameAsClass()
        ));

        final Preferences<TypeSerializer> customPrimitiveSerializerPreferences = Preferences.preferences(
                List.of(
                        nameOfSerializerMethodIsNot("toString"),
                        nameOfSerializerMethodIsNot("hashCode")
                ),
                List.of(
                        serializer -> serializer instanceof EnumCustomPrimitiveSerializer,
                        customPrimitiveSerializerNamed(this.preferredCustomPrimitiveSerializationMethodName)
                ));

        final Preferences<TypeDeserializer> serializedObjectPreferences = Preferences.preferences(List.of(
                serializedObjectFactoryNamed(this.preferredSerializedObjectFactoryName),
                serializedObjectFactoryWithSameNameAsClass(),
                deserializer -> deserializer instanceof MethodSerializedObjectDeserializer
        ));

        final Filters<SerializationField> serializationFieldFilters = Filters.filters(List.of(
                ignoreStaticFields()
        ));

        return defaultDisambiguator(
                customPrimitiveDeserializerPreferences,
                customPrimitiveSerializerPreferences,
                serializedObjectPreferences,
                serializationFieldFilters
        );
    }

    private static Preference<TypeDeserializer> serializedObjectFactoryNamed(final String name) {
        return serializedObjectFactoryThat(method -> method.getName().equals(name));
    }

    private static Preference<TypeDeserializer> serializedObjectFactoryWithSameNameAsClass() {
        return serializedObjectFactoryThat(DefaultDisambiguatorBuilder::methodHasSameNameAsDeclaringClass);
    }

    private static Preference<TypeDeserializer> serializedObjectFactoryThat(final Predicate<Method> filter) {
        return deserializer -> {
            if (!(deserializer instanceof MethodSerializedObjectDeserializer)) {
                return false;
            }
            final Method method = ((MethodSerializedObjectDeserializer) deserializer).method().method();
            return filter.test(method);
        };
    }

    private static Preference<TypeSerializer> customPrimitiveSerializerNamed(final String name) {
        return serializer -> {
            if (!(serializer instanceof MethodCustomPrimitiveSerializer)) {
                return false;
            }
            final Method method = ((MethodCustomPrimitiveSerializer) serializer).method();
            return method.getName().equals(name);
        };
    }

    private static Preference<TypeDeserializer> customPrimitiveFactoryNamed(final String name) {
        return customPrimitiveFactoryThat(method -> method.getName().equals(name));
    }

    private static Preference<TypeDeserializer> customPrimitiveFactoryWithSameNameAsClass() {
        return customPrimitiveFactoryThat(DefaultDisambiguatorBuilder::methodHasSameNameAsDeclaringClass);
    }

    private static Preference<TypeDeserializer> customPrimitiveFactoryThat(final Predicate<Method> filter) {
        return deserializer -> {
            if (!(deserializer instanceof CustomPrimitiveByMethodDeserializer)) {
                return false;
            }
            final Method method = ((CustomPrimitiveByMethodDeserializer) deserializer).method();
            return filter.test(method);
        };
    }

    private static boolean methodHasSameNameAsDeclaringClass(final Method method) {
        final String className = method.getDeclaringClass().getSimpleName().toLowerCase();
        return method.getName().toLowerCase().equals(className);
    }

    private static Filter<TypeSerializer> nameOfSerializerMethodIsNot(final String name) {
        return serializer -> {
            if (!(serializer instanceof MethodCustomPrimitiveSerializer)) {
                return allowed();
            }
            final Method method = ((MethodCustomPrimitiveSerializer) serializer).method();
            final boolean matchesName = method.getName().equals(name);
            if (!matchesName) {
                return allowed();
            } else {
                return denied(format("method '%s' is not considered", name));
            }
        };
    }

    private static Filter<SerializationField> ignoreStaticFields() {
        return field -> {
            if (!(field.getQuery() instanceof PublicFieldQuery)) {
                return allowed();
            }
            final PublicFieldQuery query = (PublicFieldQuery) field.getQuery();
            final ResolvedField resolvedField = query.field();
            if (resolvedField.isStatic()) {
                return denied("static fields are not serialized");
            } else {
                return allowed();
            }
        };
    }
}
