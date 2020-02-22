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

package de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator;

import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveAsEnumDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.customprimitives.CustomPrimitiveByMethodDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.MethodSerializedObjectDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.serializedobjects.SerializedObjectDeserializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Method;

import static de.quantummaid.mapmaid.builder.resolving.disambiguator.defaultdisambigurator.DefaultDisambiguator.defaultDisambiguator;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DefaultDisambiguatorBuilder {
    private String preferredCustomPrimitiveFactoryName = "fromStringValue";
    private String preferredSerializedObjectFactoryName = "deserialize";

    public static DefaultDisambiguatorBuilder defaultDisambiguatorBuilder() {
        return new DefaultDisambiguatorBuilder();
    }

    public void setPreferredCustomPrimitiveFactoryName(final String preferredCustomPrimitiveFactoryName) {
        this.preferredCustomPrimitiveFactoryName = preferredCustomPrimitiveFactoryName;
    }

    public void setPreferredSerializedObjectFactoryName(final String preferredSerializedObjectFactoryName) {
        this.preferredSerializedObjectFactoryName = preferredSerializedObjectFactoryName;
    }

    public DefaultDisambiguator build() {
        final Preferences<TypeDeserializer> customPrimitivePreferences = Preferences.preferences(
                deserializer -> deserializer instanceof CustomPrimitiveAsEnumDeserializer,
                deserializer -> {
                    if (!(deserializer instanceof CustomPrimitiveByMethodDeserializer)) {
                        return false;
                    }
                    final Method method = ((CustomPrimitiveByMethodDeserializer) deserializer).method();
                    return method.getName().equals(this.preferredCustomPrimitiveFactoryName);
                }
        );

        final Preferences<TypeDeserializer> serializedObjectPreferences = Preferences.preferences(
                deserializer -> {
                    if (!(deserializer instanceof MethodSerializedObjectDeserializer)) {
                        return false;
                    }
                    final Method method = ((MethodSerializedObjectDeserializer) deserializer).method().method();
                    return method.getName().equals(this.preferredSerializedObjectFactoryName);
                },
                deserializer -> deserializer instanceof MethodSerializedObjectDeserializer
        );

        return defaultDisambiguator(customPrimitivePreferences, serializedObjectPreferences);
    }
}
