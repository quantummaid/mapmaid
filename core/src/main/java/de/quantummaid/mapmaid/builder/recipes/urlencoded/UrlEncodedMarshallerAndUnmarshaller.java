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

package de.quantummaid.mapmaid.builder.recipes.urlencoded;

import de.quantummaid.mapmaid.builder.MarshallerAndUnmarshaller;
import de.quantummaid.mapmaid.mapper.marshalling.Marshaller;
import de.quantummaid.mapmaid.mapper.marshalling.MarshallingType;
import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshaller.urlEncodedMarshaller;
import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedMarshallerRecipe.urlEncoded;
import static de.quantummaid.mapmaid.builder.recipes.urlencoded.UrlEncodedUnmarshaller.urlEncodedUnmarshaller;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlEncodedMarshallerAndUnmarshaller implements MarshallerAndUnmarshaller<String> {
    public static final MarshallingType<String> URL_ENCODED = urlEncoded();

    private final UrlEncodedMarshaller marshaller;
    private final UrlEncodedUnmarshaller unmarshaller;

    public static UrlEncodedMarshallerAndUnmarshaller urlEncodedMarshallerAndUnmarshaller() {
        final UrlEncodedMarshaller marshaller = urlEncodedMarshaller();
        final UrlEncodedUnmarshaller unmarshaller = urlEncodedUnmarshaller();
        return new UrlEncodedMarshallerAndUnmarshaller(marshaller, unmarshaller);
    }

    @Override
    public MarshallingType<String> marshallingType() {
        return URL_ENCODED;
    }

    @Override
    public Marshaller<String> marshaller() {
        return marshaller;
    }

    @Override
    public Unmarshaller<String> unmarshaller() {
        return unmarshaller;
    }
}
