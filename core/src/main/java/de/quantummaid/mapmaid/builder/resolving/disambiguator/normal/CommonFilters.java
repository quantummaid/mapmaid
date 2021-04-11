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

import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filter;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByConstructorDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.ConstructorSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.MethodCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.queries.PublicFieldQuery;
import de.quantummaid.mapmaid.shared.identifier.TypeIdentifier;
import de.quantummaid.reflectmaid.languages.Language;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedField;
import de.quantummaid.reflectmaid.resolvedtype.resolver.ResolvedMethod;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filter.filterOfType;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.allowed;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.denied;
import static java.lang.String.format;

final class CommonFilters {
    private static final Pattern KOTLIN_COMPONENT_PATTERN = Pattern.compile("component[0-9]+");

    private CommonFilters() {
    }

    static Filter<TypeDeserializer, DisambiguationContext> ignoreNonPublicMethodsForCustomPrimitiveDeserialization() {
        return filterOfType(CustomPrimitiveByMethodDeserializer.class, (deserializer, context, containingType) -> {
            if (deserializer.method().isPublic()) {
                return allowed();
            } else {
                return denied("only public static methods are considered for deserialization");
            }
        });
    }

    static Filter<TypeSerializer, DisambiguationContext> ignoreNonPublicMethodsForCustomPrimitiveSerialization() {
        return filterOfType(MethodCustomPrimitiveSerializer.class, (serializer, context, containingType) -> {
            if (serializer.method().isPublic()) {
                return allowed();
            } else {
                return denied("only public methods are considered for serialization");
            }
        });
    }

    static Filter<TypeSerializer, DisambiguationContext> ignoreComponentMethodsForKotlinCustomPrimitiveSerialization() {
        return filterOfType(MethodCustomPrimitiveSerializer.class, (serializer, context, containingType) -> {
            if (containingType.language() != Language.Companion.getKOTLIN()) {
                return allowed();
            }
            final ResolvedMethod method = serializer.method();
            final String methodName = method.name();
            final Matcher matcher = KOTLIN_COMPONENT_PATTERN.matcher(methodName);
            if (matcher.matches()) {
                return denied("Kotlin generated component methods are not used for serialization");
            } else {
                return allowed();
            }
        });
    }

    static Filter<TypeDeserializer, DisambiguationContext>
    ignoreNonPublicConstructorsForCustomPrimitiveDeserialization() {
        return filterOfType(CustomPrimitiveByConstructorDeserializer.class, (deserializer, context, containingType) -> {
            if (deserializer.constructor().isPublic()) {
                return allowed();
            } else {
                return denied("only public constructors are considered for deserialization");
            }
        });
    }

    static Filter<TypeDeserializer, DisambiguationContext>
    ignoreNonPublicMethodsForSerializedObjectDeserialization() {
        return filterOfType(MethodSerializedObjectDeserializer.class, (deserializer, context, containingType) -> {
            if (deserializer.method().isPublic()) {
                return allowed();
            } else {
                return denied("only public static methods are considered for deserialization");
            }
        });
    }

    static Filter<TypeDeserializer, DisambiguationContext>
    ignoreNonPublicConstructorsForSerializedObjectDeserialization() {
        return filterOfType(ConstructorSerializedObjectDeserializer.class,
                (deserializer, context, containingType) -> {
                    if (deserializer.constructor().isPublic()) {
                        return allowed();
                    } else {
                        return denied("only public constructors are considered for deserialization");
                    }
                });
    }

    static Filter<TypeSerializer, DisambiguationContext> nameOfSerializerMethodIsNot(final String name) {
        return (serializer, context, containingType) -> {
            if (!(serializer instanceof MethodCustomPrimitiveSerializer)) {
                return allowed();
            }
            final ResolvedMethod method = ((MethodCustomPrimitiveSerializer) serializer).method();
            final boolean matchesName = method.name().equals(name);
            if (!matchesName) {
                return allowed();
            } else {
                return denied(format("method '%s' is not considered", name));
            }
        };
    }

    static Filter<SerializationField, DisambiguationContext> ignoreInjectedFields() {
        return (serializationField, context, containingType) -> {
            final TypeIdentifier type = serializationField.type();
            if (context.isInjected(type)) {
                return denied("fields whose type is registered as injection-only are not serialized");
            } else {
                return allowed();
            }
        };
    }

    static Filter<SerializationField, DisambiguationContext> ignoreStaticFields() {
        return ignoreFieldsThat(ResolvedField::isStatic, "static fields are not serialized");
    }

    static Filter<SerializationField, DisambiguationContext> ignoreTransientFields() {
        return ignoreFieldsThat(
                ResolvedField::isTransient,
                "transient fields are not serialized"
        );
    }

    static Filter<SerializationField, DisambiguationContext> ignoreNonPublicFields() {
        return ignoreFieldsThat(
                resolvedField -> !resolvedField.isPublic(),
                "only public fields are serialized"
        );
    }

    private static Filter<SerializationField, DisambiguationContext> ignoreFieldsThat(
            final Predicate<ResolvedField> fieldPredicate,
            final String message) {
        return (field, context, containingType) -> {
            if (!(field.getQuery() instanceof PublicFieldQuery)) {
                return allowed();
            }
            final PublicFieldQuery query = (PublicFieldQuery) field.getQuery();
            final ResolvedField resolvedField = query.field();
            if (fieldPredicate.test(resolvedField)) {
                return denied(message);
            } else {
                return allowed();
            }
        };
    }
}
