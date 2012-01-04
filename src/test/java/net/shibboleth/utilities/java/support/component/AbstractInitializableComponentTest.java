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
 * Test for {@link AbstractInitializableComponent}
 */
public class AbstractInitializableComponentTest {

    @Test
    public void abstractInitializableComponentTest() throws ComponentInitializationException {
        MyComponent component = new MyComponent();
        
        Assert.assertFalse(component.isInitialized(), "New Component should not be initialized");
        Assert.assertEquals(component.getInitCount(), 0, "New Component should have zero init count");
        
        component.initialize();
        Assert.assertTrue(component.isInitialized(), "Initialized Component should show initialized");
        Assert.assertEquals(component.getInitCount(), 1, "Initialized Component should have init count of 1");
        
        component.initialize();
        Assert.assertTrue(component.isInitialized(), "ReInitialized Component should show initialized");
        Assert.assertEquals(component.getInitCount(), 1, "ReInitialized Component should have init count of 1");
    }
    
    public class MyComponent extends AbstractInitializableComponent {
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
