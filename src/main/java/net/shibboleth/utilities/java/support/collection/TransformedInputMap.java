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

import java.util.Map;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Assert;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;

//TODO(lajoie) think about Java serialization, can we mark this as serializable? what happens if the function/delegate aren't?

/**
 * A {@link Map} decorator that allows keys and values to be transformed prior to being added to the list. In addition
 * the transformation function may indicate that key or value should not be added to the list by returning
 * {@link Optional#absent()}. If either the key or the value should not be added than neither is added.
 * 
 * @param <K> type of keys added to this map
 * @param <V> type of values added to this map
 */
public class TransformedInputMap<K, V> extends ForwardingMap<K, V> {

    /** The decorated list. */
    private final Map<K, V> delegate;

    /** A function applied to keys prior to being accepted. */
    private final Function<K, Optional<? extends K>> keyTransform;

    /** A function applied to values prior to being accepted. */
    private final Function<V, Optional<? extends V>> valueTransform;

    /**
     * Constructor.
     * 
     * @param decoratedMap the map to be decorated, this map is cleared upon construction of this decorator
     * @param keyTransformer a function applied to keys prior to being accepted
     * @param valueTransformer a function applied to values prior to being accepted
     */
    public TransformedInputMap(@Nonnull Map<K, V> decoratedMap,
            @Nonnull Function<K, Optional<? extends K>> keyTransformer,
            @Nonnull Function<V, Optional<? extends V>> valueTransformer) {
        delegate = Assert.isNotNull(decoratedMap, "Decrated map can not be null");
        keyTransform = Assert.isNotNull(keyTransformer, "Map key transformation function can not be null");
        valueTransform = Assert.isNotNull(valueTransformer, "Map value transformation function can not be null");

        delegate.clear();
    }

    /** {@inheritDoc} */
    protected Map<K, V> delegate() {
        return delegate;
    }

    /** {@inheritDoc} */
    public V put(K key, V value) {
        Optional<? extends K> processedKey = keyTransform.apply(key);
        if (!processedKey.isPresent()) {
            return null;
        }

        Optional<? extends V> processedValue = valueTransform.apply(value);
        if (!processedValue.isPresent()) {
            return null;
        }

        return delegate.put(processedKey.get(), processedValue.get());
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return;
        }

        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds an entry to decorated map without passing it through the key/value transform functions. This should only be
     * done if the transform function or functionally identical processing was performed to the key and value prior to
     * invoking this method.
     * 
     * @param key key added to the delegate
     * @param value value added to the delegate
     */
    protected void addToDelegate(K key, V value) {
        delegate.put(key, value);
    }
}