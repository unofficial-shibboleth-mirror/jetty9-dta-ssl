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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link ComponentSupport}
 */
public class ComponentSupportTest {

    @Test 
    public void destroyTest() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.destroy(new Object());
        MyComponent component = new MyComponent();
        
        Assert.assertFalse(component.isDestroyed(), "New component not destroyed");
        ComponentSupport.initialize(component);
        Assert.assertFalse(component.isDestroyed(), "Initialized component not destroyed");
        ComponentSupport.validate(component);
        Assert.assertFalse(component.isDestroyed(), "Validated component not destroyed");
        ComponentSupport.destroy((InitializableComponent) component);
        Assert.assertTrue(component.isDestroyed(), "Destroyed component destroyed");
    }

    @Test
    public void validateTest() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.validate(new Object());
        MyComponent component = new MyComponent();
        
        Assert.assertFalse(component.isValidated(), "New component not validated");
        ComponentSupport.initialize(component);
        Assert.assertFalse(component.isValidated(), "Initialized component not validated");
        ComponentSupport.destroy(component);
        Assert.assertFalse(component.isValidated(), "Destroyed component not validated");
        ComponentSupport.validate((InitializableComponent) component);
        Assert.assertTrue(component.isValidated(), "Validated component validated");
    }

    @Test
    public void initializeTest() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.initialize(new Object());
        MyComponent component = new MyComponent();
        
        Assert.assertFalse(component.isInitialized(), "New component not initialized");
        ComponentSupport.validate(component);
        Assert.assertFalse(component.isInitialized(), "Validated component not initialized");
        ComponentSupport.destroy(component);
        Assert.assertFalse(component.isInitialized(), "Destroyed component not initialized");
        ComponentSupport.initialize((InitializableComponent) component);
        Assert.assertTrue(component.isInitialized(), "Initialized component initialized");

    }

    private class MyComponent extends AbstractInitializableComponent implements InitializableComponent,
            ValidatableComponent, DestructableComponent {

        private boolean destroyed;
        private boolean validated;
        
        /** {@inheritDoc} */
        public boolean isDestroyed() {
            return destroyed;
        }

        /** {@inheritDoc} */
        public void destroy() {
            destroyed = true;
        }

        protected boolean isValidated() {
            return validated;
        }
        
        /** {@inheritDoc} */
        public void validate() throws ComponentValidationException {
           validated = true;
        }

    }

}
