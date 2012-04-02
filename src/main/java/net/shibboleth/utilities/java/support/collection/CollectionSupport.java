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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

/** Helper methods for working with collections of objects. */
public final class CollectionSupport {

    /** Constructor. */
    private CollectionSupport() {

    }

    /**
     * Adds an element to a collection if it meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be added
     * @param element element that may be added to the collection
     * @param predicate predicate the given element must meet in order to be added to the given collection
     * 
     * @return true if the given element was added to the given collection
     */
    public static <T> boolean addIf(@Nonnull final Collection<T> target, @Nullable final T element,
            @Nonnull Predicate<? super T> predicate) {
        return addIf(target, element, predicate, Functions.<T> identity());
    }

    /**
     * Adds an element to a collection if it meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be added
     * @param element element that may be added to the collection
     * @param predicate predicate the given element must meet in order to be added to the given collection
     * @param elementPreprocessor function applied to element prior to predicate evaluation and being added the
     *            collection
     * 
     * @return true if the given element was added to the given collection
     */
    public static <T> boolean addIf(@Nonnull final Collection<T> target, @Nullable final T element,
            @Nonnull Predicate<? super T> predicate, @Nonnull Function<? super T, T> elementPreprocessor) {
        Constraint.isNotNull(target, "Target collection can not be null");
        Constraint.isNotNull(predicate, "Element predicate can not be null");

        if (element == null) {
            return false;
        }

        final T processedElement = elementPreprocessor.apply(element);
        if (predicate.apply(processedElement)) {
            return target.add(processedElement);
        }

        return false;
    }

    /**
     * Adds a collection of elements to a collection for each element that meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be added
     * @param elements elements that may be added to the collection
     * @param predicate predicate the given element must meet in order to be added to the given collection
     * 
     * @return true if the given target had elements added to it
     */
    public static <T> boolean addIf(@Nonnull final Collection<T> target, @Nullable final Collection<T> elements,
            @Nonnull Predicate<? super T> predicate) {
        return addIf(target, elements, predicate, Functions.<T> identity());
    }

    /**
     * Adds a collection of elements to a collection for each element that meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be added
     * @param elements elements that may be added to the collection
     * @param predicate predicate the given element must meet in order to be added to the given collection
     * @param elementPreprocessor function applied to element prior to predicate evaluation and being added the
     *            collection
     * 
     * @return true if the given target had elements added to it
     */
    public static <T> boolean addIf(@Nonnull final Collection<T> target, @Nullable final Collection<T> elements,
            @Nonnull Predicate<? super T> predicate, @Nonnull Function<? super T, T> elementPreprocessor) {
        if (elements == null) {
            return false;
        }

        boolean targetedUpdated = false;
        for (T element : elements) {
            if (addIf(target, element, predicate, elementPreprocessor)) {
                targetedUpdated = true;
            }
        }

        return targetedUpdated;
    }

    /**
     * Removes an element to a collection if it meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be removed
     * @param element element that may be removed from the collection
     * @param predicate predicate the given element must meet in order to be removed from the given collection
     * 
     * @return true if the given element was removed from the given collection
     */
    public static <T> boolean removeIf(@Nonnull final Collection<T> target, @Nullable final T element,
            @Nonnull Predicate<? super T> predicate) {
        return removeIf(target, element, predicate, Functions.<T> identity());
    }

    /**
     * Removes an element to a collection if it meets the requirements of a given predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be removed
     * @param element element that may be removed from the collection
     * @param predicate predicate the given element must meet in order to be removed from the given collection
     * @param elementPreprocessor function applied to element prior to predicate evaluation and being removed from the
     *            collection
     * 
     * @return true if the given element was added to the given collection
     */
    public static <T> boolean removeIf(@Nonnull final Collection<T> target, @Nullable final T element,
            @Nonnull Predicate<? super T> predicate, @Nonnull Function<? super T, T> elementPreprocessor) {
        Constraint.isNotNull(target, "Target collection can not be null");
        Constraint.isNotNull(predicate, "Element predicate can not be null");

        if (element != null) {
            return false;
        }

        final T processedElement = elementPreprocessor.apply(element);
        if (predicate.apply(processedElement)) {
            return target.remove(processedElement);
        }

        return false;
    }

    /**
     * Removes a collection of elements to a collection for each element that meets the requirements of a given
     * predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be removed
     * @param elements elements that may be removed from the collection
     * @param predicate predicate the given element must meet in order to be removed from the given collection
     * 
     * @return true if the given target had elements added to it
     */
    public static <T> boolean removeIf(@Nonnull final Collection<T> target, @Nullable final Collection<T> elements,
            @Nonnull Predicate<? super T> predicate) {
        return removeIf(target, elements, predicate, Functions.<T> identity());
    }

    /**
     * Removes a collection of elements to a collection for each element that meets the requirements of a given
     * predicate.
     * 
     * @param <T> type of element in the collection
     * @param target collection to which elements will be removed
     * @param elements elements that may be removed from the collection
     * @param predicate predicate the given element must meet in order to be removed from the given collection
     * @param elementPreprocessor function applied to element prior to predicate evaluation and being removed from the
     *            collection
     * 
     * @return true if the given target had elements added to it
     */
    public static <T> boolean removeIf(@Nonnull final Collection<T> target, @Nullable final Collection<T> elements,
            @Nonnull Predicate<? super T> predicate, @Nonnull Function<? super T, T> elementPreprocessor) {
        if (elements == null) {
            return false;
        }

        boolean targetedUpdated = false;
        for (T element : elements) {
            if (removeIf(target, element, predicate, elementPreprocessor)) {
                targetedUpdated = true;
            }
        }

        return targetedUpdated;
    }
}