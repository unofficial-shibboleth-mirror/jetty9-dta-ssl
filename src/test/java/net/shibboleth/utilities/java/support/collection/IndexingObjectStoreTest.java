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


/** Unit test for {@link IndexingObjectStore }. */
public class IndexingObjectStoreTest {

    @Test
    public void testIndexingObjectStore() {
        IndexingObjectStore<String> store = new IndexingObjectStore<String>();

        String str1 = new String("foo");
        String str2 = new String("bar");

        Assert.assertTrue(store.isEmpty());
        Assert.assertEquals(store.size(), 0);
        Assert.assertFalse(store.contains("foo"));

        String nullIndex = store.put(null);
        Assert.assertNull(nullIndex);
        Assert.assertTrue(store.isEmpty());
        Assert.assertEquals(store.size(), 0);

        String str1Index = store.put(str1);
        Assert.assertTrue(store.contains(str1Index));
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(store.size(), 1);
        Assert.assertEquals(store.get(str1Index), str1);

        String index1 = store.put("foo");
        Assert.assertTrue(store.contains(index1));
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(store.size(), 1);
        Assert.assertEquals(index1, str1Index);
        Assert.assertEquals(store.get(index1), str1);

        store.remove(str1Index);
        Assert.assertTrue(store.contains(index1));
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(store.size(), 1);
        Assert.assertEquals(index1, index1);
        Assert.assertEquals(store.get(index1), str1);

        String str2Index = store.put(str2);
        Assert.assertTrue(store.contains(str2Index));
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(store.size(), 2);
        Assert.assertEquals(store.get(str2Index), str2);

        store.remove(str1Index);
        Assert.assertFalse(store.contains(str1Index));
        Assert.assertFalse(store.isEmpty());
        Assert.assertEquals(store.size(), 1);
        Assert.assertNull(store.get(str1Index));

        store.clear();
        Assert.assertTrue(store.isEmpty());
        Assert.assertEquals(store.size(), 0);
    }
}