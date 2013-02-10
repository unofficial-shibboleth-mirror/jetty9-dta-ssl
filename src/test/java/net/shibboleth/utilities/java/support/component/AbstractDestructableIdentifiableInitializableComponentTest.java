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

/** {@link AbstractDestructableIdentifiableInitializableComponent} unit test. */
public class AbstractDestructableIdentifiableInitializableComponentTest {

    @Test public void testId() throws Exception {
        MockComponent component = new MockComponent();
        
        Assert.assertNull(component.getId());
        
        component.setId("foo");
        Assert.assertEquals(component.getId(), "foo");
        
        component.initialize();
        try{
            component.setId("bar");
            Assert.fail();
        }catch(UnmodifiableComponentException e){
            //expected this
        }
        Assert.assertEquals(component.getId(), "foo");
        
        component = new MockComponent();
        component.destroy();
        try{
            component.setId("bar");
            Assert.fail();
        }catch(DestroyedComponentException e){
            //expected this
        }
    }
    
    /** A mock component. */
    private class MockComponent extends AbstractDestructableIdentifiableInitializableComponent{
        
        /** {@inheritDoc} */
        public synchronized void setId(String componentId) {
            super.setId(componentId);
        }
    }
}