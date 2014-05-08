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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Generic predicate that checks a candidate {@link Object} returned by a lookup function
 * for containment in a {@link Collection} returned by another lookup function.
 * 
 * @param <T1> type of object used as the source of the data to compare
 * @param <T2> type of object being compared
 */
public class CollectionContainmentPredicate<T1,T2> implements Predicate<T1> {

    /** Lookup strategy for string. */
    @Nonnull private final Function<T1,T2> objectLookupStrategy;
    
    /** Lookup strategy for collection. */
    @Nonnull private final Function<T1,Collection<T2>> collectionLookupStrategy;
    
    /**
     * Constructor.
     * 
     * @param objectStrategy  lookup strategy for object
     * @param collectionStrategy    lookup strategy for collection
     */
    public CollectionContainmentPredicate(@Nonnull final Function<T1,T2> objectStrategy,
            @Nonnull final Function<T1,Collection<T2>> collectionStrategy) {
        objectLookupStrategy = Constraint.isNotNull(objectStrategy, "Object lookup strategy cannot be null");
        collectionLookupStrategy =
                Constraint.isNotNull(collectionStrategy, "Collection lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final T1 input) {
        final T2 o = objectLookupStrategy.apply(input);
        final Collection<T2> c = collectionLookupStrategy.apply(input);
        
        if (c != null && o != null) {
            return c.contains(o);
        }
        
        return false;
    }

}