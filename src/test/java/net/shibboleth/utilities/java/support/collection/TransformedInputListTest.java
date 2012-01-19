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
import java.util.Collection;
import java.util.List;
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
            CollectionTestSupport.nullRemoveFunction);
    }

    /**
     * Test the test method with a known good List and then with a LazyList
     */
    @Test public void testSimple() {
        CollectionTestSupport.testSimpleCollection(testList, true);
    }

    /**
     * Test The array function.
     */
    @Test public void testArray() {
        CollectionTestSupport.testArrayCollection(testList, new ArrayList<String>());
    }

    /**
     * Test the iterator part of a collection.
     */
    @Test public void testIterator() {
        CollectionTestSupport.testIteratorCollection(testList, new ArrayList<String>());
    }

    /**
     * Test those things that distinguish the {@link List} API from the {@link Collection} one.
     */
    @Test public void testListFunctions() {
        ListTestSupport.testListFunctions(testList);
    }

    /**
     * Test the transform bits
     */
    @Test public void testTransforms() {
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
}
