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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

/**
 * A specialization of {@link ClassToInstanceMultiMap} which exposes a map-specific
 * instance of {@link ReadWriteLock}. Callers of the map are responsible for explicitly locking
 * (and unlocking) for reading and/or writing, based on application use cases and concurrency 
 * requirements.
 * 
 * @param <B> a bound for the types of values in the map
 */
public class LockableClassToInstanceMultiMap<B> extends ClassToInstanceMultiMap<B> {
    
    /** The map's read write lock. */
    @Nonnull private final ReadWriteLock readWriteLock;

    /**
     * Constructor.
     */
    public LockableClassToInstanceMultiMap() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param isIndexingSupertypes indicates whether supertypes of a value should be indexed
     */
    public LockableClassToInstanceMultiMap(final boolean isIndexingSupertypes) {
        super(isIndexingSupertypes);
        readWriteLock = new ReentrantReadWriteLock(true);
    }

    /**
     * Get the map-specific instance of the {@link ReadWriteLock}.
     * 
     * <p>
     * Callers of the map are responsible for explicitly locking (and unlocking)
     * for reading and/or writing, based on application use cases.
     * </p>
     * 
     * @return Returns the rwlock.
     */
    @Nonnull public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

}