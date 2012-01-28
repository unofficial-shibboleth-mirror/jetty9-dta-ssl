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

/** Base class for things that implement {@link DestructableComponent} and {@link InitializableComponent}. */
public abstract class AbstractDestructableInitializableComponent implements DestructableComponent,
        InitializableComponent {

    /** Whether this component has been destroyed. */
    private boolean isDestroyed;

    /** Whether this component has been initialized. */
    private boolean isInitialized;

    /** {@inheritDoc} */
    public final boolean isDestroyed() {
        return isDestroyed;
    }

    /** {@inheritDoc} */
    public boolean isInitialized() {
        return isInitialized;
    }

    /** {@inheritDoc} */
    public final synchronized void destroy() {
        if (isDestroyed) {
            return;
        }

        doDestroy();
        isDestroyed = true;
    }

    /** {@inheritDoc} */
    public final synchronized void initialize() throws ComponentInitializationException {
        if (isInitialized()) {
            return;
        }

        doInitialize();
        isInitialized = true;
    }

    /** Checks if this component is destroyed and, if so, throws a {@link DestroyedComponentException}. */
    protected void checkDestroyed() {
        if (isDestroyed) {
            throw new DestroyedComponentException();
        }
    }
    
    /** 
     * Checks if this component has been initialized and, if not, throws an {@link UninitializedComponentException}. 
     */
    protected void checkInitialized(){
        if(!isInitialized){
            throw new UninitializedComponentException();
        }
    }

    /** Performs {@link #checkInitialized()} and then {@link #checkDestroyed()}. */
    protected void readyCheck() {
        checkInitialized();
        checkDestroyed();
    }

    /** Performs component specific destruction logic. Default implementation of this method is a no-op. */
    protected void doDestroy() {

    }

    /**
     * Performs the initialization of the component. This method is executed within the lock on the object being
     * initialized.
     * 
     * The default implementation of this method is a no-op.
     * 
     * @throws ComponentInitializationException thrown if there is a problem initializing the component
     */
    protected void doInitialize() throws ComponentInitializationException {

    }
}