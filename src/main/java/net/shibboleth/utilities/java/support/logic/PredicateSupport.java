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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Helper class for constructing predicates. Especially useful for creating internal DSLs via Java's static method
 * import mechanism.
 */
public final class PredicateSupport {

    /** Constructor. */
    private PredicateSupport() {
    }

    /**
     * Creates a predicate that checks that all elements of an {@link Iterable} matches a given target predicate.
     * 
     * @param <T> type of objects in the iterable and that the target operates upon
     * @param target predicate used to check each element in the iterable
     * 
     * @return the constructed predicate
     */
    @Nonnull public static <T> Predicate<Iterable<T>> allMatch(@Nonnull final Predicate<T> target) {
        return new AllMatchPredicate<>(target);
    }

    /**
     * Creates a predicate that checks that any element in an {@link Iterable} matches a given target predicate.
     * 
     * @param <T> type of objects in the iterable and that the target operates upon
     * @param target predicate used to check each element in the iterable
     * 
     * @return the constructed predicate
     */
    @Nonnull public static <T> Predicate<Iterable<T>> anyMatch(@Nonnull final Predicate<T> target) {
        return new AnyMatchPredicate<>(target);
    }

    /**
     * Creates a predicate that checks if a given {@link CharSequence} matches a target string while ignoring case.
     * 
     * @param target the target string to match against
     * 
     * @return the constructed predicate
     */
    @Nonnull public static Predicate<CharSequence> caseInsensitiveMatch(@Nonnull final String target) {
        return new CaseInsensitiveStringMatchPredicate(target);
    }
    
    /**
     * Creates a predicate that applies a function to an input and returns its result, or a default value
     * if null.
     * 
     * @param <T> type of function input
     * 
     * @param function function to apply to input
     * @param defValue default value if function returns null
     * 
     * @return a predicate adapter 
     */
    @Nonnull public static <T> Predicate<T> fromFunction(@Nonnull final Function<T,Boolean> function,
            final boolean defValue) {
        return new Predicate<T>() {
            public boolean apply(@Nullable final T input) {
                final Boolean result = function.apply(input);
                return result != null ? result : defValue;
            }
        };
    }
    
}