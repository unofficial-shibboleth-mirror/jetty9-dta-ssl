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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Assert;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingSet;

//TODO(lajoie) think about Java serialization, can we mark this as serializable? what happens if the function/delegate aren't?

/**
 * A {@link Set} decorator that allows input to be transformed prior to being added to the list. In addition the
 * transformation function may indicate that a element should not be added to the list by returning
 * {@link Optional#absent()}.
 * 
 * @param <E> type element added to the list
 */
public class TransformedInputSet<E> extends ForwardingSet<E> {

    /** The decorated list. */
    private final Set<E> delegate;

    /** A function applied to elements prior to being accepted. */
    private final Function<E, Optional<? extends E>> transform;

    /**
     * Constructor.
     * 
     * @param decoratedSet the list to be decorated, this list is cleared during decorator construction
     * @param elementTransform function applied to elements prior to being accepted
     */
    public TransformedInputSet(@Nonnull Set<E> decoratedSet,
            @Nonnull Function<E, Optional<? extends E>> elementTransform) {
        transform = Assert.isNotNull(elementTransform, "Element transform can not be null");
        delegate = Assert.isNotNull(decoratedSet, "Delegate set can not be null");
        delegate.clear();
    }

    /** {@inheritDoc} */
    public boolean add(@Nullable E element) {
        Optional<? extends E> processedElement = transform.apply(element);

        if (processedElement.isPresent()) {
            return delegate().add(processedElement.get());
        }

        return false;
    }

    /** {@inheritDoc} */
    public boolean addAll(@Nullable @NullableElements Collection<? extends E> collection) {
        return standardAddAll(collection);
    }

    /** {@inheritDoc} */
    public boolean retainAll(Collection<?> collection) {
        return standardRetainAll(collection);
    }

    /** {@inheritDoc} */
    protected Set<E> delegate() {
        return delegate;
    }
    
    /**
     * Adds an element to decorated set without passing it through the element transform function. This should only be
     * done if the transform function or functionally identical processing was performed to the element prior to
     * invoking this method.
     * 
     * @param element the element to add
     */
    protected void addToDelegate(@Nullable E element) {
        delegate.add(element);
    }
}