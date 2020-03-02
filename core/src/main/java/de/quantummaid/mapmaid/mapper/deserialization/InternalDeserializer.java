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

package de.quantummaid.mapmaid.mapper.deserialization;

import de.quantummaid.mapmaid.mapper.definitions.Definition;
import de.quantummaid.mapmaid.mapper.definitions.Definitions;
import de.quantummaid.mapmaid.mapper.deserialization.deserializers.TypeDeserializer;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationErrorsMapping;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ValidationResult;
import de.quantummaid.mapmaid.mapper.injector.Injector;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.mapper.universal.UniversalNull;
import de.quantummaid.mapmaid.shared.mapping.CustomPrimitiveMappings;
import de.quantummaid.mapmaid.shared.types.ResolvedType;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.lang.String.format;

@SuppressWarnings({"unchecked", "InstanceofConcreteClass"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class InternalDeserializer implements DeserializerCallback {
    private final Definitions definitions;
    private final CustomPrimitiveMappings customPrimitiveMappings;
    private final ValidationErrorsMapping onValidationErrors;

    static InternalDeserializer internalDeserializer(final Definitions definitions,
                                                     final CustomPrimitiveMappings customPrimitiveMappings,
                                                     final ValidationErrorsMapping validationErrorsMapping) {
        NotNullValidator.validateNotNull(definitions, "definitions");
        NotNullValidator.validateNotNull(customPrimitiveMappings, "customPrimitiveMappings");
        NotNullValidator.validateNotNull(validationErrorsMapping, "validationErrorsMapping");
        return new InternalDeserializer(definitions, customPrimitiveMappings, validationErrorsMapping);
    }

    <T> T deserialize(final Universal input,
                      final ResolvedType targetType,
                      final ExceptionTracker exceptionTracker,
                      final Injector injector) {
        final T result = (T) this.deserializeRecursive(input, targetType, exceptionTracker, injector);
        final ValidationResult validationResult = exceptionTracker.validationResult();
        if (validationResult.hasValidationErrors()) {
            this.onValidationErrors.map(validationResult.validationErrors());
        }
        return result;
    }

    @Override
    public Object deserializeRecursive(final Universal input,
                                       final ResolvedType targetType,
                                       final ExceptionTracker exceptionTracker,
                                       final Injector injector) {
        final Optional<Object> namedDirectInjection = injector.getDirectInjectionForPropertyPath(exceptionTracker.getPosition());
        if (namedDirectInjection.isPresent()) {
            return namedDirectInjection.get();
        }

        final Optional<Object> typedDirectInjection = injector.getDirectInjectionForType(targetType);
        if (typedDirectInjection.isPresent()) {
            return typedDirectInjection.get();
        }

        final Universal resolved = injector.getUniversalInjectionFor(exceptionTracker.getPosition()).orElse(input);

        if (input instanceof UniversalNull) {
            return null;
        }

        final Definition definition = this.definitions.getDefinitionForType(targetType);
        try {
            final TypeDeserializer deserializer = definition.deserializer().orElseThrow(() ->
                    new UnsupportedOperationException(format("No deserializer configured for '%s'", definition.type().description())));
            return deserializer.deserialize(resolved, exceptionTracker, injector, this, this.customPrimitiveMappings);
        } catch (final WrongInputStructureException e) {
            exceptionTracker.track(e, e.getMessage());
            return null;
        }
    }
}
