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

package de.quantummaid.mapmaid.docs.examples.system;

import de.quantummaid.mapmaid.MapMaid;
import de.quantummaid.mapmaid.docs.examples.system.expectation.Expectation;
import de.quantummaid.mapmaid.docs.examples.system.mode.ExampleMode;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.quantummaid.mapmaid.docs.examples.system.Result.emptyResult;
import static de.quantummaid.mapmaid.docs.examples.system.expectation.DeserializationSuccessfulExpectation.deserializationWas;
import static de.quantummaid.mapmaid.docs.examples.system.expectation.Expectation.*;
import static de.quantummaid.mapmaid.docs.examples.system.expectation.SerializationSuccessfulExpectation.serializationWas;
import static de.quantummaid.mapmaid.docs.examples.system.mode.NormalExampleMode.*;
import static de.quantummaid.mapmaid.mapper.marshalling.MarshallingType.json;
import static de.quantummaid.mapmaid.shared.types.ResolvedType.resolvedType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScenarioBuilder {
    private final ResolvedType type;
    private String serializedForm;
    private Object deserializedForm;
    private final Map<ExampleMode, List<Expectation>> scenarios = new HashMap<>();

    public static ScenarioBuilder scenarioBuilderFor(final Class<?> type) {
        return new ScenarioBuilder(resolvedType(type));
    }

    public static ScenarioBuilder scenarioBuilderFor(final ResolvedType type) {
        return new ScenarioBuilder(type);
    }

    public ScenarioBuilder withSerializedForm(final String serializedForm) {
        this.serializedForm = serializedForm;
        return this;
    }

    public ScenarioBuilder withDeserializedForm(final Object deserializedForm) {
        this.deserializedForm = deserializedForm;
        return this;
    }

    public ScenarioBuilder withScenario(final ExampleMode exampleMode, final Expectation... expectation) {
        this.scenarios.put(exampleMode, List.of(expectation));
        return this;
    }

    public ScenarioBuilder withAllScenariosFailing(final String message) {
        withScenario(withAllCapabilities(), initializationFailed(message));
        withScenario(deserializationOnly(), initializationFailed(message));
        withScenario(serializationOnly(), initializationFailed(message));
        return this;
    }

    public ScenarioBuilder withAllScenariosSuccessful() {
        withScenario(deserializationOnly(), deserializationWas(this.deserializedForm), serializationFailedForNotSupported(this.type));
        withScenario(serializationOnly(), serializationWas(this.serializedForm), deserializationFailedForNotSupported(this.type));
        withScenario(withAllCapabilities(), deserializationWas(this.deserializedForm), serializationWas(this.serializedForm));
        return this;
    }

    public ScenarioBuilder withDeserializationOnly() {
        withScenario(deserializationOnly(), deserializationWas(this.deserializedForm), serializationFailedForNotSupported(this.type));
        withScenario(serializationOnly(), initializationFailed("TODO"));
        withScenario(withAllCapabilities(), initializationFailed("TODO"));
        return this;
    }

    public ScenarioBuilder withSerializationOnly() {
        withScenario(serializationOnly(), serializationWas(this.serializedForm), deserializationFailedForNotSupported(this.type));
        withScenario(deserializationOnly(), initializationFailed("TODO"));
        withScenario(withAllCapabilities(), initializationFailed("TODO"));
        return this;
    }

    public void run() {
        this.scenarios.forEach(this::run);
    }

    private void run(final ExampleMode mode, final List<Expectation> expectations) {
        final Result result = runScenario(mode);
        expectations.forEach(expectation -> expectation.ensure(result));
    }

    private Result runScenario(final ExampleMode mode) {
        System.out.println("mode = " + mode);
        final Result result = emptyResult();

        final MapMaid mapMaid;
        try {
            mapMaid = mode.provideMapMaid(this.type);
        } catch (final Throwable throwable) {
            result.withInitializationException(throwable);
            return result;
        }

        try {
            final String serialized = mapMaid.serializer()
                    .serialize(this.deserializedForm, this.type, json(), stringObjectMap -> stringObjectMap);
            result.withSerializationResult(serialized);
        } catch (final Throwable throwable) {
            result.withSerializationException(throwable);
        }

        try {
            final Object deserialized = mapMaid.deserializer().deserialize(this.serializedForm, this.type, json());
            result.withDeserializationResult(deserialized);
        } catch (final Throwable throwable) {
            result.withDeserializationException(throwable);
        }

        return result;
    }
}
