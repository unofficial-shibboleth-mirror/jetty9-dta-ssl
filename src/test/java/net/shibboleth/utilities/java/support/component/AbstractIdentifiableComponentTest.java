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
 * Tests for {@link AbstractIdentifiableComponent}
 */
public class AbstractIdentifiableComponentTest {

    private static final String STRING1 = "s1";
    private static final String STRING2 = "string2";
    
    protected static void testNullAssignment(SettableIdentifiableComponent component) {
        Assert.assertNull(component.getId());
        boolean thrown = false;
        try {
            component.setId(null);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting a null ID should throw");
        
        thrown = false;
        try {
            component.setId("");
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Setting en empty ID should throw");        
    }
    
    @Test
    public void abstractIdentifiableComponentTest() {
        
        SettableIdentifiableComponent component = new InternalComponent();
        
        testNullAssignment(component);
        component.setId(STRING1);
        Assert.assertEquals(component.getId(), STRING1, "Should be what was set");
        component.setId(STRING2);
        Assert.assertNotSame(component.getId(), STRING1, "Should not be what was originally set");
        Assert.assertEquals(component.getId(), STRING2, "Should be what was set");
        
    }
    
    private class InternalComponent extends AbstractIdentifiableComponent implements SettableIdentifiableComponent {
        public void setId(final String componentId) {
            super.setId(componentId);
        }
    }
}
