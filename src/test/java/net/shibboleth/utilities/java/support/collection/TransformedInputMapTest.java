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

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link TransformedInputMap}.
 */
public class TransformedInputMapTest {

    @Test public void testTransformedInputMap() {
        MapTest.testMap(new TransformedInputMap(new LazyMap(), CollectionTest.nullRemoveFunction,
                CollectionTest.nullRemoveFunction));
    }

    @Test public void testPredicate() {
        
        TransformedInputMap<String, String> testMap =
                new TransformedInputMap(new HashMap(), CollectionTest.nullRemoveFunction,
                        CollectionTest.nullRemoveFunction);
        
        Assert.assertTrue(testMap.isEmpty(), "Initial state");

        testMap.put(null, "testData");
        Assert.assertTrue(testMap.isEmpty(), "null key add");

        testMap.put("testkey", null);
        Assert.assertTrue(testMap.isEmpty(), "null data add");

        testMap.put("testkey", "testData");
        Assert.assertEquals(testMap.size(), 1, "key add");
        Assert.assertTrue(testMap.containsValue("testData"), "key Add");
        Assert.assertTrue(testMap.containsKey("testkey"), "key Add");

        //
        // And check the null predicate again
        //
        testMap.put(null, "testData");
        testMap.put("testkey", null);
        Assert.assertEquals(testMap.size(), 1, "key add");
        Assert.assertTrue(testMap.containsValue("testData"), "key Add");
        Assert.assertTrue(testMap.containsKey("testkey"), "key Add");

    }

    @Test public void testTransform() {
        TransformedInputMap<String, String> testMap =
                new TransformedInputMap(new HashMap(), CollectionTest.upcaseNotNull,
                        CollectionTest.upcaseNotNull);

        Assert.assertTrue(testMap.isEmpty(), "Initial state");
        testMap.put("testkey", "testData");
        Assert.assertEquals(testMap.size(), 1, "key add");
        Assert.assertFalse(testMap.containsValue("testData"), "key Add");
        Assert.assertFalse(testMap.containsKey("testkey"), "key Add");
        Assert.assertTrue(testMap.containsValue("TESTDATA"), "key Add");

        testMap.put("tEsTkEy", "test");
        Assert.assertEquals(testMap.size(), 1, "key add");
        Assert.assertTrue(testMap.containsValue("TEST"), "key Add");
        Assert.assertTrue(testMap.containsKey("TESTKEY"), "key Add");
        
    }
}
