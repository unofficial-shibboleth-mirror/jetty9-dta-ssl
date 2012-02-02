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

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Tests for {@link NamespaceSupport}
 */
public class NamespaceSupportTest {

    private Element parent;

    private Element child;

    private Element grandchild;

    private BasicParserPool parserPool;

    private static final String NAMESPACE_1 = "http://example.org/NameSpace1";

    private static final String NAMESPACE_2 = "http://example.org/NameSpace2";

    private static final String NAMESPACE_3 = "http://example.org/NameSpace3";

    private static final String NAMESPACE_4 = "http://example.org/NameSpace4";

    private static final String DEFAULT_NAMESPACE_1 = "http://example.org/DefaultSpace1";

    private static final String DEFAULT_NAMESPACE_2 = "http://example.org/DefaultSpace2";

    private static final String PREFIX_1 = "myns1";

    private static final String PREFIX_2 = "myns2";

    private static final String PREFIX_3 = "myns3";

    private static final String PREFIX_4 = "myns4";

    @BeforeMethod public void setup() throws XMLParserException, ComponentInitializationException, SAXException,
            IOException, ResourceException {
        parserPool = new BasicParserPool();
        parserPool.initialize();
        DocumentBuilder builder = parserPool.getBuilder();
        try {
            ClasspathResource resource =
                    new ClasspathResource("data/net/shibboleth/utilities/java/support/xml/namespaceSupportTest.xml");
            resource.initialize();
            Document testFile = builder.parse(resource.getInputStream());

            parent = (Element) testFile.getFirstChild();

            child = ElementSupport.getFirstChildElement(parent);

            grandchild = ElementSupport.getFirstChildElement(child);

        } finally {
            parserPool.returnBuilder(builder);
        }
    }

    @Test public void testLookupNamespaceURI() {

        //
        // We need to work out what to do about this function
        //
        // Assert.assertTrue(false);

        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(parent, null, PREFIX_1), NAMESPACE_1,
                "lookupNamespaceURI(parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(parent, null, PREFIX_2), NAMESPACE_2,
                "lookupNamespaceURI(parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(parent, null, PREFIX_3), NAMESPACE_3,
                "lookupNamespaceURI(parent)");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(parent, null, PREFIX_4), "lookupNamespaceURI(parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(parent, null, null), DEFAULT_NAMESPACE_1,
                "lookupNamespaceURI(parent)");

        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_1), NAMESPACE_2,
                "lookupNamespaceURI(child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_2), NAMESPACE_2,
                "lookupNamespaceURI(child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_3), NAMESPACE_3,
                "lookupNamespaceURI(child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_4), NAMESPACE_4,
                "lookupNamespaceURI(child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, null), DEFAULT_NAMESPACE_1,
                "lookupNamespaceURI(child)");

        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_1), NAMESPACE_2,
                "lookupNamespaceURI(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_2), NAMESPACE_2,
                "lookupNamespaceURI(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_3), NAMESPACE_3,
                "lookupNamespaceURI(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_4), NAMESPACE_4,
                "lookupNamespaceURI(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, null), DEFAULT_NAMESPACE_2,
                "lookupNamespaceURI(grandchild)");

        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_1),
                "lookupNamespaceURI(grandchild, grandchild)");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_2),
                "lookupNamespaceURI(grandchild, grandchild)");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_3),
                "lookupNamespaceURI(grandchild, grandchild)");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_4),
                "lookupNamespaceURI(grandchild, grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, null), DEFAULT_NAMESPACE_2,
                "lookupNamespaceURI(grandchild, grandchild)");

        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, child, PREFIX_2),
                "lookupNamespaceURI(grandchild, child)");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(grandchild, child, PREFIX_3),
                "lookupNamespaceURI(grandchild, child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, child, PREFIX_1), NAMESPACE_2,
                "lookupNamespaceURI(grandchild, child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, child, PREFIX_4), NAMESPACE_4,
                "lookupNamespaceURI(grandchild, child)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, child, null), DEFAULT_NAMESPACE_2,
                "lookupNamespaceURI(grandchild, grandchild)");

        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, parent, PREFIX_1), NAMESPACE_2,
                "lookupNamespaceURI(child, parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, parent, PREFIX_2), NAMESPACE_2,
                "lookupNamespaceURI(child, parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, parent, PREFIX_3), NAMESPACE_3,
                "lookupNamespaceURI(child, parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, parent, PREFIX_4), NAMESPACE_4,
                "lookupNamespaceURI(child, parent)");
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, parent, null), DEFAULT_NAMESPACE_1,
                "lookupNamespaceURI(child, parent)");
        
        //
        // Finally check that a freshly created node does not have a namespace attribute
        // even if it was created with a defauklt namespace for that prefix.
        //
        Element element = ElementSupport.constructElement(parent.getOwnerDocument(), DEFAULT_NAMESPACE_1, "Element", PREFIX_1);
        Assert.assertEquals(element.lookupNamespaceURI(PREFIX_1), DEFAULT_NAMESPACE_1, "Default namespace correct");
        Assert.assertNull(NamespaceSupport.lookupNamespaceURI(element, null, PREFIX_1),  "Default namespace correct");
   }

    @Test public void testLookupPrefix() {

        //
        // We need to work out what to do about this function
        //
        // .assertTrue(false);

        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,NAMESPACE_1), PREFIX_1, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,NAMESPACE_2), PREFIX_2, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,NAMESPACE_3), PREFIX_3, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,NAMESPACE_4), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_1), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_2), null, "lookupPrefix(parent)");

        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, null,NAMESPACE_1), PREFIX_1, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, null,NAMESPACE_2), PREFIX_1, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, null,NAMESPACE_3), PREFIX_3, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, null,NAMESPACE_4), PREFIX_4, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_1), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_2), null, "lookupPrefix(parent)");

        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, null,NAMESPACE_1), PREFIX_1,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, null,NAMESPACE_2), PREFIX_1,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, null,NAMESPACE_3), PREFIX_3,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, null,NAMESPACE_4), PREFIX_4,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_1), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_2), null, "lookupPrefix(parent)");

        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, child, NAMESPACE_1), null,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, child, NAMESPACE_2), PREFIX_1,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, child, NAMESPACE_3), null,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(grandchild, child, NAMESPACE_4), PREFIX_4,
                "lookupPrefix(grandchild)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_1), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_2), null, "lookupPrefix(parent)");

        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, parent, NAMESPACE_1), PREFIX_1, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, parent, NAMESPACE_2), PREFIX_1, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, parent, NAMESPACE_3), PREFIX_3, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(child, parent, NAMESPACE_4), PREFIX_4, "lookupPrefix(child)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_1), null, "lookupPrefix(parent)");
        Assert.assertEquals(NamespaceSupport.lookupPrefix(parent, null,DEFAULT_NAMESPACE_2), null, "lookupPrefix(parent)");

        //
        // Finally check that a freshly created node does not have a namespace attribute
        // even if it was created with a default prefix for that namespace.
        //
        Element element = ElementSupport.constructElement(parent.getOwnerDocument(), DEFAULT_NAMESPACE_1, "Element", PREFIX_1);
        Assert.assertEquals(element.lookupPrefix(DEFAULT_NAMESPACE_1), PREFIX_1, "Default namespace correct");
        Assert.assertNull(NamespaceSupport.lookupPrefix(element, null, DEFAULT_NAMESPACE_1),  "Default namespace correct");
        
}

    @Test(dependsOnMethods = {"testLookupPrefix", "testLookupNamespaceURI"}) public void
            testAppendNamespaceDeclaration() {
        Element element = ElementSupport.constructElement(parent.getOwnerDocument(), null, "Element", null);


        NamespaceSupport.appendNamespaceDeclaration(element, NAMESPACE_1, PREFIX_1);
        Assert.assertEquals(element.lookupNamespaceURI(PREFIX_1), NAMESPACE_1,
                "appendNamespaceDeclaration - simple add");

        NamespaceSupport.appendNamespaceDeclaration(element, NAMESPACE_2, PREFIX_1);
        Assert.assertEquals(element.lookupPrefix(NAMESPACE_2), PREFIX_1,
                "appendNamespaceDeclaration - simple replace");
        Assert.assertEquals(element.lookupNamespaceURI(PREFIX_1), NAMESPACE_2,
                "appendNamespaceDeclaration - simple replace");

        boolean thrown = false;
        try {
            NamespaceSupport.appendNamespaceDeclaration(element, DEFAULT_NAMESPACE_1, null);
        } catch (DOMException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "cannot change default namespace");
        Assert.assertEquals(element.lookupPrefix(null), null,
                "appendNamespaceDeclaration - default NS cannot be changed");
        Assert.assertEquals(element.lookupNamespaceURI(null), null,
                "appendNamespaceDeclaration - default NS cannot be changed");

        //
        // Now, try with a prefix and concrete default
        //
        element = ElementSupport.constructElement(parent.getOwnerDocument(), DEFAULT_NAMESPACE_1, "Element", PREFIX_1);
        NamespaceSupport.appendNamespaceDeclaration(element, NAMESPACE_2, null);

        thrown = false;
        try {
            NamespaceSupport.appendNamespaceDeclaration(element, DEFAULT_NAMESPACE_2, PREFIX_1);
        } catch (DOMException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "cannot change that namespace");
        Assert.assertEquals(element.lookupNamespaceURI(PREFIX_1), DEFAULT_NAMESPACE_1,
                "appendNamespaceDeclaration - default NS can be changed");
        Assert.assertEquals(element.lookupPrefix(DEFAULT_NAMESPACE_1), PREFIX_1,
                "appendNamespaceDeclaration - that NS cannot be changed");

        //
        // Now, try with no prefix but with a concrete default
        //
        element = ElementSupport.constructElement(parent.getOwnerDocument(), DEFAULT_NAMESPACE_1, "Element", null);
        thrown = false;
        try {
            NamespaceSupport.appendNamespaceDeclaration(element, DEFAULT_NAMESPACE_2, null);
        } catch (DOMException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown, "cannot change default namespace");
        Assert.assertEquals(element.lookupNamespaceURI(null), DEFAULT_NAMESPACE_1,
                "appendNamespaceDeclaration - default NS cannot be changed");
        Assert.assertEquals(element.lookupPrefix(null), null,
                "appendNamespaceDeclaration - default NS cannot be changed");
        
        //
        // Try overiding parsed entities
        //
        NamespaceSupport.appendNamespaceDeclaration(grandchild, DEFAULT_NAMESPACE_1, null);
        Assert.assertEquals(grandchild.lookupNamespaceURI(null), DEFAULT_NAMESPACE_1, "Change default name space");
        
        
        NamespaceSupport.appendNamespaceDeclaration(child, NAMESPACE_3, PREFIX_2);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_2), NAMESPACE_3, "Change default name space");
    }

    @Test(dependsOnMethods = {"testLookupPrefix", "testLookupNamespaceURI", "testAppendNamespaceDeclaration"}) public
            void testRootNamespaces() {
        //
        // We are going to root an element we parsed.  Preconditions
        //
        Assert.assertEquals(child.lookupNamespaceURI(null), DEFAULT_NAMESPACE_1);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, null), DEFAULT_NAMESPACE_1);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, null), null);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_2), null);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_3), null);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_4), NAMESPACE_4);

        Assert.assertEquals(grandchild.lookupNamespaceURI(null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_1), null);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_2), null);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_3), null);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_4), null);

        NamespaceSupport.rootNamespaces(child);
        
        //
        // On rooting child will get a default and grandchild will get ns3 (because that is its prefix) and ns2 (because of the
        // attribute.  Nothing else should change
        //

        Assert.assertEquals(child.lookupNamespaceURI(null), DEFAULT_NAMESPACE_1);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, null), DEFAULT_NAMESPACE_1);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, null), DEFAULT_NAMESPACE_1);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_2), null);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_3), null);
        Assert.assertEquals(child.lookupNamespaceURI(PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, null, PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(child, child, PREFIX_4), NAMESPACE_4);

        Assert.assertEquals(grandchild.lookupNamespaceURI(null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, null), DEFAULT_NAMESPACE_2);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_1), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_1), null);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_2), NAMESPACE_2);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_3), NAMESPACE_3);
        Assert.assertEquals(grandchild.lookupNamespaceURI(PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, null, PREFIX_4), NAMESPACE_4);
        Assert.assertEquals(NamespaceSupport.lookupNamespaceURI(grandchild, grandchild, PREFIX_4), null);

    }

}
