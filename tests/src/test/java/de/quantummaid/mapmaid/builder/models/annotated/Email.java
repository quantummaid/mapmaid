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

package de.quantummaid.mapmaid.builder.models.annotated;

import de.quantummaid.mapmaid.builder.conventional.annotations.MapMaidDeserializationMethod;
import de.quantummaid.mapmaid.builder.conventional.annotations.MapMaidSerializedField;
import de.quantummaid.mapmaid.builder.validation.RequiredParameterValidator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Email {
    @MapMaidSerializedField
    public final EmailAddress sender;
    @MapMaidSerializedField
    public final EmailAddress receiver;
    @MapMaidSerializedField
    public final Subject subject;
    @MapMaidSerializedField
    public final Body body;

    @MapMaidDeserializationMethod
    public static Email restoreEmail(final EmailAddress sender,
                                     final EmailAddress receiver,
                                     final Subject subject,
                                     final Body body) {
        RequiredParameterValidator.ensureNotNull(sender, "sender");
        RequiredParameterValidator.ensureNotNull(receiver, "receiver");
        RequiredParameterValidator.ensureNotNull(body, "body");
        return new Email(sender, receiver, subject, body);
    }
}
