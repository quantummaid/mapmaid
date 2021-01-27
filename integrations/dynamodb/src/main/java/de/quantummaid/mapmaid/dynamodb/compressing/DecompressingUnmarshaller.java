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

package de.quantummaid.mapmaid.dynamodb.compressing;

import de.quantummaid.mapmaid.mapper.marshalling.Unmarshaller;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class DecompressingUnmarshaller implements Unmarshaller<byte[]> {
    private final Unmarshaller<String> innerMarshaller;

    public static DecompressingUnmarshaller decompressingUnmarshaller(final Unmarshaller<String> innerMarshaller) {
        return new DecompressingUnmarshaller(innerMarshaller);
    }

    @Override
    public Object unmarshal(final byte[] input) throws Exception {
        final String decompressed = StringCompressor.decompress(input);
        return innerMarshaller.unmarshal(decompressed);
    }
}
