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

package de.quantummaid.mapmaid.debug;

import de.quantummaid.mapmaid.debug.scaninformation.ScanInformation;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class MapMaidException extends RuntimeException {
    private static final String URL = "https://github.com/quantummaid/mapmaid/tree/master/documentation";

    protected MapMaidException(final String message,
                               final Throwable cause) {
        super(message, cause);
    }

    public static MapMaidException mapMaidException(final String message,
                                                    final ScanInformation... scanInformations) {
        return mapMaidException(message, null, scanInformations);
    }

    public static MapMaidException mapMaidException(final String message,
                                                    final List<ScanInformation> scanInformations) {
        return mapMaidException(message, null, scanInformations);
    }

    public static MapMaidException mapMaidException(final String message,
                                                    final Throwable cause,
                                                    final ScanInformation... scanInformations) {
        final List<ScanInformation> asList = asList(scanInformations);
        return mapMaidException(message, cause, asList);
    }

    public static MapMaidException mapMaidException(final String message,
                                                    final Throwable cause,
                                                    final List<ScanInformation> scanInformations) {
        final StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(message);
        messageBuilder.append("\n\n");

        for (final ScanInformation scanInformation : scanInformations) {
            messageBuilder.append(scanInformation.render());
            messageBuilder.append("\n\n");
        }
        messageBuilder.append(format("%nPlease visit our documentation at '%s' for additional help.%n", URL));
        final String fullMessage = messageBuilder.toString();
        return new MapMaidException(fullMessage, cause);
    }
}
