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

package net.shibboleth.utilities.java.support.resolver;

import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link CriteriaSet} unit test. */
public class CriteriaSetTest {

    @Test public void testConstruction() {
        CriteriaSet set = new CriteriaSet();
        Assert.assertTrue(set.isEmpty());

        set = new CriteriaSet((Criterion[])null);
        Assert.assertTrue(set.isEmpty());

        set = new CriteriaSet(new Criterion[] {});
        Assert.assertTrue(set.isEmpty());

        set = new CriteriaSet(new MockCriterion());
        Assert.assertEquals(set.size(), 1);
        
        set = new CriteriaSet(null, new MockCriterion());
        Assert.assertEquals(set.size(), 1);

        try {
            new CriteriaSet(new MockCriterion(), new MockCriterion());
        } catch (IllegalArgumentException e) {
            // expected this
        }

    }

    private class MockCriterion implements Criterion {

    }
}