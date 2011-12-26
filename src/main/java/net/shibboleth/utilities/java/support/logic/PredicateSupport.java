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

package net.shibboleth.utilities.java.support.logic;

import java.util.Collection;

import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;

/**
 * Helper methods for working with {@link Predicate}s.
 * 
 * Note, static importing the methods herein that produce {@link Predicate}, some fairly succinct evaluation code can be
 * written.
 */
public final class PredicateSupport {

    /** Constructor. */
    private PredicateSupport() {

    }

    /**
     * Produces a predicate that always evaluates to false.
     * 
     * @param <T> type of argument upon which the predicate operates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> alwaysFalse() {
        return AlwaysFalsePredicate.INSTANCE;
    }

    /**
     * Produces a predicate that always evaluates to true.
     * 
     * @param <T> type of argument upon which the predicate operates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> alwaysTrue() {
        return AlwaysTruePredicate.INSTANCE;
    }

    /**
     * Produces a predicate that returns true if, and only if, all composed predicates return true.
     * 
     * @param <T> type of argument upon which the predicate operates
     * @param predicates composed predicates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> and(@NotNull final Predicate<? super T>... predicates) {
        return new AndPredicate<T>(predicates);
    }

    /**
     * Produces a predicate that returns true if its argument matches the given target.
     * 
     * @param <T> type of argument upon which the predicate operates
     * @param target the target to check the argument against
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> isEqual(@NotNull final Object target) {
        return new IsEqualPredicate(target);
    }

    /**
     * Produces a predicate that returns true if the argument is in the target collection.
     * 
     * @param <T> type of argument upon which the predicate operates
     * @param target the target collection
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> isInCollection(Collection<? extends T> target) {
        return new IsInCollectionPredicate(target);
    }

    /**
     * Produces a predicate that return true if its argument is not null.
     * 
     * @param <T> type of argument upon which the predicate operates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> isNotNull() {
        return IsNotNullPredicate.INSTANCE;
    }

    /**
     * Produces a predicate that returns true if its argument is null.
     * 
     * @param <T> type of argument upon which the predicate operates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> isNull() {
        return IsNullPredicate.INSTANCE;
    }

    /**
     * Produces a predicate that negates the result of the supplied predicate.
     * 
     * @param <T> type of argument upon which the predicate operates
     * @param predicate the supplied predicate
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> not(Predicate<T> predicate) {
        return new NotPredicate(predicate);
    }

    /**
     * Produces a predicate that returns true if any composed predicate returns true.
     * 
     * @param <T> type of argument upon which the predicate operates
     * @param predicates the composed predicates
     * 
     * @return the produced predicate
     */
    public <T> Predicate<T> or(Predicate<? super T>... predicates) {
        return new OrPredicate<T>(predicates);
    }
}