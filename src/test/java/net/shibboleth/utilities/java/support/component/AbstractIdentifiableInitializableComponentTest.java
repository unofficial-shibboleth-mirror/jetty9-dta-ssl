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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test for {@link AbstractIdentifiableInitializableComponent}
 */
public class AbstractIdentifiableInitializableComponentTest {

    private static final String STRING1 = "s1";

    private static final String STRING2 = "string2";

    private static final String STRING3 = "Three String";

    private MyComponent component;

    @BeforeMethod public void setup() {
        component = new MyComponent();
    }

    @Test public void testNullAssignment() {
        Assert.assertNull(component.getId());
        boolean thrown = false;
        try {
            component.setId(null);
        } catch (AssertionError e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting a null ID should throw");

        thrown = false;
        try {
            component.setId("");
        } catch (AssertionError e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting en empty ID should throw");
    }

    @Test public void abstractIdentifiableInitializableComponentTest() throws ComponentInitializationException {

        Assert.assertFalse(component.isInitialized(), "New Component should not be initialized");
        Assert.assertEquals(component.getInitCount(), 0, "New Component should have zero init count");

        Assert.assertNull(component.getId());
        boolean thrown = false;
        try {
            component.setId(null);
        } catch (AssertionError e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting a null ID should throw");

        thrown = false;
        try {
            component.setId("");
        } catch (AssertionError e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting en empty ID should throw");

        component.setId(STRING1);
        Assert.assertEquals(component.getId(), STRING1, "Should be what was set");
        component.setId(STRING2);
        Assert.assertNotSame(component.getId(), STRING1, "Should not be what was originally set");
        Assert.assertEquals(component.getId(), STRING2, "Should be what was set");

        component.initialize();
        Assert.assertTrue(component.isInitialized(), "Initialized Component should show initialized");
        Assert.assertEquals(component.getInitCount(), 1, "Initialized Component should have init count of 1");

        component.initialize();
        Assert.assertTrue(component.isInitialized(), "ReInitialized Component should show initialized");
        Assert.assertEquals(component.getInitCount(), 1, "ReInitialized Component should have init count of 1");

        component.setId(STRING3);
        Assert.assertNotSame(component.getId(), STRING1, "Should not be what was originally set");
        Assert.assertNotSame(component.getId(), STRING3, "Should not be what was set after initialized");
        Assert.assertEquals(component.getId(), STRING2, "Should be what was set in last set before init");

    }

    private class MyComponent extends AbstractIdentifiableInitializableComponent {
        private int initCount;

        protected int getInitCount() {
            return initCount;
        }

        /** {@inheritDoc} */
        protected void doInitialize() throws ComponentInitializationException {
            initCount++;
            super.doInitialize();
        }
    }
}
