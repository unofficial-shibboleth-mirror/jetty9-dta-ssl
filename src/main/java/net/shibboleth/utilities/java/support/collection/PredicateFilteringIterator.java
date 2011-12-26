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
import java.util.NoSuchElementException;

import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;
import net.shibboleth.utilities.java.support.annotation.constraint.NotThreadSafe;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.logic.Predicate;

/**
 * <p>
 * This implementation of {@link Iterator} wraps another {@link Iterator} of a particular type, containing candidates
 * which are to be evaluated against a given {@link Predicate}. When the iterator is traversed, predicate evaluation is
 * performed on each candidate element of the underlying wrapped iterator via {@link Predicate#apply(Object)}. Only
 * those elements which satisfy the predicate are returned by this Iterator.
 * </p>
 * 
 * Note, this iterator does not support the use of the {@link #remove()} operation.
 * 
 * @param <T> the type of candidate elements being evaluated
 */
@NotThreadSafe
public class PredicateFilteringIterator<T> implements Iterator<T> {

    /** The candidates to evaluate. */
    private final Iterator<? extends T> candidates;

    /** The candidate selector. */
    private final Predicate<T> selector;

    /** The current candidate which will be returned by the next call to next(). */
    private T current;

    /**
     * Constructor.
     * 
     * @param candidatesIterator the candidates to evaluate
     * @param selectionPredicate the predicate that determines if a candidate element is selected
     */
    public PredicateFilteringIterator(@NotNull final Iterator<? extends T> candidatesIterator,
            @NotNull final Predicate<T> selectionPredicate) {
        candidates = Assert.isNotNull(candidatesIterator, "Candidate element iterator can not be null");
        selector = Assert.isNotNull(selectionPredicate, "Candidate element selection predicate can not be null");
        current = null;
    }

    /** {@inheritDoc} */
    public boolean hasNext() {
        if (current != null) {
            return true;
        }

        current = getNextMatch();
        if (current != null) {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public T next() {
        T temp;

        if (current != null) {
            temp = current;
            current = null;
            return temp;
        }

        temp = getNextMatch();
        if (temp != null) {
            return temp;
        } else {
            throw new NoSuchElementException("No more elements are available");
        }
    }

    /** {@inheritDoc} */
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported by this iterator");
    }

    /**
     * Get the next matching candidate.
     * 
     * @return the next matching candidate
     */
    private T getNextMatch() {
        while (candidates.hasNext()) {
            final T candidate = candidates.next();
            if (match(candidate)) {
                return candidate;
            }
        }

        return null;
    }

    /**
     * Evaluate the candidate against all the criteria.
     * 
     * @param candidate the candidate to evaluate
     * 
     * @return true if the candidate satisfies the set of criteria, false otherwise
     */
    protected boolean match(T candidate) {
        return selector.apply(candidate);
    }
}