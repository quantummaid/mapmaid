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

package de.quantummaid.mapmaid.testsupport.givenwhenthen;

import de.quantummaid.mapmaid.debug.DebugInformation;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.deserialization.validation.AggregatedValidationException;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.StructureValidations;
import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators.StructureValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.StringContains;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static de.quantummaid.mapmaid.debug.scaninformation.Classification.CUSTOM_PRIMITIVE;
import static de.quantummaid.mapmaid.debug.scaninformation.Classification.SERIALIZED_OBJECT;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Then {
    private final ThenData thenData;

    public static Then then(final ThenData thenData) {
        return new Then(thenData);
    }

    private static int countCustomPrimitives(final DebugInformation debugInformation) {
        return (int) debugInformation.allScanInformations().stream()
                .filter(Then::isCustomPrimitive)
                .count();
    }

    private static int countSerializedObjects(final DebugInformation debugInformation) {
        return (int) debugInformation.allScanInformations().stream()
                .filter(Then::isSerializedObject)
                .count();
    }

    private static boolean isCustomPrimitive(final ScanInformation scanInformation) {
        return scanInformation.classification().equals(CUSTOM_PRIMITIVE);
    }

    private static boolean isSerializedObject(final ScanInformation scanInformation) {
        return scanInformation.classification().equals(SERIALIZED_OBJECT);
    }

    public Then theDeserializedObjectIs(final Object expected) {
        assertThat(this.thenData.getDeserializationResult(), is(expected));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Then theDeserialiedObjectHas(final Class<T> type, final Predicate<T> predicate) {
        final Object deserializationResult = this.thenData.getDeserializationResult();
        assertThat(deserializationResult, instanceOf(type));
        assertThat(predicate.test((T) deserializationResult), is(true));
        return this;
    }

    public Then anExceptionIsThrownWithAMessageContaining(final String message) {
        final Exception exception = thenData.getException();
        assertThat(exception, not(is(nullValue())));
        assertThat(exception.getMessage(), StringContains.containsString(message));
        return this;
    }

    public Then anExceptionIsThrownWithType(final Class<?> aClass) {
        final Exception cause = this.thenData.getException();
        assertThat(cause, instanceOf(aClass));
        return this;
    }

    public Then anExceptionIsThrownWithAUnderlyingCause(final String message) {
        final Exception exception = this.thenData.getException();
        assertThat(exception, not(is(nullValue())));
        final Throwable cause = exception.getCause();
        assertThat(cause.getMessage(), StringContains.containsString(message));
        return this;
    }

    public Then anExceptionIsThrownWithAMessageContainingLine(final String expectedMessage) {
        final Exception exception = thenData.getException();
        assertThat(exception, not(is(nullValue())));
        final String message = exception.getMessage();
        final List<String> lines = message.lines()
                .map(String::trim)
                .collect(toList());
        assertThat(lines, hasItem(expectedMessage));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Then anExceptionOfClassIsThrownFulfilling(final Class<T> expectedExceptionClass,
                                                         final Consumer<T> verificationLogic) {
        final Exception exception = thenData.getException();
        assertThat(exception, not(is(nullValue())));
        assertThat(exception, instanceOf(expectedExceptionClass));
        verificationLogic.accept((T) exception);
        return this;
    }

    public Then noExceptionHasBeenThrown() {
        final Exception exception = thenData.getException();
        if (exception != null) {
            final IllegalStateException illegalStateException = new IllegalStateException("Unexpected exception thrown", exception);
            fail(illegalStateException);
        }
        return this;
    }

    public Then anAggregatedExceptionHasBeenThrownWithNumberOfErrors(final int numberOfErrors) {
        assertThat(this.thenData.getException(), instanceOf(AggregatedValidationException.class));
        final AggregatedValidationException aggregatedValidationException = (AggregatedValidationException) this.thenData.getException();
        assertThat(aggregatedValidationException.getValidationErrors(), hasSize(numberOfErrors));
        return this;
    }

    public Then theDefinitionsContainExactlyTheCustomPrimitives(final Class<?>... types) {
        final DebugInformation debugInformation = this.thenData.getDebugInformation();
        final List<Class<?>> actualTypes = stream(types)
                .filter(classType -> debugInformation.optionalScanInformationFor(classType)
                        .map(Then::isCustomPrimitive)
                        .orElseThrow())
                .collect(toList());
        assertThat(actualTypes, containsInAnyOrder(types));
        assertThat(countCustomPrimitives(debugInformation), is(types.length));
        return this;
    }

    public Then theDefinitionsContainExactlyTheSerializedObjects(final Class<?>... types) {
        final DebugInformation debugInformation = this.thenData.getDebugInformation();
        final List<Class<?>> actualTypes = stream(types)
                .filter(classType -> debugInformation.optionalScanInformationFor(classType)
                        .map(Then::isSerializedObject)
                        .orElseThrow())
                .collect(toList());
        assertThat(actualTypes, containsInAnyOrder(types));
        assertThat(countSerializedObjects(debugInformation), is(types.length));
        return this;
    }

    public Then theSerializationResultMatches(final StructureValidator validator) {
        final Object serializationResult = thenData.getSerializationResult();
        final StructureValidations result = validator.validate(serializationResult);
        assertThat(result.render(), result.isValid(), is(true));
        return this;
    }

    public Then theSerializationResultWas(final Object serialized) {
        assertThat(this.thenData.getSerializationResult(), is(serialized));
        return this;
    }

    public Then mapMaidKnowsAboutMarshallingTypes(final MarshallingType<?>... types) {
        assertThat(this.thenData.getSupportedMarshallingTypes(), hasItems(types));
        return this;
    }

    public Then mapMaidKnowsAboutUnmarshallingTypes(final MarshallingType<?>... types) {
        assertThat(this.thenData.getSupportedUnmarshallingTypes(), hasItems(types));
        return this;
    }

    public Then theSchemaWas(final String schema) {
        assertThat(this.thenData.getSchema(), is(schema));
        return this;
    }
}
