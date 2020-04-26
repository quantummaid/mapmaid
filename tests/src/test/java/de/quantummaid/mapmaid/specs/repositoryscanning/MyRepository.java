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

package de.quantummaid.mapmaid.specs.repositoryscanning;

import de.quantummaid.mapmaid.domain.ANumber;
import de.quantummaid.mapmaid.domain.AString;
import de.quantummaid.mapmaid.testsupport.domain.valid.*;

import java.util.Collection;

public final class MyRepository {
    public final AString aString = null;
    private final ANumber aNumber = null;

    private MyRepository(final AWrapperBoolean aWrapperBoolean) {
    }

    public MyRepository(final AWrapperInteger aWrapperInteger) {
    }

    private void method1(final AWrapperDouble aWrapperDouble) {
    }

    public Collection<APrimitiveBoolean> method2(final APrimitiveInteger aPrimitiveInteger) {
        throw new UnsupportedOperationException();
    }
}
