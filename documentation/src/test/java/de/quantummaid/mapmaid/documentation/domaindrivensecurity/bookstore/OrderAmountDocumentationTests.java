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

package de.quantummaid.mapmaid.documentation.domaindrivensecurity.bookstore;

import org.junit.jupiter.api.Test;

import static de.quantummaid.mapmaid.documentation.domaindrivensecurity.bookstore.BookStore.bookStore;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class OrderAmountDocumentationTests {

    @Test
    public void testAmountCanBeHacked() {
        final BookStore bookStore = bookStore(100);
        bookStore.orderBook("abc", -3);
        final int balance = bookStore.getBalance();
        assertThat(balance, is(70));
    }

    @Test
    public void testAmountCanNotBeHacked() {
        final BookStore bookStore = bookStore(100);
        Exception exception;
        try {
            bookStore.orderBook(new Isbn(), OrderAmount.fromStringValue("-3"));
            exception = null;
        } catch (final IllegalArgumentException e) {
            exception = e;
        }
        assertThat(exception, notNullValue());
        final int balance = bookStore.getBalance();
        assertThat(balance, is(100));
    }
}
