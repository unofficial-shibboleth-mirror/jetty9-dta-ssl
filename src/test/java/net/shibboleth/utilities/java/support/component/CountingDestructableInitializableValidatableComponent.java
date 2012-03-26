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

package net.shibboleth.utilities.java.support.component;

/**
 * A Component that counts the number of times {@link #destroy()}, {@link #initialize()} and {{@link #validate()} are
 * called.
 */
public class CountingDestructableInitializableValidatableComponent implements DestructableComponent,
        InitializableComponent, ValidatableComponent {

    /** Number of times {@link #destroy()} was called. */
    private int destroyCount;

    /** Number of times {@link #initialize()} was called. */
    private int initializeCount;

    /** Number of times {@link #validate()} was called. */
    private int validateCount;

    /** Whether to fail {@link #validate()} calls. */
    private boolean failValidate;
    
    /** {@inheritDoc} */
    public void destroy() {
        destroyCount += 1;
    }

    /** {@inheritDoc} */
    public boolean isDestroyed() {
        return destroyCount > 0;
    }

    /** Set whether to fail {@link #validate()} calls. */
    public void setFailValidate(boolean what) {
        failValidate = what;
    }
    
    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {
        if (failValidate) {
            throw new ComponentValidationException();
        }
        validateCount += 1;
    }

    /** {@inheritDoc} */
    public boolean isInitialized() {
        return initializeCount > 0;
    }

    /** {@inheritDoc} */
    public void initialize() throws ComponentInitializationException {
        initializeCount += 1;
    }

    /**
     * Gets the number of times {@link #destroy()} was called.
     * 
     * @return number of times {@link #destroy()} was called
     */
    public int getDestroyCount() {
        return destroyCount;
    }

    /**
     * Gets the number of times {@link #initialize()} was called.
     * 
     * @return number of times {@link #initialize()} was called
     */
    public int getInitializeCount() {
        return initializeCount;
    }

    /**
     * Gets the number of times {@link #validate()} was called.
     * 
     * @return number of times {@link #validate()} was called
     */
    public int getValidateCount() {
        return validateCount;
    }
}