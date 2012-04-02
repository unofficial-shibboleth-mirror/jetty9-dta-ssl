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

import java.util.Arrays;
import java.util.List;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * test for the various methods inside {@link StringSupport}
 */
public class StringSupportTest {

    private static final String TRIM_TEST1 = " AARDVARK incorporated";

    private static final String EMPTY_TRIM_TEST2 = " \t ";

    private static final String SEPARATOR = "+";

    private static final String TEST_LIST = "1+x2+y3+z4+5+6+";

    private static final List<String> TEST_LIST_AS_LIST = Arrays.asList("1", "x2", "y3", "z4", "5", "6", "");

    @Test public void testListToStringValue() {
        Assert.assertEquals(StringSupport.listToStringValue(TEST_LIST_AS_LIST, SEPARATOR), TEST_LIST,
                "toList<String> fails");
        boolean thrown = false;
        try {
            StringSupport.listToStringValue(TEST_LIST_AS_LIST, null);
        } catch (ConstraintViolationException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "null separator should throw an assertion");

        thrown = false;
        try {
            StringSupport.listToStringValue(null, SEPARATOR);
        } catch (ConstraintViolationException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "null list should throw an assertion");
    }

    @Test public void testStringToList() {
        Assert.assertEquals(StringSupport.stringToList(TEST_LIST, SEPARATOR), TEST_LIST_AS_LIST,
                "from List<String> fails");
        Assert.assertTrue(StringSupport.stringToList("", SEPARATOR).isEmpty(), "Empty input should give empty list");

        boolean thrown = false;
        try {
            StringSupport.stringToList(null, SEPARATOR);
        } catch (ConstraintViolationException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Null input should throw an assertion");

        thrown = false;
        try {
            StringSupport.stringToList(TEST_LIST, null);
        } catch (ConstraintViolationException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Null separator should throw an assertion");
    }

    @Test public void testTrim() {

        Assert.assertEquals(StringSupport.trim(null), null, "Trimming Null should be OK");
        Assert.assertEquals(StringSupport.trim(EMPTY_TRIM_TEST2).length(), 0,
                "Trimming an empty string should return a string of zero length");

        Assert.assertEquals(StringSupport.trim(TRIM_TEST1), TRIM_TEST1.trim(), "Trimming a string");

    }

    @Test public void testTrimOrNull() {
        Assert.assertEquals(StringSupport.trimOrNull(null), null, "Trimming Null should be OK");
        Assert.assertEquals(StringSupport.trimOrNull(EMPTY_TRIM_TEST2), null,
                "Trimming an empty string should return null");

        Assert.assertEquals(StringSupport.trim(TRIM_TEST1), TRIM_TEST1.trim(), "Trimming a string");

    }

}
