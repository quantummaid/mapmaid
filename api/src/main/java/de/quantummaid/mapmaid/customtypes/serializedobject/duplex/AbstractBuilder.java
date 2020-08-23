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

package de.quantummaid.mapmaid.customtypes.serializedobject.duplex;

import de.quantummaid.mapmaid.customtypes.DuplexType;
import de.quantummaid.mapmaid.customtypes.serializedobject.Builder;
import de.quantummaid.mapmaid.customtypes.serializedobject.InvocableDeserializer;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static de.quantummaid.mapmaid.customtypes.serializedobject.duplex.Common.createDuplexType;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class AbstractBuilder<X, T extends InvocableDeserializer<X>> {
    protected final Builder builder;

    public DuplexType<X> deserializedUsing(final T deserializer) {
        builder.setDeserializer(deserializer);
        return createDuplexType(builder);
    }
}
