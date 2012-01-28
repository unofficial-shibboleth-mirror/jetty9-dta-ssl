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

import com.google.common.base.Predicate;

/**
 * This predicate can be used to wrap another predicate and ensure that the given predicate input is of a valid value
 * type before it is passed to the wrap context. This is especially useful when working with values coming from
 * collections that do not enforce any type restrictions.
 * 
 * <p>
 * This predicate will check its input against the registered supported value type via {@link Class#isInstance(Object)}
 * and, if the check passes, invoke the wrapped predicate. If the check does not pass and {@link #ignoreInvalidTypes}
 * this predicate returns false otherwise it throws an {@link IllegalArgumentException}.
 * </p>
 */
public class TypeCheckAndApplyPredicate implements Predicate {

    /** The wrapped predicate. */
    private final Predicate predicate;

    /** Whether attribute values that are not handled by this predicate should be ignored. */
    private final boolean ignoreInvalidTypes;

    /** Types supported by the wrapped predicate. */
    private final Class valueType;

    /**
     * Constructor.
     * 
     * @param <T> the value type
     * @param typedPredicate the wrapped predicate
     * @param validValueType type supported by the wrapped predicate
     * @param ignoreInvalidValueTypes whether attribute values that are not handled by this predicate should be ignored
     */
    public <T> TypeCheckAndApplyPredicate(Predicate<T> typedPredicate, Class<? extends T> validValueType,
            boolean ignoreInvalidValueTypes) {
        predicate = Assert.isNotNull(typedPredicate, "Typed predicate can not be null");
        valueType = validValueType;
        ignoreInvalidTypes = ignoreInvalidValueTypes;
    }

    /** {@inheritDoc} */
    public boolean apply(Object input) {
        if (valueType.isInstance(input)) {
            return predicate.apply(input);
        }

        if (!ignoreInvalidTypes) {
            throw new IllegalArgumentException("Input type of " + input.getClass().getCanonicalName()
                    + " is not supported");
        }

        return false;
    }
}