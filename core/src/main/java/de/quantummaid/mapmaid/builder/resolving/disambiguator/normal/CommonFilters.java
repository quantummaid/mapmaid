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
import de.quantummaid.mapmaid.shared.types.resolver.ResolvedField;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filter.filterOfType;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.allowed;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.FilterResult.denied;
import static java.lang.String.format;

final class CommonFilters {

    private CommonFilters() {
    }

    static Filter<TypeDeserializer> ignoreNonPublicMethodsForCustomPrimitiveDeserialization() {
        return filterOfType(CustomPrimitiveByMethodDeserializer.class, deserializer -> {
            if (deserializer.method().isPublic()) {
                return allowed();
            } else {
                return denied("only public static methods are considered for deserialization");
            }
        });
    }

    static Filter<TypeDeserializer> ignoreNonPublicConstructorsForCustomPrimitiveDeserialization() {
        return filterOfType(CustomPrimitiveByConstructorDeserializer.class, deserializer -> {
            if (deserializer.constructor().isPublic()) {
                return allowed();
            } else {
                return denied("only public constructors are considered for deserialization");
            }
        });
    }

    static Filter<TypeDeserializer> ignoreNonPublicMethodsForSerializedObjectDeserialization() {
        return filterOfType(MethodSerializedObjectDeserializer.class, deserializer -> {
            if (deserializer.method().isPublic()) {
                return allowed();
            } else {
                return denied("only public static methods are considered for deserialization");
            }
        });
    }

    static Filter<TypeDeserializer> ignoreNonPublicConstructorsForSerializedObjectDeserialization() {
        return filterOfType(ConstructorSerializedObjectDeserializer.class, deserializer -> {
            if (deserializer.constructor().isPublic()) {
                return allowed();
            } else {
                return denied("only public constructors are considered for deserialization");
            }
        });
    }

    static Filter<TypeSerializer> nameOfSerializerMethodIsNot(final String name) {
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

    static Filter<SerializationField> ignoreStaticFields() {
        return ignoreFieldsThat(ResolvedField::isStatic, "static fields are not serialized");
    }

    static Filter<SerializationField> ignoreTransientFields() {
        return ignoreFieldsThat(ResolvedField::isTransient, "transient fields are not serialized");
    }

    static Filter<SerializationField> ignoreNonPublicFields() {
        return ignoreFieldsThat(resolvedField -> !resolvedField.isPublic(), "only public fields are serialized");
    }

    private static Filter<SerializationField> ignoreFieldsThat(final Predicate<ResolvedField> fieldPredicate,
                                                               final String message) {
        return field -> {
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
