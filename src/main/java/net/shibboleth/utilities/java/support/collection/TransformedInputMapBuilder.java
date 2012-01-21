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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * A builder of various types of {@link Map} instances that allows keys and values to be transformed prior to being
 * added to the list. In addition the transformation function may indicate that key or value should not be added to the
 * list by returning {@link Optional#absent()}. If either the key or the value should not be added than neither is
 * added.
 * 
 * @param <K> type of keys added to the constructed map
 * @param <V> type of values added to the constructed map
 */
@NotThreadSafe
public class TransformedInputMapBuilder<K, V> {

    /** Collection of entries added to builder. */
    private final ArrayList<Pair<K, V>> entries;

    /**
     * A function applied to keys prior to being constraint checked and accepted. Default value:
     * {@link Functions#identity()}
     */
    private Function<K, ? extends K> keyPreprocessor;

    /**
     * Constraint which must be met in order for the key to be valid for the constructed map. Default value:
     * {@link Predicates#notNull()}
     */
    private Predicate<K> keyConstraint;

    /**
     * Whether entries whose keys do not meet the key constraint should be cause an error or just be silently dropped.
     * Default value: false
     */
    private boolean failOnKeyConstraintViolation;

    /** Transformation function used on map keys. */
    private TransformAndCheckFunction<K> keyTransform;

    /**
     * A function applied to values prior to being constraint checked and accepted. Default value:
     * {@link Functions#identity()}
     */
    private Function<V, ? extends V> valuePreprocessor = Functions.identity();

    /**
     * Constraint which must be met in order for the value to be valid for the constructed map. Default value:
     * {@link Predicates#notNull()}
     */
    private Predicate<V> valueConstraint = Predicates.notNull();

    /**
     * Whether entries whose keys do not meet the key constraint should be cause an error or just be silently dropped.
     * Default value: false
     */
    private boolean failOnValueConstraintViolation;

    /** Transformation function used on map values. */
    private TransformAndCheckFunction<V> valueTransform;

    /** Constructor. */
    public TransformedInputMapBuilder() {
        entries = new ArrayList<Pair<K, V>>();

        keyPreprocessor = Functions.identity();
        keyConstraint = Predicates.notNull();
        failOnKeyConstraintViolation = false;
        keyTransform = buildKeyTransform();

        valuePreprocessor = Functions.identity();
        valueConstraint = Predicates.notNull();
        failOnValueConstraintViolation = false;
        valueTransform = buildValueTransform();
    }

    /**
     * Sets the function applied to keys prior to being constraint checked and accepted.
     * 
     * @param preprocessor function applied to keys prior to being constraint checked and accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> keyPreprocessor(@Nonnull Function<K, ? extends K> preprocessor) {
        keyPreprocessor = Assert.isNotNull(preprocessor, "Map key preprocessor can not be null");
        keyTransform = buildKeyTransform();
        return this;
    }

    /**
     * Sets the constraint applied keys in order to be accepted.
     * 
     * @param constraint constraint applied keys in order to be accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> keyConstraint(@Nonnull Predicate<K> constraint) {
        keyConstraint = Assert.isNull(constraint, "Map key constraint can not be null");
        keyTransform = buildKeyTransform();
        return this;
    }

    /**
     * Indicates that keys which are not accepted should result in an {@link IllegalArgumentException} being thrown.
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> failOnKeyConstraintViolation() {
        failOnKeyConstraintViolation = true;
        keyTransform = buildKeyTransform();
        return this;
    }

    /**
     * Sets the function applied to values prior to being constraint checked and accepted.
     * 
     * @param preprocessor function applied to values prior to being constraint checked and accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> valuePreprocessor(@Nonnull Function<V, ? extends V> preprocessor) {
        valuePreprocessor = Assert.isNotNull(preprocessor, "Map value preprocessor can not be null");
        valueTransform = buildValueTransform();
        return this;
    }

    /**
     * Sets the constraint applied values in order to be accepted.
     * 
     * @param constraint constraint applied keys in order to be accepted
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> valueConstraint(@Nonnull Predicate<V> constraint) {
        valueConstraint = Assert.isNull(constraint, "Map value constraint can not be null");
        valueTransform = buildValueTransform();
        return this;
    }

    /**
     * Indicates that values which are not accepted should result in an {@link IllegalArgumentException} being thrown.
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> failOnValueConstraintViolation() {
        failOnValueConstraintViolation = true;
        valueTransform = buildValueTransform();
        return this;
    }

    /**
     * Adds a key/value pair that will be placed in the constructed map.
     * 
     * @param key the key to be added
     * @param value the value to added
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> put(@Nullable K key, @Nullable V value) {
        Optional<? extends K> processedKey = keyTransform.apply(key);
        if (!processedKey.isPresent()) {
            return null;
        }

        Optional<? extends V> processedValue = valueTransform.apply(value);
        if (!processedValue.isPresent()) {
            return null;
        }

        entries.add(new Pair<K, V>(processedKey.get(), processedValue.get()));

        return this;
    }

    /**
     * Adds all the key/value pair from the given map.
     * 
     * @param map the map containing key/value pairs to be added
     * 
     * @return this builder
     */
    @Nonnull public TransformedInputMapBuilder<K, V> putAll(
            @Nullable @NullableElements Map<? extends K, ? extends V> map) {
        if (map != null) {
            for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        return this;
    }

    /**
     * Constructs a {@link Map} containing the entries that have been added so far. This map is <strong>not</strong>
     * backed by the entry collection maintained by this builder so changes to entries, constraints or preprocessors
     * made after the map is constructed will not be reflected in that map.
     * 
     * @return the constructed map
     */
    @Nonnull public Map<K, V> buildMap() {
        TransformedInputMap<K, V> map = new TransformedInputMap(new HashMap<K, V>(), keyTransform, valueTransform);

        for (Pair<K, V> entry : entries) {
            map.putToDelegate(entry.getFirst(), entry.getSecond());
        }

        return map;
    }

    /**
     * Constructs a {@link ConcurrentMap} containing the entries that have been added so far. This map is
     * <strong>not</strong> backed by the entry collection maintained by this builder so changes to entries, constraints
     * or preprocessors made after the map is constructed will not be reflected in that map.
     * 
     * @return the constructed map
     */
    @Nonnull public ConcurrentMap<K, V> buildConcurrentMap() {
        TransformedInputConcurrentMap<K, V> map =
                new TransformedInputConcurrentMap(new ConcurrentHashMap<K, V>(), keyTransform, valueTransform);

        for (Pair<K, V> entry : entries) {
            map.addToDelegate(entry.getFirst(), entry.getSecond());
        }

        return map;
    }

    /**
     * Constructs an {@link ImmutablerMap} containing the entries that have been added so far. This map is
     * <strong>not</strong> backed by the entry collection maintained by this builder so changes to entries, constraints
     * or preprocessors made after the map is constructed will not be reflected in that map.
     * 
     * @return the constructed map
     */
    @Nonnull public ImmutableMap<K, V> buildImmutableMap() {
        Builder<K, V> builder = new Builder<K, V>();
        for (Pair<K, V> entry : entries) {
            builder.put(entry.getFirst(), entry.getSecond());
        }

        return builder.build();
    }

    /**
     * Builds the key transformation function based on the current {@link #keyPreprocessor}, {@link #keyConstraint}, and
     * {@link #failOnKeyConstraintViolation}.
     * 
     * @return the transformation function
     */
    private TransformAndCheckFunction<K> buildKeyTransform() {
        return new TransformAndCheckFunction(keyPreprocessor, keyConstraint, failOnKeyConstraintViolation);
    }

    /**
     * Builds the value transformation function based on the current {@link #valuePreprocessor},
     * {@link #valueConstraint}, and {@link #failOnValueConstraintViolation}.
     * 
     * @return the transformation function
     */
    private TransformAndCheckFunction<V> buildValueTransform() {
        return new TransformAndCheckFunction(valuePreprocessor, valueConstraint, failOnValueConstraintViolation);
    }
}