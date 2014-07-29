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

package net.shibboleth.utilities.java.support.security;

import java.security.KeyException;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;

/**
 * Interface for acquiring secret keys on behalf of the {@link DataSealer} class,
 * this principally abstracts methods of handling key versioning moreso than actual
 * access to keys, by optimizing access to keys in accordance with the manner in which
 * key rollover is handled.
 * 
 * <p>Implementations are expected to guarantee good performance for access to a current
 * "default" key, and may offer less performance on access to non-default keys.</p>
 */
public interface DataSealerKeyStrategy {

    /**
     * Get the default/current key to use for new operations, returned along with an identifier for it.
     * 
     * @return  the key
     * @throws KeyException if the key cannot be returned
     */
    @Nonnull Pair<String,SecretKey> getDefaultKey() throws KeyException;
    
    /**
     * Get a specifically named key.
     * 
     * @param name name of the key to retrieve
     * 
     * @return  the key
     * @throws KeyException if the key cannot be returned, does not exist, etc.
     */
    @Nonnull SecretKey getKey(@Nonnull @NotEmpty final String name) throws KeyException;
    
}