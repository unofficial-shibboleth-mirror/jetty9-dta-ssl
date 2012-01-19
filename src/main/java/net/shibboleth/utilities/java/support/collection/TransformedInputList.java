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
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Assert;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;

//TODO(lajoie) think about Java serialization, can we mark this as serializable? what happens if the function/delegate aren't?

/**
 * A {@link List} decorator that allows input to be transformed prior to being added to the list. In addition the
 * transformation function may indicate that a element should not be added to the list by returning
 * {@link Optional#absent()}.
 * 
 * @param <E> type element added to the list
 */
public class TransformedInputList<E> extends ForwardingList<E> {

    /** The decorated list. */
    private final List<E> delegate;

    /** A function applied to elements prior to being accepted. */
    private final Function<E, Optional<? extends E>> transform;

    /**
     * Constructor.
     * 
     * @param decoratedList the list to be decorated, this list is cleared during decorator construction
     * @param elementTransform function applied to elements prior to being accepted
     */
    public TransformedInputList(@Nonnull List<E> decoratedList,
            @Nonnull Function<E, Optional<? extends E>> elementTransform) {
        transform = Assert.isNotNull(elementTransform, "Element transform can not be null");
        delegate = Assert.isNotNull(decoratedList, "Created delegate list can not be null");
        delegate.clear();
    }

    /** {@inheritDoc} */
    public boolean add(@Nullable E element) {
        return standardAdd(element);
    }

    /** {@inheritDoc} */
    public void add(int index, E element) {
        Optional<? extends E> processedElement = transform.apply(element);

        if (processedElement.isPresent()) {
            delegate().add(index, processedElement.get());
        }
    }

    /** {@inheritDoc} */
    public boolean addAll(@Nullable @NullableElements Collection<? extends E> collection) {
        return standardAddAll(collection);
    }

    /** {@inheritDoc} */
    public boolean addAll(int index, Collection<? extends E> elements) {
        return standardAddAll(index, elements);
    }

    /** {@inheritDoc} */
    public boolean retainAll(Collection<?> collection) {
        return standardRetainAll(collection);
    }

    /** {@inheritDoc} */
    public E set(int index, E element) {
        Optional<? extends E> processedElement = transform.apply(element);

        if (processedElement.isPresent()) {
            return delegate().set(index, processedElement.get());
        }

        return null;
    }
    
    /** {@inheritDoc} */
    public ListIterator<E> listIterator() {
        return standardListIterator();
    }
    
    /** {@inheritDoc} */
    public ListIterator<E> listIterator(int index) {
        return new DelegatingListIterator(index);
    }


    /** {@inheritDoc} */
    public List<E> subList(int fromIndex, int toIndex) {
        return standardSubList(fromIndex, toIndex);
    }

    /** {@inheritDoc} */
    protected List<E> delegate() {
        return delegate;
    }

    /**
     * Adds an element to decorated list without passing it through the element transform function. This should only be
     * done if the transform function or functionally identical processing was performed to the element prior to
     * invoking this method.
     * 
     * @param element the element to add
     */
    protected void addToDelegate(@Nullable E element) {
        delegate.add(element);
    }

    /**
     * We need to implement a list iterator to override add and set.
     */
    protected class DelegatingListIterator extends ForwardingListIterator<E> {

        
        /**
         * The delegated ListIterator.
         */
        private final ListIterator<E> innerDelegate;
        
        /**
         * Constructor.
         *
         * @param index where in the outer class to start the iterator.
         */
        protected DelegatingListIterator(int index) {
            innerDelegate = delegate.listIterator(index);
        }
        
        /** {@inheritDoc} */
        protected ListIterator<E> delegate() {
            return innerDelegate;
        }
        
        @Override
        public void add(E element) {
            Optional<? extends E> processedElement = transform.apply(element);

            if (processedElement.isPresent()) {
                delegate().add(processedElement.get());
            }
        }
        
        @Override
        public void set(E element) {
            Optional<? extends E> processedElement = transform.apply(element);

            if (processedElement.isPresent()) {
                delegate().set(element);
            }
        }


    }
}