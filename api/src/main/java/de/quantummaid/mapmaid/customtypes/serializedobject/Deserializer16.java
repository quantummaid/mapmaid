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

package de.quantummaid.mapmaid.customtypes.serializedobject;

@SuppressWarnings({"java:S109", "java:S107"})
public interface Deserializer16<X, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> extends InvocableDeserializer<X> {
    X deserialize(A a,
                  B b,
                  C c,
                  D d,
                  E e,
                  F f,
                  G g,
                  H h,
                  I i,
                  J j,
                  K k,
                  L l,
                  M m,
                  N n,
                  O o,
                  P p
    );

    @SuppressWarnings("unchecked")
    @Override
    default X invoke(final Object[] arguments) {
        return deserialize(
                (A) arguments[0],
                (B) arguments[1],
                (C) arguments[2],
                (D) arguments[3],
                (E) arguments[4],
                (F) arguments[5],
                (G) arguments[6],
                (H) arguments[7],
                (I) arguments[8],
                (J) arguments[9],
                (K) arguments[10],
                (L) arguments[11],
                (M) arguments[12],
                (N) arguments[13],
                (O) arguments[14],
                (P) arguments[15]
        );
    }
}
