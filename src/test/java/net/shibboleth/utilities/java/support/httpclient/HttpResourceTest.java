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

import java.io.File;
import java.io.InputStream;

import net.shibboleth.utilities.java.support.component.DestroyedComponentException;
import net.shibboleth.utilities.java.support.component.UninitializedComponentException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.apache.http.client.HttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/** Unit test for {@link HttpResource}. */
public class HttpResourceTest {

    private HttpClient httpClient;

    private File backupFile;

    private File propFile;

    @BeforeTest public void testSetUp() {
        // Be sure to keep connection pooling enabled. TestNG runs its tests in parallel
        // and so without connection pooling one test might clobber the connection of another
        HttpClientBuilder clientBuilder = new HttpClientBuilder();
        httpClient = clientBuilder.buildClient();

        backupFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "index");
        if (backupFile.exists()) {
            backupFile.delete();
        }

        propFile = new File(backupFile.getAbsolutePath() + ".props");
        if (propFile.exists()) {
            propFile.delete();
        }
    }

    @AfterMethod public void postMethod() {
        if (backupFile.exists()) {
            backupFile.delete();
        }

        if (propFile.exists()) {
            propFile.delete();
        }
    }

    @Test public void testInvalidInstantiation() {
        try {
            new HttpResource(null, "http://example.org");
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }

        try {
            new HttpResource(httpClient, null);
            Assert.fail();
        } catch (ConstraintViolationException e) {
            // expected this
        }

    }

    @Test public void testInitialization() throws Exception {
        HttpResource resource = new HttpResource(httpClient, "http://shibboleth.net");
        Assert.assertFalse(resource.isInitialized());

        try {
            resource.exists();
            Assert.fail();
        } catch (UninitializedComponentException e) {
            // expected this
        }

        try {
            resource.getInputStream();
            Assert.fail();
        } catch (UninitializedComponentException e) {
            // expected this
        }

        try {
            resource.getLastModifiedTime();
            Assert.fail();
        } catch (UninitializedComponentException e) {
            // expected this
        }

        resource.initialize();
        Assert.assertTrue(resource.isInitialized());
    }

    @Test public void testDestruction() throws Exception {
        HttpClient client = new HttpClientBuilder().buildClient();
        HttpResource resource = new HttpResource(client, "http://shibboleth.net");
        Assert.assertFalse(resource.isDestroyed());

        resource.destroy();
        Assert.assertTrue(resource.isDestroyed());

        try {
            resource.exists();
            Assert.fail();
        } catch (DestroyedComponentException e) {
            // expected this
        }

        try {
            resource.getInputStream();
            Assert.fail();
        } catch (DestroyedComponentException e) {
            // expected this
        }

        try {
            resource.getLastModifiedTime();
            Assert.fail();
        } catch (DestroyedComponentException e) {
            // expected this
        }
    }

    @Test public void testValidUrl() throws Exception {
        String url = "http://shibboleth.net";

        HttpResource resource = new HttpResource(httpClient, url);
        resource.initialize();
        Assert.assertEquals(resource.getLocation(), url);

        Assert.assertTrue(resource.exists());

        InputStream ins = resource.getInputStream();
        Assert.assertNotNull(ins);
        Assert.assertTrue(ins.available() > 0);
        ins.close();
    }

    @Test public void testInvalidUrl() throws Exception {
        String url = "http://shibboleth.net/lkjeiocjkljn";

        HttpResource resource = new HttpResource(httpClient, url);
        resource.initialize();
        Assert.assertEquals(resource.getLocation(), url);

        Assert.assertFalse(resource.exists());

        try {
            resource.getInputStream().close();
            Assert.fail();
        } catch (ResourceException e) {
            // expected this
        }
    }
}