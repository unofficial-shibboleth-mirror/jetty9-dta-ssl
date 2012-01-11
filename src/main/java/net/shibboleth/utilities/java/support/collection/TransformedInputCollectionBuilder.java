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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.logic.TransformAndCheckFunction;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * A builder of various types of {@link List} and {@link Set} instances that allows elements to be transformed prior to
 * being added to the collection. The transformation function may indicate that element should not be added to the
 * collection by returning {@link Optional#absent()}. In addition, the constructed collection will maintain the same
 * transformation logic after it is built.
 * 
 * @param <E> type of element
 */
@NotThreadSafe
public class TransformedInputCollectionBuilder<E> {

    /** Collection of entries added to builder. */
    private final ArrayList<E> entries;

    /**
     * A function applied to elements prior to being constraint checked and accepted. Default value:
     * {@link Functions#identity()}
     */
    private Function<E, ? extends E> elementPreprocessor;

    /**
     * Constraint which must be met in order for an element to be valid. Default value: {@link Predicates#notNull()}
     */
    private Predicate<E> elementConstraint;

    /**
     * Whether elements that do not meet the constraint should be cause an error or just be silently dropped. Default
     * value: false
     */
    private boolean failOnElementConstraintViolation;

    /** Transformation function used on elements. */
    private TransformAndCheckFunction<E> elementTransform;

    /** Constructor. */
    public TransformedInputCollectionBuilder() {
        entries = new ArrayList<E>();

        elementPreprocessor = Functions.identity();
        elementConstraint = Predicates.notNull();
        failOnElementConstraintViolation = false;
        elementTransform = buildElementTransform();
    }

    /**
     * Sets the function applied to elements prior to being constraint checked and accepted.
     * 
     * @param preprocessor function applied to elements prior to being constraint checked and accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputCollectionBuilder<E> preprocessor(@Nonnull Function<E, ? extends E> preprocessor) {
        elementPreprocessor = Assert.isNotNull(preprocessor, "Map key preprocessor can not be null");
        elementTransform = buildElementTransform();
        return this;
    }

    /**
     * Sets the constraint applied elements in order to be accepted.
     * 
     * @param constraint constraint applied elements in order to be accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputCollectionBuilder<E> constraint(@Nonnull Predicate<E> constraint) {
        elementConstraint = Assert.isNull(constraint, "Map key constraint can not be null");
        elementTransform = buildElementTransform();
        return this;
    }

    /**
     * Indicates that elements which are not accepted should result in an {@link IllegalArgumentException} being thrown.
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputCollectionBuilder<E> failOnConstraintViolation() {
        failOnElementConstraintViolation = true;
        elementTransform = buildElementTransform();
        return this;
    }

    /**
     * Adds an element to the collection of elements held by this builder.
     * 
     * @param element the element to be added
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputCollectionBuilder<E> add(@Nullable E element) {
        Optional<? extends E> processedElement = elementTransform.apply(element);
        if (!processedElement.isPresent()) {
            return null;
        }

        entries.add(element);

        return this;
    }

    /**
     * Adds a collection of elements to the collection of elements held by this builder.
     * 
     * @param collection the elements to be added
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputCollectionBuilder<E> addAll(@Nullable @NullableElements Collection<E> collection) {
        if (collection != null) {
            for (E element : collection) {
                add(element);
            }
        }

        return this;
    }

    /**
     * Builds a list containing the current builder entries that enforces the same element pre-processing, constraint,
     * and fail on constraint validation settings as this builder. This list is <strong>not</strong> backed by the entry
     * collection maintained by this builder so changes to entries, constraints or preprocessors made after the map is
     * constructed will not be reflected in that map.
     * 
     * @return the list
     */
    public List<E> buildList() {
        TransformedInputList<E> list = new TransformedInputList(new ArrayList<E>(), elementTransform);

        for (E element : entries) {
            list.addToDelegate(element);
        }

        return list;
    }

    /**
     * Builds an immutable list containing the current builder entries.
     * 
     * @return the list
     */
    public ImmutableList<E> buildImmutableList() {
        return ImmutableList.<E> builder().addAll(entries).build();
    }

    /**
     * Builds a set containing the current builder entries that enforces the same element pre-processing, constraint,
     * and fail on constraint validation settings as this builder. This set is <strong>not</strong> backed by the entry
     * collection maintained by this builder so changes to entries, constraints or preprocessors made after the map is
     * constructed will not be reflected in that map.
     * 
     * @return the list
     */
    public Set<E> buildSet() {
        TransformedInputSet<E> set = new TransformedInputSet(new HashSet<E>(), elementTransform);

        for (E element : entries) {
            set.addToDelegate(element);
        }

        return set;
    }

    /**
     * Builds an immutable containing the current builder entries that enforces the same element pre-processing,
     * constraint, and fail on constraint validation settings as this builder.
     * 
     * @return the list
     */
    public ImmutableSet<E> buildImmutableSet() {
        return ImmutableSet.<E> builder().addAll(entries).build();
    }

    /**
     * Builds the element transformation function based on the current {@link #elementPreprocessor},
     * {@link #elementConstraint}, and {@link #failOnElementConstraintViolation}.
     * 
     * @return the transformation function
     */
    private TransformAndCheckFunction<E> buildElementTransform() {
        return new TransformAndCheckFunction(elementPreprocessor, elementConstraint, failOnElementConstraintViolation);
    }
}