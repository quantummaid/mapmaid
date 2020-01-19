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

package de.quantummaid.mapmaid.mapper.definitions.validation.statemachine.proxy;

import de.quantummaid.mapmaid.builder.resolving.Transition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static de.quantummaid.mapmaid.mapper.definitions.validation.statemachine.proxy.Delegate.delegate;
import static de.quantummaid.mapmaid.shared.validators.NotNullValidator.validateNotNull;
import static java.lang.reflect.Proxy.isProxyClass;
import static java.lang.reflect.Proxy.newProxyInstance;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StateProxy implements InvocationHandler {
    private final Class<?> interfaceType;
    private final Delegate delegate;

    @SuppressWarnings("unchecked")
    public static <T> T createStateProxyIfNotAlready(final Class<T> interfaceType,
                                                     final T initialDelegate,
                                                     final Consumer<T> onEnter) {
        return (T) untypedCreateStateProxyIfNotAlready(interfaceType, initialDelegate, (Consumer<Object>) onEnter);
    }

    private static Object untypedCreateStateProxyIfNotAlready(final Class<?> interfaceType,
                                                              final Object initialDelegate,
                                                              final Consumer<Object> onEnter) {
        if (isProxyClass(initialDelegate.getClass())) {
            return initialDelegate;
        } else {
            return untypedCreateStateProxy(interfaceType, initialDelegate, onEnter);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createStateProxy(final Class<T> interfaceType,
                                         final T initialDelegate,
                                         final Consumer<T> onEnter) {
        return (T) untypedCreateStateProxy(interfaceType, initialDelegate, (Consumer<Object>) onEnter);
    }

    private static Object untypedCreateStateProxy(final Class<?> interfaceType,
                                                  final Object initialDelegate,
                                                  final Consumer<Object> onEnter) {
        validateNotNull(interfaceType, "interfaceType");
        validateNotNull(initialDelegate, "initialDelegate");
        final InvocationHandler invocationHandler = new StateProxy(interfaceType, delegate(initialDelegate, onEnter));
        return newProxyInstance(StateProxy.class.getClassLoader(),
                new Class[]{interfaceType},
                invocationHandler);
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Class<?> returnType = method.getReturnType();
        if (Transition.class.equals(returnType)) {
            final Transition<?> transition = (Transition<?>) invoke(method, args);
            this.delegate.change(transition.state());
            return transition;
        }
        if (this.interfaceType.isAssignableFrom(returnType)) {
            this.delegate.change(invoke(method, args));
            //return this.delegate; // TODO
            return null;
        }
        return invoke(method, args);
    }

    private Object invoke(final Method method, final Object[] args) throws Exception {
        try {
            return method.invoke(this.delegate, args);
        } catch (final InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        }
    }
}
