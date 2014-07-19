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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.component.IdentifiedComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.component.UnmodifiableComponent;

/**
 * A component that supplies {@link AccessControl} instances identified by a policy name.
 * 
 * <p>Policy names are an abstraction that can be used in a component's configuration to
 * indicate which access control configuration to apply, creating a layer of indirection
 * so that policies can be reused across components.</p> 
 */
@ThreadSafe
public interface AccessControlService extends InitializableComponent, IdentifiedComponent, UnmodifiableComponent {

    /**
     * Get an {@link AccessControl} instance matching a given name.
     * 
     * <p>In the absence of a matching instance, a default policy that denies access is returned.</p>
     * 
     * @param name  policy name
     * 
     * @return  the matching instance, or a default
     */
    @Nonnull AccessControl getInstance(@Nonnull final String name);
    
}