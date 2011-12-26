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

import net.shibboleth.utilities.java.support.annotation.constraint.NoNullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;

/**
 * A predicate that returns false if any composed predicate returns false. Note, this predicate does perform a
 * short-circuit evaluation so predicates occurring after the first predicate to return <code>false</code> will not be
 * evaluated.
 * 
 * @param <Input> type of argument
 */
public class AndPredicate<Input> implements Predicate<Input> {

    /** Predicates composed by a logical AND. */
    private final Predicate<? super Input>[] predicates;

    /**
     * Constructor.
     * 
     * @param composedPredicates predicates composed by a logical AND
     */
    public AndPredicate(@NotNull @NoNullElements final Predicate<? super Input>... composedPredicates) {
        Assert.isNotNull(composedPredicates, "Composed predicates can not be null");
        Assert.isGreaterThan(0, composedPredicates.length, "Composed predicate array must contain at least one element");
        predicates = Assert.noNullItems(composedPredicates, "Composed predicate array can not contain null elements");
    }

    /** {@inheritDoc} */
    public boolean apply(Input argument) {
        for (Predicate<? super Input> predicate : predicates) {
            if (!predicate.apply(argument)) {
                return false;
            }
        }

        return true;
    }
}