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

package net.shibboleth.utilities.java.support.primitive;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.utilities.java.support.component.IdentifiedComponent;

public class TimerSupportTest {
    
    @Test
    public void testBasic() {
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   myComponentID    ", "   myComponentToString   ")), 
                "Timer for myComponentID");
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   ", "   myComponentToString   ")), 
                "Timer for myComponentToString");
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   ", "   ")), 
                "Timer for " + MockIdentifiedComponent.class.getName());
        Assert.assertEquals(TimerSupport.getTimerName(new MockObject("   myComponentToString   ")), 
                "Timer for myComponentToString");
        Assert.assertEquals(TimerSupport.getTimerName(new MockObject("   ")), 
                "Timer for " + MockObject.class.getName());
        
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   myComponentID    ", "   myComponentToString   "), "abc123"), 
                "Timer for myComponentID (abc123)");
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   ", "   myComponentToString   "), "abc123"), 
                "Timer for myComponentToString (abc123)");
        Assert.assertEquals(TimerSupport.getTimerName(new MockIdentifiedComponent("   ", "   "), "abc123"), 
                "Timer for " + MockIdentifiedComponent.class.getName() + " (abc123)");
        Assert.assertEquals(TimerSupport.getTimerName(new MockObject("   myComponentToString   "), "abc123"), 
                "Timer for myComponentToString (abc123)");
        Assert.assertEquals(TimerSupport.getTimerName(new MockObject("   "), "abc123"), 
                "Timer for " + MockObject.class.getName() + " (abc123)");
    }
    
    private static class MockIdentifiedComponent implements IdentifiedComponent {
        
        private String id;
        private String toString;
        
        public MockIdentifiedComponent(String idValue, String toStringValue) {
            id = idValue;
            toString = toStringValue;
        }
        
        public String getId() {
            return id;
        }
        
        public String toString() {
            return toString;
        }
    }
    
    private static class MockObject {
        
        private String toString;
        
        public MockObject(String toStringValue) {
            toString = toStringValue;
        }
        
        public String toString() {
            return toString;
        }
        
    }

}
