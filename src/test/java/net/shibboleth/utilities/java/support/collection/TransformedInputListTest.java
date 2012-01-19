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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for {@link TransformedInputList}. We extend the basic tests done for the LazySet.
 */
public class TransformedInputListTest {

    private TransformedInputList testList;

    @BeforeMethod public void setup() {
        testList = new TransformedInputList<String>(new ArrayList<String>(),
            CollectionTest.nullRemoveFunction);
    }

    @Test public void testSimple() {
        CollectionTest.testSimpleCollection(testList, true);
    }

    @Test public void testArray() {
        CollectionTest.testArrayCollection(testList, new ArrayList<String>());
    }

    @Test public void testIterator() {
        CollectionTest.testIteratorCollection(testList, new ArrayList<String>());
    }

    @Test public void testListFunctions() {
        ListTest.testListFunctions(testList);
    }

    /**
     * Test the predicate bits
     */
    @Test public void testPredicate() {
        Assert.assertTrue(testList.isEmpty(), "Initial state");
        testList.add(null);
        Assert.assertTrue(testList.isEmpty(), "Add null");

        testList.add("String");
        Assert.assertEquals(testList.get(0), "String", "Add something");

        testList.addAll(Arrays.asList("one", null, "two", null));
        Assert.assertEquals(testList.get(0), "String", "allAll test");
        Assert.assertEquals(testList.get(1), "one", "allAll test");
        Assert.assertEquals(testList.get(2), "two", "allAll test");

        testList.addAll(1, Arrays.asList("NEWone", null, "NEWtwo", null));
        Assert.assertEquals(testList.get(0), "String", "allAll test2");
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        Assert.assertEquals(testList.get(2), "NEWtwo", "allAll test2");
        Assert.assertEquals(testList.get(3), "one", "allAll test2");
        Assert.assertEquals(testList.get(4), "two", "allAll test2");
        
        testList.add(1, null);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");

        testList.add(1, "NewerOne");
        Assert.assertEquals(testList.get(1), "NewerOne", "allAll test2");
        testList.remove(1);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        
        testList.set(1, null);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");

        testList.set(1, "ONE");
        Assert.assertEquals(testList.get(1), "ONE", "allAll test2");
        testList.set(1, "NEWone");
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        
        ListIterator<String> listIterator = testList.listIterator();
        
        listIterator.next();
        listIterator.add(null);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        listIterator.add("NewerOne");
        Assert.assertEquals(testList.get(1), "NewerOne", "allAll test2");
        testList.remove(1);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        
        listIterator = testList.listIterator(1);
        
        listIterator.add(null);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
        listIterator.add("NewerOne");
        Assert.assertEquals(testList.get(1), "NewerOne", "allAll test2");
        testList.remove(1);
        Assert.assertEquals(testList.get(1), "NEWone", "allAll test2");
    }
    
    @Test public void testTransform() {
        testList = new TransformedInputList<String>(new ArrayList<String>(), CollectionTest.upcaseNotNull);

        Assert.assertTrue(testList.isEmpty(), "Initial state");
        
        testList.add("one");
        testList.add("two");
        Assert.assertEquals(testList.size(), 2, "Add test");
        Assert.assertEquals(testList.get(0), "ONE", "Add test");
        Assert.assertEquals(testList.get(1), "TWO", "Add test");
        
        testList.set(1, "one");
        testList.set(0, "zero");
        Assert.assertEquals(testList.size(), 2, "Set test");
        Assert.assertEquals(testList.get(0), "ZERO", "Set test");
        Assert.assertEquals(testList.get(1), "ONE", "Set test");
        
        testList.addAll(1, Arrays.asList("Onea", "Oneb"));
        Assert.assertEquals(testList.size(), 4, "AddAll test");
        Assert.assertEquals(testList.get(1), "ONEA", "AddAll test");
        Assert.assertEquals(testList.get(2), "ONEB", "AddAll test");
        Assert.assertEquals(testList.get(3), "ONE", "AddAll test");
        
        testList.listIterator(3).add("onec");
        Assert.assertEquals(testList.size(), 5, "ListIterator Add test");
        Assert.assertEquals(testList.get(3), "ONEC", "ListIterator Add test");
        
        ListIterator<String> le = testList.listIterator(3);
        le.next();
        le.set("new");
        Assert.assertEquals(testList.size(), 5, "ListIterator Set test");
        Assert.assertEquals(testList.get(3), "NEW", "ListIterator Add test");
        
        
    }

}
