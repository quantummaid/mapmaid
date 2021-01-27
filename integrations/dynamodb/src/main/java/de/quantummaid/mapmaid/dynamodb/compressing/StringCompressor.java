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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static de.quantummaid.mapmaid.debug.MapMaidException.mapMaidException;

final class StringCompressor {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BLOCK_SIZE = 4096;

    private StringCompressor() {
    }

    static byte[] compress(final String string) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BLOCK_SIZE)) {
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {
                final byte[] bytes = string.getBytes(CHARSET);
                deflaterOutputStream.write(bytes);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (final IOException e) {
            throw mapMaidException("exception during compression", e);
        }
    }

    static String decompress(final byte[] byteArray) {
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(byteArray))) {
            final byte[] decompressed = inflaterInputStream.readAllBytes();
            return new String(decompressed, CHARSET);
        } catch (final IOException e) {
            throw mapMaidException("exception during decompression", e);
        }
    }
}
