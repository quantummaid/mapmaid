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

package de.quantummaid.mapmaid.dynamodb.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("java:S106")
@Slf4j
public final class FreePortPool {
    private static final int START_PORT = 9000;
    private static final int HIGHEST_PORT = 65535;
    private static AtomicInteger currentPort = new AtomicInteger(START_PORT);

    private FreePortPool() {
    }

    public static int freePort() {
        final int port = currentPort.incrementAndGet();
        if (port >= HIGHEST_PORT) {
            currentPort.set(START_PORT);
            return freePort();
        } else {
            try {
                final ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
                return port;
            } catch (IOException ex) {
                log.info("port {} in use, trying next one", port);
                return freePort();
            }
        }
    }

}
