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

package de.quantummaid.mapmaid.dynamodb.customdynamodb;

import de.quantummaid.mapmaid.builder.MapMaidBuilder;
import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.builder.autoload.optimistic.OptimisticAutoloader;
import de.quantummaid.mapmaid.builder.recipes.Recipe;
import de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshaller;
import de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueUnmarshaller;
import de.quantummaid.mapmaid.dynamodb.rearranging.Rearranger;
import de.quantummaid.mapmaid.dynamodb.rearranging.RearrangingMarshaller;
import de.quantummaid.mapmaid.dynamodb.rearranging.RearrangingUnmarshaller;
import de.quantummaid.mapmaid.dynamodb.toplevelmap.TopLevelMapMarshaller;
import de.quantummaid.mapmaid.dynamodb.toplevelmap.TopLevelMapUnmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import de.quantummaid.mapmaid.minimaljson.MinimalJsonMarshallerAndUnmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;
import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueMarshaller.attributeValueMarshaller;
import static de.quantummaid.mapmaid.dynamodb.attributevalue.AttributeValueUnmarshaller.attributeValueUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.chain.ChainedMarshaller.chainMarshallers;
import static de.quantummaid.mapmaid.dynamodb.chain.ChainedUnmarshaller.chainUnmarshallers;
import static de.quantummaid.mapmaid.dynamodb.compressing.CompressingMarshaller.compressingMarshaller;
import static de.quantummaid.mapmaid.dynamodb.compressing.DecompressingUnmarshaller.decompressingUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.customdynamodb.CustomDynamoDbMarshallerAndUnmarshaller.customDynamoDbMarshallerAndUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.rearranging.Combination.combine;
import static de.quantummaid.mapmaid.dynamodb.rearranging.RearrangingMarshaller.rearrangingMarshaller;
import static de.quantummaid.mapmaid.dynamodb.rearranging.RearrangingUnmarshaller.rearrangingUnmarshaller;
import static de.quantummaid.mapmaid.dynamodb.rearranging.Transformation.transform;
import static de.quantummaid.mapmaid.dynamodb.toplevelmap.TopLevelMapMarshaller.topLevelMapMarshaller;
import static de.quantummaid.mapmaid.dynamodb.toplevelmap.TopLevelMapUnmarshaller.topLevelMapUnmarshaller;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomDynamoDbMarshallerRecipe implements Recipe {
    private final List<Function<MarshallerAndUnmarshaller<String>, Rearranger>> rearrangerProviders = new ArrayList<>();
    private MarshallerAndUnmarshaller<String> internalMarshaller;

    public static CustomDynamoDbMarshallerRecipe aDynamoDbMarshallerAndUnmarshaller() {
        return new CustomDynamoDbMarshallerRecipe();
    }

    public CustomDynamoDbMarshallerRecipe compressingTopLevelProperty(final String property) {
        return compressingTopLevelProperties(property).intoDynamoDbAttribute(property);
    }

    public CompressionStage compressingTopLevelProperties(final String... properties) {
        return attributeName -> {
            rearrangerProviders.add(x -> combine(asList(properties), attributeName));
            rearrangerProviders.add(x -> transform(
                    attributeName,
                    compressingMarshaller(x.marshaller()),
                    decompressingUnmarshaller(x.unmarshaller())
            ));
            return this;
        };
    }

    public CustomDynamoDbMarshallerRecipe usingMarshallerInsideOfCompression(final MarshallerAndUnmarshaller<String> marshallerAndUnmarshaller) {
        validateNotNull(marshallerAndUnmarshaller, "marshallerAndUnmarshaller");
        this.internalMarshaller = marshallerAndUnmarshaller;
        return this;
    }

    @Override
    public void cook(final MapMaidBuilder mapMaidBuilder) {
        final AttributeValueMarshaller attributeValueMarshaller = attributeValueMarshaller();
        final TopLevelMapMarshaller<AttributeValue> topLevelMapMarshaller = topLevelMapMarshaller(attributeValueMarshaller);

        final AttributeValueUnmarshaller attributeValueUnmarshaller = attributeValueUnmarshaller();
        final TopLevelMapUnmarshaller<AttributeValue> topLevelMapUnmarshaller = topLevelMapUnmarshaller(attributeValueUnmarshaller);

        final Marshaller<Map<String, AttributeValue>> marshaller;
        final Unmarshaller<Map<String, AttributeValue>> unmarshaller;
        if (rearrangerProviders.isEmpty()) {
            marshaller = topLevelMapMarshaller;
            unmarshaller = topLevelMapUnmarshaller;
        } else {
            final MarshallerAndUnmarshaller<String> usedInternalMarshaller = provideInternalMarshaller();
            final List<Rearranger> rearrangers = rearrangerProviders.stream()
                    .map(provider -> provider.apply(usedInternalMarshaller))
                    .collect(toList());
            final RearrangingMarshaller rearrangingMarshaller = rearrangingMarshaller(rearrangers);
            marshaller = chainMarshallers(rearrangingMarshaller, topLevelMapMarshaller);
            final List<Rearranger> reverseArrangers = new ArrayList<>(rearrangers);
            Collections.reverse(reverseArrangers);
            final RearrangingUnmarshaller rearrangingUnmarshaller = rearrangingUnmarshaller(reverseArrangers);
            unmarshaller = chainUnmarshallers(topLevelMapUnmarshaller, rearrangingUnmarshaller);
        }

        final CustomDynamoDbMarshallerAndUnmarshaller marshallerAndUnmarshaller =
                customDynamoDbMarshallerAndUnmarshaller(marshaller, unmarshaller);
        mapMaidBuilder.withAdvancedSettings(advancedBuilder -> advancedBuilder.usingMarshaller(marshallerAndUnmarshaller));
    }

    private MarshallerAndUnmarshaller<String> provideInternalMarshaller() {
        if (internalMarshaller != null) {
            return internalMarshaller;
        }
        return OptimisticAutoloader.autoload(
                /* do not refactor to lambda or method reference!!! */
                new Supplier<MinimalJsonMarshallerAndUnmarshaller>() {
                    @Override
                    public MinimalJsonMarshallerAndUnmarshaller get() {
                        return MinimalJsonMarshallerAndUnmarshaller.minimalJsonMarshallerAndUnmarshaller();
                    }
                },
                e -> mapMaidException("MinimalJSON marshaller is not on classpath" +
                        " - provide it or configure a marshaller manually", e)
        );
    }
}
