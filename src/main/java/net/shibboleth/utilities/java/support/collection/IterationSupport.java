/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.utilities.java.support.collection;

import java.util.Collection;
import java.util.Iterator;

import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;
import net.shibboleth.utilities.java.support.logic.Assert;

/** Support functions for working with {@link Iterable} and {@link Iterator} instances. */
public final class IterationSupport {

    /** Constructor. */
    private IterationSupport() {

    }

    /**
     * Adds all the items produced by an {@link Iterable} in to a given collection.
     * 
     * @param <T> type of element produced by the iterable and accepted by the collection
     * @param source the iterable that produces the elements
     * @param target the collection that receives the elements
     */
    public static <T> void
            toCollection(@NotNull final Iterable<? extends T> source, @NotNull final Collection<T> target) {
        Assert.isNotNull(source, "Element source iterable can not be null");
        Assert.isNotNull(target, "Target collection can not be null");

        for (T element : source) {
            target.add(element);
        }
    }

    /**
     * Adds all the items produced by an {@link Iterator} in to a given collection.
     * 
     * @param <T> type of element produced by the iterator and accepted by the collection
     * @param source the iterator that produces the elements
     * @param target the collection that receives the elements
     */
    public static <T> void
            toCollection(@NotNull final Iterator<? extends T> source, @NotNull final Collection<T> target) {
        Assert.isNotNull(source, "Element source iterable can not be null");
        Assert.isNotNull(target, "Target collection can not be null");

        while (source.hasNext()) {
            target.add(source.next());
        }
    }
}