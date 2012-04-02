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

package net.shibboleth.utilities.java.support.resource;

import java.io.IOException;
import java.io.InputStream;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class ClasspathResourceTest {

    private final String PATH = "data/net/shibboleth/utilities/java/support/resource/";

    private final String EXIST_RESOURCE = PATH + "classPathResourceData.dat";

    private final String NON_EXIST_RESOURCE = PATH + "nonExist.dat";

    private static final byte[] DATA = "123456789abcdefghijklmnopqrstuvwxyz".getBytes();

    private Resource resource;

    @BeforeMethod public void setup() throws ComponentInitializationException {
        resource = new ClasspathResource(EXIST_RESOURCE);
        resource.initialize();
    }

    @Test public void testExist() throws ResourceException {
        Assert.assertTrue(resource.exists(), "Preconfigured resource exists");
        boolean thrown = false;

        @SuppressWarnings("unused") Resource other = null;
        try {
            other = new ClasspathResource(NON_EXIST_RESOURCE);
        } catch (ConstraintViolationException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Non existant resource does not exist");
    }

    @Test public void testContents() throws ResourceException, IOException {
        InputStream stream = resource.getInputStream();

        for (byte b : DATA) {
            int i = stream.read();
            Assert.assertTrue(i >= 0, "should not be at end of resource");
            Assert.assertEquals(i, (int) b, "Input should natch what was put there");
        }
        Assert.assertTrue(stream.read() == -1, "should now be at end of resource");
        stream.close();
        stream = resource.getInputStream();
        Assert.assertEquals(stream.read(), (int) (DATA[0]), "New stream should be at head of resource");
    }
}
