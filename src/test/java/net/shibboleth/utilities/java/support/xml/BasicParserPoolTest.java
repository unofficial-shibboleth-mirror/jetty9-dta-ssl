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

package net.shibboleth.utilities.java.support.xml;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.UnmodifiableComponentException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool.DocumentBuilderProxy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test the basic parser pool implementation.
 */
public class BasicParserPoolTest {
    
    /** Pool instance to test. */
    private BasicParserPool pool;
    
    /** Max size of the pool we're using. */
    int maxPoolSize = 10;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        pool = new BasicParserPool();
        pool.setMaxPoolSize(maxPoolSize);
        
        pool.initialize();
    }
    
    /**
     * Test issue reported in JXT-46 - a parser should not be checked into the pool multiple times
     * via the auto-checkin mechanism by the proxy finalize().
     * 
     * @throws XMLParserException 
     * @throws InterruptedException 
     */
    @Test
    public void testFinalize() throws XMLParserException {
        
        AssertJUnit.assertEquals(0, pool.getPoolSize());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        pool.returnBuilder(builder);
        
        AssertJUnit.assertEquals(1, pool.getPoolSize());
        
        // Get rid of any references to the first builder we got, so that it will be GCed
        //builder = null;
        // Do explicit GC and sleep a little make sure proxy finalize() gets called
        //System.out.println("Garbage collection and sleep");
        //System.gc();
        //Thread.sleep(3000);
        //System.out.println("Done sleeping");
        
        // Rather than relying on forcing GC behavior in the test as above, which was in initial debugging this problem,
        // explicitly invoke finalize() to simulate.
        // (which we can do b/c it's protected access *and* we're in the same package)
        try {
            ((DocumentBuilderProxy)builder).finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        AssertJUnit.assertEquals(1, pool.getPoolSize());
        
        
        // Both of these would have been the same instance pre-bug fix.
        DocumentBuilder builder1 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        AssertJUnit.assertNotNull(builder1);
        DocumentBuilder builder2 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        AssertJUnit.assertNotNull(builder2);
        AssertJUnit.assertFalse(builder1.equals(builder2));
        
    }
    
    
    /**
     * Test for caller (illegally) returning a builder multiple times to pool.
     * 
     * @throws XMLParserException
     */
    @Test
    public void testExplicitMultipleReturn() throws XMLParserException {
        AssertJUnit.assertEquals(0, pool.getPoolSize());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        
        pool.returnBuilder(builder);
        AssertJUnit.assertEquals(1, pool.getPoolSize());
        
        // This isn't legal to do, but should be silently detected and ignored
        pool.returnBuilder(builder);
        AssertJUnit.assertEquals(1, pool.getPoolSize());
        
        DocumentBuilder builder1 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        AssertJUnit.assertNotNull(builder1);
        DocumentBuilder builder2 = ((DocumentBuilderProxy) pool.getBuilder()).getProxiedBuilder();
        AssertJUnit.assertNotNull(builder2);
        AssertJUnit.assertFalse(builder1.equals(builder2));
        
    }
    
    /**
     * Test for a caller illegally using a parser proxy after it has been returned. 
     * 
     * @throws XMLParserException 
     * @throws URISyntaxException 
     * @throws IOException 
     * 
     */
    @Test
    public void testParserUseAfterReturn() throws XMLParserException, URISyntaxException {
        String testPath = "/data/net/shibboleth/utilities/java/support/xml/foo.xml";
        InputStream is = BasicParserPoolTest.class.getResourceAsStream(testPath);
        File file = new File(this.getClass().getResource(testPath).toURI());
        
        // Check out and return a builder
        DocumentBuilder builder = pool.getBuilder();
        
        try {
            builder.parse(file);
        } catch (IllegalStateException e) {
            Assert.fail("Parser proxy was in a valid state");
        } catch (SAXException e) {
            Assert.fail("Parser proxy was in a valid state");
        } catch (IOException e) {
            Assert.fail("Parser proxy was in a valid state");
        }
        
        pool.returnBuilder(builder);
        
        
        try {
            builder.parse(file);
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            Assert.fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(is);
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            Assert.fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(new InputSource(is));
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            Assert.fail("Parser proxy was in an illegal state");
        }
        
        try {
            builder.parse(file.toURI().toString());
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IllegalStateException e) {
            // do nothing, expected
        } catch (SAXException e) {
            Assert.fail("Parser proxy was in an illegal state");
        } catch (IOException e) {
            Assert.fail("Parser proxy was in an illegal state");
        }
        
    }
    
    /**
     * Test that only maxPoolSize parsers are ever cached.
     * 
     * @throws XMLParserException
     */
    @Test
    public void testMaxPoolSize() throws XMLParserException {
        AssertJUnit.assertEquals(0, pool.getPoolSize());
        
        ArrayList<DocumentBuilder> list = new ArrayList<DocumentBuilder>();
        
        // Get 3x the maxPoolSize number of builders
        for (int i=0; i < 3*maxPoolSize; i++) {
            list.add(pool.getBuilder());
        }
        
        AssertJUnit.assertEquals(0, pool.getPoolSize());
        
        for (DocumentBuilder b : list) {
           pool.returnBuilder(b); 
        }
        
        // Even though we return 3*maxPoolSize builders, only maxPoolSize should be cached
        AssertJUnit.assertEquals(maxPoolSize, pool.getPoolSize());
    }
    
    
    /**
     * Test that initialization may only be called once, and pool properties
     * may not be changed after initialization.
     */
    @Test
    public void testInitialization() {
        BasicParserPool p = new BasicParserPool();
        boolean nsAware = p.isNamespaceAware();
        p.setNamespaceAware(!nsAware);
        p.setMaxPoolSize(20);
        
        try {
            p.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Parser pool failed initialization");
        }
        
        AssertJUnit.assertEquals(!nsAware, p.isNamespaceAware());
        
        try {
            p.initialize();
            Assert.fail("Parser pool initialization should have failed - already initialized");
        } catch (ComponentInitializationException e) {
            // expected
        }
        
        try {
            p.setNamespaceAware(true);
            Assert.fail("Parser pool was already initialized - property change disallowed");
        } catch (UnmodifiableComponentException e) {
            // expected
        }
        
        try {
            p.setMaxPoolSize(10);
            Assert.fail("Parser pool was already initialized - property change disallowed");
        } catch (UnmodifiableComponentException e) {
            // expected
        }
        
    }
    
    

    
    


}
