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

import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filters;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.TieBreaker;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveAsEnumDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.TypeSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.customprimitives.EnumCustomPrimitiveSerializer;
import de.quantummaid.mapmaid.mapper.serialization.serializers.serializedobject.SerializationField;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.CommonFilters.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.CommonPreferences.*;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.CommonTieBreakers.primitiveSerializationMethodNamed;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.NormalDisambiguator.normalDisambiguator;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Filters.filters;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.preferences.Preferences.preferences;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.tiebreaker.TieBreaker.tieBreaker;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DisambiguatorBuilder {
    private String preferredCustomPrimitiveFactoryName = "fromStringValue";
    private String preferredCustomPrimitiveSerializationMethodName = "stringValue";
    private String preferredSerializedObjectFactoryName = "deserialize";

    public static DisambiguatorBuilder defaultDisambiguatorBuilder() {
        return new DisambiguatorBuilder();
    }

    public void setPreferredCustomPrimitiveFactoryName(final String preferredCustomPrimitiveFactoryName) {
        this.preferredCustomPrimitiveFactoryName = preferredCustomPrimitiveFactoryName;
    }

    public void setPreferredCustomPrimitiveSerializationMethodName(
            final String preferredCustomPrimitiveSerializationMethodName) {
        this.preferredCustomPrimitiveSerializationMethodName = preferredCustomPrimitiveSerializationMethodName;
    }

    public void setPreferredSerializedObjectFactoryName(final String preferredSerializedObjectFactoryName) {
        this.preferredSerializedObjectFactoryName = preferredSerializedObjectFactoryName;
    }

    public NormalDisambiguator build() {
        final Preferences<TypeDeserializer, DisambiguationContext> customPrimitiveDeserializerPreferences =
                buildCustomPrimitiveDeserializerPreferences();
        final Preferences<TypeSerializer, DisambiguationContext> customPrimitiveSerializerPreferences =
                buildCustomPrimitiveSerializerPreferences();

        final Preferences<TypeDeserializer, DisambiguationContext> serializedObjectPreferences =
                buildSerializedObjectPreferences();
        final Filters<SerializationField, DisambiguationContext> serializationFieldFilters =
                buildSerializationFieldFilters();

        final Preferences<SerializationField, DisambiguationContext> postSymmetrySerializationFieldPreferences =
                preferences(List.of(
                        publicFields()
                ));

        final TieBreaker tieBreaker = buildTieBreaker();

        return normalDisambiguator(
                customPrimitiveDeserializerPreferences,
                customPrimitiveSerializerPreferences,
                serializedObjectPreferences,
                serializationFieldFilters,
                postSymmetrySerializationFieldPreferences,
                tieBreaker
        );
    }

    private Preferences<TypeDeserializer, DisambiguationContext> buildCustomPrimitiveDeserializerPreferences() {
        return preferences(
                List.of(
                        ignoreNonPublicMethodsForCustomPrimitiveDeserialization(),
                        ignoreNonPublicConstructorsForCustomPrimitiveDeserialization()
                ),
                List.of(
                        deserializer -> deserializer instanceof CustomPrimitiveAsEnumDeserializer,
                        customPrimitiveFactoryNamed(this.preferredCustomPrimitiveFactoryName),
                        customPrimitiveFactoryWithSameNameAsClass()
                ));
    }

    private Preferences<TypeSerializer, DisambiguationContext> buildCustomPrimitiveSerializerPreferences() {
        return preferences(
                List.of(
                        ignoreNonPublicMethodsForCustomPrimitiveSerialization(),
                        ignoreComponentMethodsForKotlinCustomPrimitiveSerialization(),
                        nameOfSerializerMethodIsNot("toString"),
                        nameOfSerializerMethodIsNot("hashCode")
                ),
                List.of(
                        serializer -> serializer instanceof EnumCustomPrimitiveSerializer,
                        customPrimitiveSerializerNamed(this.preferredCustomPrimitiveSerializationMethodName)
                ));
    }

    private Preferences<TypeDeserializer, DisambiguationContext> buildSerializedObjectPreferences() {
        return preferences(
                List.of(
                        ignoreNonPublicMethodsForSerializedObjectDeserialization(),
                        ignoreNonPublicConstructorsForSerializedObjectDeserialization()
                ),
                List.of(
                        serializedObjectFactoryNamed(this.preferredSerializedObjectFactoryName),
                        serializedObjectFactoryWithSameNameAsClass(),
                        deserializer -> deserializer instanceof MethodSerializedObjectDeserializer
                ));
    }

    private Filters<SerializationField, DisambiguationContext> buildSerializationFieldFilters() {
        return filters(List.of(
                ignoreStaticFields(),
                ignoreTransientFields(),
                ignoreNonPublicFields(),
                ignoreInjectedFields()
        ));
    }

    private TieBreaker buildTieBreaker() {
        return tieBreaker(
                List.of(
                        primitiveSerializationMethodNamed(this.preferredCustomPrimitiveSerializationMethodName)
                ),
                List.of(),
                List.of(
                        CommonTieBreakers.primitiveFactoryNamed(this.preferredCustomPrimitiveFactoryName)
                ),
                List.of(
                        CommonTieBreakers.serializedObjectFactoryNamed(this.preferredSerializedObjectFactoryName)
                )
        );
    }
}
