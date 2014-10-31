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

package net.shibboleth.utilities.java.support.codec;

import java.security.NoSuchAlgorithmException;

import net.shibboleth.utilities.java.support.codec.StringDigester.OutputFormat;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test {@link StringDigester} functionality.
 */
public class StringDigesterTest {
    
    private StringDigester digester;
    
    @Test
    public void testBasic() throws NoSuchAlgorithmException {
        String input  = "foobarbaz";
        
        digester = new StringDigester("SHA-1", OutputFormat.BASE64);
        Assert.assertEquals(digester.apply(input), "X1UT+IIv2+UUWvM7ZNjZcNz5XG4=");
        
        digester = new StringDigester("SHA-1", OutputFormat.HEX_LOWER);
        Assert.assertEquals(digester.apply(input), "5f5513f8822fdbe5145af33b64d8d970dcf95c6e");
        
        digester = new StringDigester("SHA-1", OutputFormat.HEX_UPPER);
        Assert.assertEquals(digester.apply(input), "5F5513F8822FDBE5145AF33B64D8D970DCF95C6E");
        
        // test empty input
        digester = new StringDigester("SHA-1", OutputFormat.BASE64);
        Assert.assertNull(digester.apply("     "));
    }
    
    @Test(expectedExceptions=NoSuchAlgorithmException.class)
    public void testInvalidAlgorithm() throws NoSuchAlgorithmException {
        digester = new StringDigester("bogus", OutputFormat.BASE64);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullOrEmptyAlgorithm() throws NoSuchAlgorithmException {
        digester = new StringDigester("   ", OutputFormat.BASE64);
    }

}
