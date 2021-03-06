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

package de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.validators;

import de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.StructureValidations;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.StructureValidations.ok;
import static de.quantummaid.mapmaid.testsupport.givenwhenthen.structurevalidation.StructureValidations.validation;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NullValidator implements StructureValidator {

    public static StructureValidator nullValue() {
        return new NullValidator();
    }

    @Override
    public StructureValidations validate(final Object object) {
        if (object == null) {
            return ok();
        } else {
            return validation("not null");
        }
    }

    @Override
    public Object mutableSample() {
        return null;
    }
}
