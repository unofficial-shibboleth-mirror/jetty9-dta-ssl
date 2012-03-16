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

package net.shibboleth.utilities.java.support.net;

import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link HttpServletSupport} unit test. */
public class HttpServletSupportTest {

    @Test public void testAddNoCacheHeaders(){
        MockHttpServletResponse response = new MockHttpServletResponse();
        Assert.assertNull(response.getHeaderValue("Cache-control"));
        Assert.assertNull(response.getHeaderValue("Pragma"));
        
        HttpServletSupport.addNoCacheHeaders(response);
        Assert.assertEquals(response.getHeaderValue("Cache-control"), "no-cache, no-store");
        Assert.assertEquals(response.getHeaderValue("Pragma"), "no-cache");
    }
    
    @Test public void testGetFullRequestUri(){
//        mock request doesn't do what we want, need to figure out something better
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        
//        request.setScheme("http");
//        request.setServerName("example.org");
//        request.setRequestURI("/foo/bar");
//        request.setQueryString("baz=true");        
//        Assert.assertEquals(HttpServletSupport.getFullRequestUri(request), "http://example.org/foo/bar?baz=true");
//        
//        request.setScheme("https");
//        request.setServerPort(8443);
//        request.setQueryString(null);
//        Assert.assertEquals(HttpServletSupport.getFullRequestUri(request), "https://example.org:8443/foo/bar");
    }
    
    @Test public void testGetRequestPathWithoutContext(){
        
    }
    
    @Test public void testSetContentType(){
        
    }
    
    @Test public void testSetUTF8Encoding(){
        
    }
}