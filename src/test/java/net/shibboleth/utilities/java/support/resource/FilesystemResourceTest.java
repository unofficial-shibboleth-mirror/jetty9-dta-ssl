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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class FilesystemResourceTest {
    
    private static final byte[] DATA = "123456789abcdefghijklmnopqrstuvwxyz".getBytes();
    private File file;
    private Resource resource;

    @BeforeMethod
    public void setup() throws IOException, ComponentInitializationException{
        file = File.createTempFile("ShibbolethTestFile-", null);
        String path = file.getAbsolutePath();
        OutputStream stream = new FileOutputStream(file);
        stream.write(DATA);
        stream.close();        
        
        resource = new FilesystemResource(path);
        resource.initialize();
    }
    
    @Test
    public void testFileExists() throws ResourceException  {
        Assert.assertTrue(resource.exists(), "File exists");
        file.delete();
        Assert.assertFalse(resource.exists(), "File exists");   
    }
    
    @Test
    public void testContents() throws ResourceException, IOException {
        InputStream stream = resource.getInputStream();
        
        for (byte b : DATA) {
            int i = stream.read();
            Assert.assertTrue(i >= 0, "should not be at end of resource");
            Assert.assertEquals(i, (int)b, "Input should natch what was put there");
        }
        Assert.assertTrue(stream.read() == -1, "should now be at end of resource");
        stream.close();
        stream = resource.getInputStream();
        Assert.assertEquals(stream.read(), (int)(DATA[0]), "New stream should be at head of resource");
    }
}
