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
 * Base class for components implementing {@link DestructableComponent}, {@link IdentifiableComponent} and
 * {@link InitializableComponent}.
 */
public abstract class AbstractDestrucableIdentifiableInitializableComponent extends
        AbstractIdentifiableInitializableComponent implements DestructableComponent {

    /** Whether this component has been destroyed. */
    private boolean isDestroyed;

    /** {@inheritDoc} */
    protected synchronized void setId(String componentId) {
        if (isDestroyed) {
            throw new DestroyedComponentException();
        }

        super.setId(componentId);
    }

    /** {@inheritDoc} */
    public final boolean isDestroyed() {
        return isDestroyed;
    }

    /** {@inheritDoc} */
    public final synchronized void destroy() {
        if (isDestroyed) {
            return;
        }

        doDestroy();
        isDestroyed = true;
    }

    /** Performs component specific destruction logic. Default implementation of this method is a no-op. */
    protected void doDestroy() {

    }
}