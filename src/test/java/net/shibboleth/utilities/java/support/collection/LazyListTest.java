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
import java.util.Collection;
import java.util.List;

import org.testng.annotations.Test;

/**
 * Tests for {@link LazyList}.  We extend the basic tests done for the LazySet.
 */
public class LazyListTest {
    
    /**
     * Test the test method with a known good List and then with a LazyList
     */
    @Test 
    public void testSimple() {
        CollectionTest.testSimpleCollection(new LazyList<String>(), true);
    }

    /**
     * Test The array function.
     */
    @Test 
    public void testArray() {
        CollectionTest.testArrayCollection(new LazyList<String>(), new ArrayList<String>());
    }
    
    /**
     * Test the iterator part of a collection.
     */
    @Test
    public void testIterator() {
        CollectionTest.testIteratorCollection(new LazyList<String>(), new ArrayList<String>());
    }
    
     /**
     * Test those things that distinguish the {@link List} API from the {@link Collection} one. 
     */
    @Test
    public void testListFunctions() {
        ListTest.testListFunctions(new LazyList<String>());
    }

}
