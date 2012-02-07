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

import java.io.File;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.ResourceException;
import net.shibboleth.utilities.java.support.xml.SchemaBuilder.SchemaLanguage;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 * Tests for {@link NamespaceSupport}
 */
public class SchemaBuilderTest {

    private static final String TEST_DIR = "data/net/shibboleth/utilities/java/support/xml/schemaBuilderTestDir/";

    private static final String FILE_ROOT = "src/test/resources/";

    private static final String FIRST_SCHEMA_FILE = "schemaBuilderTest-schemaFirstLoaded.xsd";

    private static final String SECOND_SCHEMA_FILE = "schemaBuilderTest-schemaSecondLoaded.xsd";

    private Resource works;

    private Resource fails;

    private StreamSource workingSource() throws ResourceException {
        return new StreamSource(works.getInputStream());
    }

    private StreamSource failingSource() throws ResourceException {
        return new StreamSource(fails.getInputStream());
    }

    @BeforeTest public void setup() throws ResourceException, ComponentInitializationException {
        works = new ClasspathResource(TEST_DIR + "schemaBuilderTest-works.xml");
        works.initialize();
        fails = new ClasspathResource(TEST_DIR + "schemaBuilderTest-fails.xml");
        fails.initialize();
    }

    @Test public void testString() throws SAXException, IOException, ResourceException {

        Schema schema =
                SchemaBuilder.buildSchema(SchemaLanguage.XML, FILE_ROOT + TEST_DIR + FIRST_SCHEMA_FILE, FILE_ROOT
                        + TEST_DIR + SECOND_SCHEMA_FILE);

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

    @Test public void testStringDir() throws SAXException, IOException, ResourceException {
        Schema schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, FILE_ROOT + TEST_DIR);

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

    @Test public void testFiles() throws SAXException, IOException, ResourceException {
        File first = new File(FILE_ROOT + TEST_DIR + FIRST_SCHEMA_FILE);
        File second = new File(FILE_ROOT + TEST_DIR + SECOND_SCHEMA_FILE);

        Schema schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, first, second);

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

    @Test public void testDir() throws SAXException, IOException, ResourceException {
        File dir = new File(FILE_ROOT + TEST_DIR);

        Schema schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, dir);

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

    @Test public void testResource() throws SAXException, IOException, ResourceException, ComponentInitializationException {
        Resource first = new ClasspathResource(TEST_DIR + FIRST_SCHEMA_FILE);
        Resource second = new ClasspathResource(TEST_DIR + SECOND_SCHEMA_FILE);
        first.initialize();
        second.initialize();

        Schema schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, first, second);

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

    @Test public void testInputStream() throws SAXException, IOException, ResourceException, ComponentInitializationException {
        Resource first = new ClasspathResource(TEST_DIR + FIRST_SCHEMA_FILE);
        Resource second = new ClasspathResource(TEST_DIR + SECOND_SCHEMA_FILE);
        first.initialize();
        second.initialize();

        Schema schema = SchemaBuilder.buildSchema(SchemaLanguage.XML, first.getInputStream(), second.getInputStream());

        Validator validator = schema.newValidator();

        validator.validate(workingSource());

        boolean thrown = false;
        StreamSource fails = failingSource();
        try {
            validator.validate(fails);
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "Should fail to validate");
    }

}
