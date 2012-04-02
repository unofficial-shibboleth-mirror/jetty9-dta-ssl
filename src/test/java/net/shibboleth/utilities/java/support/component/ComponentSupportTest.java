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

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/** Tests for {@link ComponentSupport} */
public class ComponentSupportTest {

    @Test public void testDestroy() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.destroy(null);

        ComponentSupport.destroy(new Object());

        MockDestructableComponent component = new MockDestructableComponent();
        Assert.assertFalse(component.isDestroyed(), "New component not destroyed");

        ComponentSupport.destroy(component);
        Assert.assertTrue(component.isDestroyed(), "Destroyed component destroyed");
    }

    @Test public void testInitialized() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.initialize(null);

        ComponentSupport.initialize(new Object());

        MockInitializableComponent component = new MockInitializableComponent();
        Assert.assertFalse(component.isInitialized(), "New component not initialized");

        ComponentSupport.initialize(component);
        Assert.assertTrue(component.isInitialized(), "Initialized component initialized");
    }

    @Test public void testValidate() throws ComponentInitializationException, ComponentValidationException {
        ComponentSupport.validate(null);

        ComponentSupport.validate(new Object());

        ValidatabledComponent component = new ValidatabledComponent();
        Assert.assertFalse(component.isValidated(), "New component not validated");

        ComponentSupport.validate(component);
        Assert.assertTrue(component.isValidated(), "Validated component validated");
    }

    @Test public void testIfDestroyedThrowDestroyedComponentException() {
        MockDestructableComponent component = new MockDestructableComponent();

        try {
            ComponentSupport.ifDestroyedThrowDestroyedComponentException(component);
        } catch (DestroyedComponentException e) {
            Assert.fail();
        }

        component.destroy();
        try {
            ComponentSupport.ifDestroyedThrowDestroyedComponentException(component);
            Assert.fail();
        } catch (DestroyedComponentException e) {
            // expected this
        }

        try {
            ComponentSupport.ifDestroyedThrowDestroyedComponentException(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }
    }

    @Test public void testIfNotInitializedThrowUninitializedComponentException() throws Exception {
        MockInitializableComponent component = new MockInitializableComponent();

        try {
            ComponentSupport.ifNotInitializedThrowUninitializedComponentException(component);
            Assert.fail();
        } catch (UninitializedComponentException e) {
            // expected this
        }

        component.initialize();
        try {
            ComponentSupport.ifNotInitializedThrowUninitializedComponentException(component);
        } catch (UninitializedComponentException e) {
            Assert.fail();
        }

        try {
            ComponentSupport.ifNotInitializedThrowUninitializedComponentException(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }
    }

    @Test public void testIfInitializedThrowUnmodifiabledComponentException() throws Exception {
        MockInitializableComponent component = new MockInitializableComponent();

        try {
            ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(component);
        } catch (UnmodifiableComponentException e) {
            Assert.fail();
        }

        component.initialize();
        try {
            ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(component);
            Assert.fail();
        } catch (UnmodifiableComponentException e) {
            // expected this
        }

        try {
            ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }
    }

    private class MockDestructableComponent implements DestructableComponent {

        private boolean destroyed;

        /** {@inheritDoc} */
        public void destroy() {
            destroyed = true;
        }

        /** {@inheritDoc} */
        public boolean isDestroyed() {
            return destroyed;
        }
    }

    public class MockInitializableComponent implements InitializableComponent {

        private boolean initialized;

        /** {@inheritDoc} */
        public void initialize() throws ComponentInitializationException {
            initialized = true;
        }

        /** {@inheritDoc} */
        public boolean isInitialized() {
            return initialized;
        }
    }

    private class ValidatabledComponent implements ValidatableComponent {

        private boolean validated;

        /**
         * Gets whether the component was validated.
         * 
         * @return whether the component was validated
         */
        public boolean isValidated() {
            return validated;
        }

        /** {@inheritDoc} */
        public void validate() throws ComponentValidationException {
            validated = true;
        }
    }
}