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

import de.quantummaid.mapmaid.debug.MapMaidException;
import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;
import de.quantummaid.mapmaid.mapper.deserialization.validation.ExceptionTracker;
import de.quantummaid.mapmaid.mapper.universal.Universal;
import de.quantummaid.mapmaid.shared.validators.NotNullValidator;

public final class WrongInputStructureException extends MapMaidException {
    private final transient Object inputObject;

    public WrongInputStructureException(final String message,
                                        final Object inputObject) {
        super(message, null);
        this.inputObject = inputObject;
    }

    public static WrongInputStructureException wrongInputStructureException(final Class<? extends Universal> expected,
                                                                            final Universal actual,
                                                                            final ExceptionTracker exceptionTracker,
                                                                            final ScanInformation scanInformation) {
        NotNullValidator.validateNotNull(expected, "expected");
        NotNullValidator.validateNotNull(actual, "actual");
        NotNullValidator.validateNotNull(exceptionTracker, "exceptionTracker");
        final String position = exceptionTracker.getPosition();
        final String location;
        if (position.isEmpty()) {
            location = "";
        } else {
            location = " for field '" + position + "'";
        }
        final String message = "Requiring the input to be an '" + Universal.describe(expected) +
                "'" + location +
                "\n\n" + scanInformation.render();
        return new WrongInputStructureException(message, actual.toNativeJava());
    }

    public Object inputObject() {
        return inputObject;
    }
}
