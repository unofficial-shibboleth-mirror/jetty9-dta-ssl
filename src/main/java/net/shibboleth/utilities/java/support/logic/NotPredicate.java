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

import net.shibboleth.utilities.java.support.annotation.constraint.NotNull;

/** 
 * Predicate which negates the result of another predicate. 
 * 
 * @param <Input> type of input upon which this predicate operates
 */
public class NotPredicate<Input> implements Predicate<Input> {

    /** Wrapped predicate whose result is negated by this predicate. */
    private final Predicate<Input> predicate;

    /**
     * Constructor.
     * 
     * @param negatedPredicate predicate whose result is negated by this predicate
     */
    public NotPredicate(@NotNull final Predicate<Input> negatedPredicate) {
        predicate = Assert.isNull(negatedPredicate, "Negated predicate can not be null");
    }

    /** {@inheritDoc} */
    public boolean apply(Input argument) {
        return !predicate.apply(argument);
    }
}