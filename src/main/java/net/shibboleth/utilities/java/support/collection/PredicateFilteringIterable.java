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

import java.util.Iterator;

import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.logic.Predicate;

/**
 * An implementation of {@link Iterable} which wraps another underlying {@link Iterable} in order to support production
 * of instances of {@link PredicateFilteringIterator} based on the underlying Iterable's Iterator.
 * 
 * For iterator behavior and meaning and use of the parameters, see {@link PredicateFilteringIterator}.
 * 
 * @param <T> the type of candidate elements being evaluated
 */
public class PredicateFilteringIterable<T> implements Iterable<T> {

    /** The candidates to evaluate. */
    private final Iterable<? extends T> candidates;

    /** The set of criteria against which to evaluate the candidates. */
    private final Predicate<T> selector;

    /**
     * Constructor.
     * 
     * @param candidatesIterator the candidates to evaluate
     * @param selectionPredicate the predicate that determines if a candidate element is selected
     */
    public PredicateFilteringIterable(@NotNull final Iterable<? extends T> candidatesIterator,
            @NotNull final Predicate<T> selectionPredicate) {
        candidates = Assert.isNotNull(candidatesIterator, "Candidate element iterator can not be null");
        selector = Assert.isNotNull(selectionPredicate, "Candidate element selection predicate can not be null");
    }

    /** {@inheritDoc} */
    public Iterator<T> iterator() {
        return new PredicateFilteringIterator<T>(candidates.iterator(), selector);
    }
}