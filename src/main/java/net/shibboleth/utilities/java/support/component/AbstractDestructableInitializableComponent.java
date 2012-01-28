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

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

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
    protected void ifDestroyedThrowDestroyedComponentException() {
        ifDestroyedThrowDestroyedComponentException(null);
    }

    /**
     * Checks if this component is destroyed and, if so, throws a {@link DestroyedComponentException}.
     * 
     * @param componentId identifier of the component that has been destroyed; used to generate an exception message
     */
    protected void ifDestroyedThrowDestroyedComponentException(@Nullable String componentId) {
        String trimmedId = StringSupport.trimOrNull(componentId);

        if (isDestroyed) {
            if (trimmedId != null) {
                throw new DestroyedComponentException("Component '" + trimmedId
                        + "' has already been destroyed and can no longer be used.");
            } else {
                throw new DestroyedComponentException();
            }
        }
    }

    /** Checks if this component has been initialized and, if not, throws an {@link UninitializedComponentException}. */
    protected void ifNotInitializedThrowUninitializedComponentException() {
        ifNotInitializedThrowUninitializedComponentException(null);
    }

    /**
     * Checks if this component has been initialized and, if not, throws an {@link UninitializedComponentException}.
     * 
     * @param componentId identifier of the component that has been destroyed; used to generate an exception message
     */
    protected void ifNotInitializedThrowUninitializedComponentException(@Nullable String componentId) {
        String trimmedId = StringSupport.trimOrNull(componentId);

        if (!isInitialized) {
            if (trimmedId != null) {
                throw new UninitializedComponentException("Component '" + trimmedId
                        + "' has not yet been initialized and so can not be used.");
            } else {
                throw new UninitializedComponentException();
            }
        }
    }

    /** Checks if this component has been initialized and, if so, throws an {@link UnmodifiableComponentException}. */
    protected void ifInitializedThrowUnmodifiabledComponentException() {
        ifInitializedThrowUnmodifiabledComponentException(null);
    }

    /**
     * Checks if this component has been initialized and, if so, throws an {@link UnmodifiableComponentException}.
     * 
     * @param componentId identifier of the component that has been destroyed; used to generate an exception message
     */
    protected void ifInitializedThrowUnmodifiabledComponentException(@Nullable String componentId) {
        String trimmedId = StringSupport.trimOrNull(componentId);

        if (isInitialized) {
            if (trimmedId != null) {
                throw new UnmodifiableComponentException("Component '" + trimmedId
                        + "' has already been initialized and so can no longer be modified");
            } else {
                throw new UnmodifiableComponentException();
            }
        }
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