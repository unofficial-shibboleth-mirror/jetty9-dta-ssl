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


package net.shibboleth.utilities.java.support.httpclient;

import org.apache.http.client.HttpClient;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HttpClientBuilderTest {
    
    // Don't impose default timeouts
    @Test public void JSPT48() throws Exception {
        final HttpClientBuilder builder = new HttpClientBuilder();
        
        // Check the defaults at the builder level
        Assert.assertEquals(builder.getConnectionTimeout(), -1);
        Assert.assertEquals(builder.getSocketTimeout(), -1);
        
        // Just make sure we can create a client, too
        final HttpClient client = builder.buildClient();
        Assert.assertNotNull(client);
    }
    
}
