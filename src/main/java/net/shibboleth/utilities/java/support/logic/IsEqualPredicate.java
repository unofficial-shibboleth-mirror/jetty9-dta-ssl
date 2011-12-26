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

import net.shibboleth.utilities.java.support.primitive.ObjectSupport;

/**
 * Predicate that checks if the given argument is equal to a target object. Equality is checked by means of the
 * {@link ObjectSupport#equals(Object)} method.
 */
public class IsEqualPredicate implements Predicate {

    /** Target against which the argument is checked. */
    private final Object target;

    /**
     * Constructor.
     * 
     * @param targetObject target against which the predicate argument is checked
     */
    public IsEqualPredicate(final Object targetObject) {
        target = targetObject;
    }

    /** {@inheritDoc} */
    public boolean apply(Object argument) {
        return ObjectSupport.equals(argument, target);
    }
}