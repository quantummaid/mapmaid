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

package de.quantummaid.mapmaid.specs.exceptions;

import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class ExceptionThrowingMarshaller implements Marshaller<String>, Unmarshaller<String> {

    public static ExceptionThrowingMarshaller exceptionThrowingMarshaller() {
        return new ExceptionThrowingMarshaller();
    }

    public static ExceptionThrowingMarshaller exceptionThrowingUnmarshaller() {
        return new ExceptionThrowingMarshaller();
    }

    @Override
    public String marshal(final Object object) throws Exception {
        throw new TestException();
    }

    @Override
    public Object unmarshal(final String input) throws Exception {
        throw new TestException();
    }
}
