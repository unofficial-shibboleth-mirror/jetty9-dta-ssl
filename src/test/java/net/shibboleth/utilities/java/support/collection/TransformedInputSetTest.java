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

import java.util.Arrays;
import java.util.HashSet;

import net.shibboleth.utilities.java.support.logic.TransformAndCheckFunction;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;

/**
 * Tests for {@Link TransformedInputSet}.
 */
public class TransformedInputSetTest {
    
    private TransformedInputSet testSet;

    @BeforeMethod public void setup() {
        testSet = new TransformedInputSet<String>(new HashSet<String>(),
            CollectionTestSupport.nullRemoveFunction);
    }

    @Test 
    public void testSimple() {
        CollectionTestSupport.testSimpleCollection(testSet, false);
    }

    @Test 
    public void testArray() {
        CollectionTestSupport.testArrayCollection(testSet, new HashSet<String>());
    }
    
    @Test
    public void testIterator() {
        CollectionTestSupport.testIteratorCollection(testSet, new HashSet<String>());
    }

    @Test
    public void testPredictateAndTransform() {
        testSet = new TransformedInputSet<String>(new HashSet<String>(),
                new TransformAndCheckFunction(new CollectionTestSupport.UpcaseFunction(), Predicates.notNull(), false));
        Assert.assertTrue(testSet.isEmpty(), "Initial state");
        testSet.add(null);
        Assert.assertTrue(testSet.isEmpty(), "Add null");

        testSet.add("String");
        Assert.assertEquals(testSet.size(), 1, "Add something");
        Assert.assertTrue(testSet.contains("STRING"), "Add something");

        testSet.addAll(Arrays.asList("one", null, "two", null));
        Assert.assertEquals(testSet.size(), 3, "AddAll");
        Assert.assertTrue(testSet.contains("STRING"), "AddAll");
        Assert.assertTrue(testSet.contains("ONE"), "AddAll");
        Assert.assertTrue(testSet.contains("TWO"), "AddAll");
    }
}
