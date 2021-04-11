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

package de.quantummaid.mapmaid.builder;

import de.quantummaid.mapmaid.builder.autoload.Autoloadable;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguator;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguatorBuilder;
import de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.NormalDisambiguator;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.registry.MarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.registry.UnmarshallerRegistry;
import de.quantummaid.mapmaid.mapper.marshalling.string.StringUnmarshaller;
import de.quantummaid.mapmaid.polymorphy.PolymorphicTypeIdentifierExtractor;
import de.quantummaid.reflectmaid.ReflectMaid;
import de.quantummaid.reflectmaid.resolvedtype.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static de.quantummaid.mapmaid.builder.MapMaidConfiguration.emptyMapMaidConfiguration;
import static de.quantummaid.mapmaid.builder.MarshallerAutoloadingException.conflictingMarshallersForTypes;
import static de.quantummaid.mapmaid.builder.autoload.ActualAutoloadable.autoloadIfClassPresent;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.Disambiguators.disambiguators;
import static de.quantummaid.mapmaid.builder.resolving.disambiguator.normal.DisambiguatorBuilder.defaultDisambiguatorBuilder;
import static de.quantummaid.mapmaid.collections.Collection.smallMap;
import static de.quantummaid.mapmaid.mapper.marshalling.registry.MarshallerRegistry.marshallerRegistry;
import static de.quantummaid.mapmaid.mapper.marshalling.registry.UnmarshallerRegistry.unmarshallerRegistry;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.stream.Collectors.groupingBy;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdvancedBuilder {
    private static final List<Autoloadable<MarshallerAndUnmarshaller<?>>> AUTOLOADABLE_MARSHALLERS = List.of(
            autoloadIfClassPresent("de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller")
    );
    private final ReflectMaid reflectMaid;
    private final DisambiguatorBuilder defaultDisambiguatorBuilder = defaultDisambiguatorBuilder();
    private final MapMaidConfiguration mapMaidConfiguration = emptyMapMaidConfiguration();
    private Map<MarshallingType<?>, Marshaller<?>> marshallerMap = smallMap();
    private Map<MarshallingType<?>, Unmarshaller<?>> unmarshallerMap = smallMap();
    private boolean autoloadMarshallers = true;
    private List<MarshallerAndUnmarshaller<?>> autoloadedMarshallers = null;
    private Supplier<List<MarshallerAndUnmarshaller<?>>> autoloadMethod = this::autoloadMarshallers;

    public static AdvancedBuilder advancedBuilder(final ReflectMaid reflectMaid) {
        return new AdvancedBuilder(reflectMaid);
    }

    public AdvancedBuilder withTypeIdentifierKey(final String typeIdentifierKey) {
        validateNotNull(typeIdentifierKey, "typeIdentifierKey");
        mapMaidConfiguration.setTypeIdentifierKey(typeIdentifierKey);
        return this;
    }

    public AdvancedBuilder withTypeIdentifierExtractor(final PolymorphicTypeIdentifierExtractor extractor) {
        validateNotNull(extractor, "extractor");
        mapMaidConfiguration.setTypeIdentifierExtractor(extractor);
        return this;
    }

    public AdvancedBuilder withPreferredCustomPrimitiveFactoryName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredCustomPrimitiveFactoryName(name);
        return this;
    }

    public AdvancedBuilder withPreferredCustomPrimitiveSerializationMethodName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredCustomPrimitiveSerializationMethodName(name);
        return this;
    }

    public AdvancedBuilder withPreferredSerializedObjectFactoryName(final String name) {
        this.defaultDisambiguatorBuilder.setPreferredSerializedObjectFactoryName(name);
        return this;
    }

    public AdvancedBuilder doNotAutoloadMarshallers() {
        this.autoloadMarshallers = false;
        return this;
    }

    public <M> AdvancedBuilder usingMarshaller(final MarshallerAndUnmarshaller<M> marshallerAndUnmarshaller) {
        final MarshallingType<M> marshallingType = marshallerAndUnmarshaller.marshallingType();
        final Marshaller<M> marshaller = marshallerAndUnmarshaller.marshaller();
        final Unmarshaller<M> unmarshaller = marshallerAndUnmarshaller.unmarshaller();
        return usingMarshaller(marshallingType, marshaller, unmarshaller);
    }

    public <M> AdvancedBuilder usingMarshaller(final MarshallingType<M> marshallingType,
                                               final Marshaller<M> marshaller,
                                               final Unmarshaller<M> unmarshaller) {
        validateNotNull(marshaller, "marshaller");
        validateNotNull(unmarshaller, "unmarshaller");
        validateNotNull(marshallingType, "marshallingType");
        this.marshallerMap.put(marshallingType, marshaller);
        this.unmarshallerMap.put(marshallingType, unmarshaller);
        return doNotAutoloadMarshallers();
    }

    public AdvancedBuilder usingMarshaller(final Map<MarshallingType<?>, Marshaller<?>> marshallerMap,
                                           final Map<MarshallingType<?>, Unmarshaller<?>> unmarshallerMap) {
        this.marshallerMap = new HashMap<>(marshallerMap);
        this.unmarshallerMap = new HashMap<>(unmarshallerMap);
        return doNotAutoloadMarshallers();
    }

    public AdvancedBuilder usingJsonMarshaller(final Marshaller<String> marshaller, final StringUnmarshaller unmarshaller) {
        validateNotNull(marshaller, "jsonMarshaller");
        validateNotNull(unmarshaller, "jsonUnmarshaller");
        return usingMarshaller(MarshallingType.JSON, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingJsonMarshaller(final Marshaller<String> marshaller, final Unmarshaller<String> unmarshaller) {
        validateNotNull(marshaller, "jsonMarshaller");
        validateNotNull(unmarshaller, "jsonUnmarshaller");
        return usingMarshaller(MarshallingType.JSON, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingYamlMarshaller(final Marshaller<String> marshaller, final StringUnmarshaller unmarshaller) {
        validateNotNull(marshaller, "yamlMarshaller");
        validateNotNull(unmarshaller, "yamlUnmarshaller");
        return usingMarshaller(MarshallingType.YAML, marshaller, unmarshaller);
    }

    public AdvancedBuilder usingXmlMarshaller(final Marshaller<String> marshaller, final StringUnmarshaller unmarshaller) {
        validateNotNull(marshaller, "xmlMarshaller");
        validateNotNull(unmarshaller, "xmlUnmarshaller");
        return usingMarshaller(MarshallingType.XML, marshaller, unmarshaller);
    }

    Disambiguators buildDisambiguators() {
        final NormalDisambiguator defaultDisambiguator = this.defaultDisambiguatorBuilder.build();
        final Map<ResolvedType, Disambiguator> specialDisambiguators = smallMap();
        return disambiguators(defaultDisambiguator, specialDisambiguators);
    }

    MarshallerRegistry buildMarshallerRegistry() {
        if (autoloadMarshallers) {
            autoload();
            autoloadedMarshallers.forEach(autoloadableMarshaller -> {
                final MarshallingType<?> marshallingType = autoloadableMarshaller.marshallingType();
                final Marshaller<?> marshaller = autoloadableMarshaller.marshaller();
                marshallerMap.put(marshallingType, marshaller);
            });
        }
        return marshallerRegistry(marshallerMap);
    }

    UnmarshallerRegistry buildUnmarshallerRegistry() {
        if (autoloadMarshallers) {
            autoload();
            autoloadedMarshallers.forEach(autoloadableMarshaller -> {
                final MarshallingType<?> marshallingType = autoloadableMarshaller.marshallingType();
                final Unmarshaller<?> unmarshaller = autoloadableMarshaller.unmarshaller();
                unmarshallerMap.put(marshallingType, unmarshaller);
            });
        }
        return unmarshallerRegistry(unmarshallerMap);
    }

    MapMaidConfiguration mapMaidConfiguration() {
        return mapMaidConfiguration;
    }

    private void autoload() {
        if (autoloadedMarshallers == null) {
            autoloadedMarshallers = autoloadMethod.get();
        }
    }

    private List<MarshallerAndUnmarshaller<?>> autoloadMarshallers() {
        final Map<MarshallingType<?>, List<MarshallerAndUnmarshaller<?>>> foundByMarshallingTypes =
                AUTOLOADABLE_MARSHALLERS.stream()
                        .map(autoloadable -> autoloadable.autoload(reflectMaid))
                        .flatMap(Optional::stream)
                        .collect(groupingBy(MarshallerAndUnmarshaller::marshallingType));

        final MarshallingType<?>[] conflicting = foundByMarshallingTypes.values().stream()
                .filter(val -> val.size() > 1)
                .map(marshallerAndUnmarshallers -> marshallerAndUnmarshallers.get(0))
                .map(MarshallerAndUnmarshaller::marshallingType)
                .toArray(MarshallingType[]::new);

        if (conflicting.length >= 1) {
            final MarshallingType<?> firstConflict = conflicting[0];
            throw conflictingMarshallersForTypes(firstConflict, foundByMarshallingTypes.get(firstConflict));
        }

        return foundByMarshallingTypes.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
