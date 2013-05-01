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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Base class for components implementing {@link DestructableComponent}, {@link IdentifiableComponent} and
 * {@link InitializableComponent}.
 */
public abstract class AbstractDestructableIdentifiableInitializableComponent extends
        AbstractDestructableInitializableComponent implements IdentifiableComponent {

    /** ID of this component. */
    @Nullable @NonnullAfterInit private String id;

    /** {@inheritDoc} */
    @Nullable @NonnullAfterInit public String getId() {
        return id;
    }

    /**
     * Sets the ID of this component. The component must not be initialized or destroyed.
     * 
     * @param componentId ID of the component
     */
    protected synchronized void setId(@Nonnull @NotEmpty String componentId) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        id = Constraint.isNotNull(StringSupport.trimOrNull(componentId), "Component ID can not be null or empty");
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (id == null) {
            throw new ComponentInitializationException("No component ID has been set");
        }
    }
}