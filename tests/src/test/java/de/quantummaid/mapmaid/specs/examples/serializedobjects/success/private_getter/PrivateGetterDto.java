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

package de.quantummaid.mapmaid.specs.examples.serializedobjects.success.private_getter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PrivateGetterDto {
    private final String field0;
    private final String field1;
    private final String field2;
    private final String field3;

    public String getField0() {
        return field0;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    private String getField3() {
        return field3;
    }
}
