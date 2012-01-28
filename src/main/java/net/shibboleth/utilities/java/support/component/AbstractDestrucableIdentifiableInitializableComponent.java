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

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * Base class for components implementing {@link DestructableComponent}, {@link IdentifiableComponent} and
 * {@link InitializableComponent}.
 */
public abstract class AbstractDestrucableIdentifiableInitializableComponent extends
        AbstractDestructableInitializableComponent implements IdentifiableComponent {

    /** ID of this component. */
    private String id;

    /** {@inheritDoc} */
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    protected synchronized void setId(@Nonnull @NotEmpty String componentId) {
        ifInitializedThrowUnmodifiabledComponentException(id);
        ifDestroyedThrowDestroyedComponentException(id);
        
        id = Assert.isNotNull(StringSupport.trimOrNull(componentId), "Component ID can not be null or empty");
    }
}