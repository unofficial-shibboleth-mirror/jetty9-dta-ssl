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

package net.shibboleth.utilities.java.support.collection;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class IterableSupportTest {

    @Test
    public void testContainsInstance() {
        //Empty
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Object>newArrayList(), Foo.class));
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Foo>newArrayList(), Foo.class));
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Object>newArrayList(), Object.class));
        
        // Class = Object
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Object>newArrayList(new Object(), new Foo(), new Object()), Object.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Object>newArrayList(new Object(), new Object()), Object.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Foo()), Object.class));
        
        // Class = Foo
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Object>newArrayList(new Object(), new Object()), Foo.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Object>newArrayList(new Object(), new Foo(), new Object()), Foo.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Object>newArrayList(new Foo(), new Foo()), Foo.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Foo()), Foo.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Bar>newArrayList(new Bar(), new Bar()), Foo.class));
        
        // Class = Bar
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Foo()), Bar.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Bar>newArrayList(new Bar(), new Bar()), Bar.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Bar()), Bar.class));
        
        // Class = Baz
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Foo()), Baz.class));
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Bar()), Baz.class));
        Assert.assertFalse(IterableSupport.containsInstance(Lists.<Bar>newArrayList(new Bar(), new Bar()), Baz.class));
        Assert.assertTrue(IterableSupport.containsInstance(Lists.<Foo>newArrayList(new Foo(), new Bar(), new Baz()), Baz.class));
    }
    
    private static class Foo {
        
    }
    
    private static class Bar extends Foo {
        
    }
    
    private static class Baz extends Foo {
        
    }

}
