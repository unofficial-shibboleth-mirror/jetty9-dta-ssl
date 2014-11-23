
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
