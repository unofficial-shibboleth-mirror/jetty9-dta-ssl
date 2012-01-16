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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 */
public class ElementSupportTest {

    private static final String TEST_NS = "http://example.org/NameSpace";

    private static final String OTHER_NS = "http://example.org/OtherSpace";

    private static final String TEST_PREFIX = "testns";

    private static final String TEST_ELEMENT_NAME = "Element1";

    private static final String ROOT_ELEMENT = "Container";

    private static final QName TEST_ELEMENT_QNAME = new QName(TEST_NS, TEST_ELEMENT_NAME, TEST_PREFIX);

    private BasicParserPool parserPool;

    private Document testFile;

    private Element rootElement;

    @BeforeTest public void setUp() throws XMLParserException, ComponentInitializationException, SAXException,
            IOException, ResourceException {
        parserPool = new BasicParserPool();
        parserPool.initialize();

        DocumentBuilder builder = parserPool.getBuilder();
        try {

            ClasspathResource resource =
                    new ClasspathResource("data/net/shibboleth/utilities/java/support/xml/elementSupportTest.xml");
            resource.initialize();
            testFile = builder.parse(resource.getInputStream());
            rootElement = (Element) testFile.getFirstChild();

        } finally {
            parserPool.returnBuilder(builder);
        }
    }

    @Test public void testIsElementNamed() {
        Assert.assertFalse(ElementSupport.isElementNamed(null, TEST_NS, ROOT_ELEMENT),
                "not find if provided element is null");
        Assert.assertFalse(ElementSupport.isElementNamed(rootElement, null, ROOT_ELEMENT),
                "not find if provided namespace is null");
        Assert.assertFalse(ElementSupport.isElementNamed(rootElement, TEST_NS, null),
                "not find if provided attribute name is null");
        Assert.assertFalse(ElementSupport.isElementNamed(rootElement, null),
                "not find if provided attribute QName is null");

        Assert.assertTrue(ElementSupport.isElementNamed(rootElement, new QName(TEST_NS, ROOT_ELEMENT, TEST_PREFIX)),
                "lookup against QNAME");
        Assert.assertTrue(ElementSupport.isElementNamed(rootElement, TEST_NS, ROOT_ELEMENT), "lookup against name");

        Assert.assertFalse(ElementSupport.isElementNamed(rootElement, TEST_NS, ROOT_ELEMENT.toUpperCase()),
                "lookup against upper of name");
        Assert.assertFalse(ElementSupport.isElementNamed(rootElement, TEST_NS.toUpperCase(), ROOT_ELEMENT),
                "lookup against upper of namespace");

    }

    @Test(dependsOnMethods = {"testIsElementNamed"}) public void testGetChildElements() {
        Assert.assertTrue(ElementSupport.getChildElements(null).isEmpty(), "Null should provide empty list");
        Assert.assertTrue(ElementSupport.getChildElements(rootElement, null).isEmpty(),
                "Null QName should provide empty list");

        Assert.assertEquals(ElementSupport.getChildElements(rootElement).size(), 8, "unnanmed element lookup");
        List<Element> list = ElementSupport.getChildElements(rootElement, TEST_ELEMENT_QNAME);
        Assert.assertEquals(list.size(), 3, "Named element lookup");
        for (Element e : list) {
            Assert.assertTrue(ElementSupport.isElementNamed(e, TEST_ELEMENT_QNAME));
        }
    }

    @Test(dependsOnMethods = {"testIsElementNamed"}) public void testGetChildElementsByTagName() {
        Assert.assertTrue(ElementSupport.getChildElementsByTagName(rootElement, null).isEmpty(),
                "getChildElementsByTagName: Null name should provide empty list");
        Assert.assertTrue(ElementSupport.getChildElementsByTagName(null, TEST_ELEMENT_NAME).isEmpty(),
                "getChildElementsByTagName: Null root  should provide empty list");
        Assert.assertTrue(ElementSupport.getChildElementsByTagNameNS(rootElement, TEST_NS, null).isEmpty(),
                "getChildElementsByTagName: Null name should provide empty list");
        Assert.assertTrue(ElementSupport.getChildElementsByTagNameNS(rootElement, null, TEST_ELEMENT_NAME).isEmpty(),
                "getChildElementsByTagNameNS: Null name space should provide empty list");
        Assert.assertTrue(ElementSupport.getChildElementsByTagNameNS(null, TEST_NS, TEST_ELEMENT_NAME).isEmpty(),
                "getChildElementsByTagNameNS: Null root  should provide empty list");

        List<Element> list = ElementSupport.getChildElementsByTagName(rootElement, TEST_ELEMENT_NAME);
        Assert.assertEquals(list.size(), 5, "getChildElementsByTagName size");
        int i = 0;
        for (Element e : list) {
            if (ElementSupport.isElementNamed(e, TEST_ELEMENT_QNAME)) {
                i++;
            }
        }
        Assert.assertEquals(i, 3, "getChildElementsByTagName size");

        list = ElementSupport.getChildElementsByTagNameNS(rootElement, TEST_NS, TEST_ELEMENT_NAME);
        Assert.assertEquals(list.size(), 3, "getChildElementsByTagName size");
        for (Element e : list) {
            Assert.assertTrue(ElementSupport.isElementNamed(e, TEST_ELEMENT_QNAME));
        }

    }

    @Test(dependsOnMethods = {"testGetChildElementsByTagName"}) public void testGetElementAncestor() {
        Assert.assertNull(ElementSupport.getElementAncestor(null),
                "getElementAncestor: Null element should provide null result");
        Assert.assertNull(ElementSupport.getElementAncestor(rootElement),
                "getElementAncestor: root node should provide null result");

        Element child = ElementSupport.getChildElementsByTagName(rootElement, "Element4").get(0);
        Element grandChild = ElementSupport.getChildElementsByTagName(child, "Element1").get(0);

        Assert.assertEquals(ElementSupport.getElementAncestor(child), rootElement, "getElementAncestor for child");
        Assert.assertEquals(ElementSupport.getElementAncestor(grandChild), child, "getElementAncestor for grand child");

    }

    @Test(dependsOnMethods = {"testGetChildElementsByTagName"}) public void testGetElementContentAsList() {
        Assert.assertTrue(ElementSupport.getElementContentAsList(null).isEmpty(),
                "getElementContentAsList: Null element should provide empty result");

        Element interesting =
                ElementSupport.getChildElementsByTagName(
                        ElementSupport.getChildElementsByTagName(rootElement, "Element4").get(0), "Element1").get(0);

        Assert.assertEquals(ElementSupport.getElementContentAsList(interesting),
                Arrays.asList("Some", "Random", "test"));

    }

    @Test public void testGetElementContentAsQName() {
        Assert.assertNull(ElementSupport.getElementContentAsQName(null),
                "getElementContentAsQName: Null element should provide empty result");
        Assert.assertNull(ElementSupport.getElementContentAsQName(rootElement),
                "getElementContentAsQName: Empty element should provide empty result");

        Element parent = ElementSupport.getChildElementsByTagName(rootElement, "Element4").get(0);
        List<Element> children = ElementSupport.getChildElementsByTagName(parent, "QName");

        Assert.assertEquals(ElementSupport.getElementContentAsQName(children.get(0)), new QName(OTHER_NS, "localname"));
        Assert.assertNull(ElementSupport.getElementContentAsQName(children.get(1)),
                "getElementContentAsQName: invalid qname shiuld return null");
    }

    @Test(dependsOnMethods = {"testIsElementNamed"}) public void testGetChildAndNext() {
        Assert.assertNull(ElementSupport.getFirstChildElement(null),
                "getFirstChildElement: Null element should provide null result");

        Assert.assertNull(ElementSupport.getNextSiblingElement(null),
                "getNextSiblingElement: Null element should provide null result");

        Element element = ElementSupport.getFirstChildElement(rootElement);
        Assert.assertTrue(ElementSupport.isElementNamed(element, TEST_ELEMENT_QNAME), "getFirstChildElement");
        Assert.assertNull(ElementSupport.getFirstChildElement(element),
                "getFirstChildElement: Empty element should provide null result");

        element = ElementSupport.getNextSiblingElement(element);
        Assert.assertTrue(ElementSupport.isElementNamed(element, TEST_ELEMENT_QNAME), "getNextSiblingElement 1");

        element = ElementSupport.getNextSiblingElement(element);
        QName qName = new QName(TEST_NS, "Element2", TEST_PREFIX);
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 2");

        element = ElementSupport.getNextSiblingElement(element);
        qName = new QName(TEST_NS, TEST_ELEMENT_NAME, "mynsagain");
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 3");

        element = ElementSupport.getNextSiblingElement(element);
        qName = new QName(OTHER_NS, TEST_ELEMENT_NAME, TEST_PREFIX);
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 4 ");

        element = ElementSupport.getNextSiblingElement(element);
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 5");

        element = ElementSupport.getNextSiblingElement(element);
        qName = new QName(OTHER_NS, "Element2", TEST_PREFIX);
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 6");

        element = ElementSupport.getNextSiblingElement(element);
        qName = new QName(TEST_NS, "Element4", TEST_PREFIX);
        Assert.assertTrue(ElementSupport.isElementNamed(element, qName), "getNextSiblingElement 7");

        Assert.assertNull(ElementSupport.getNextSiblingElement(element),
                "getNextSiblingElement: final element should provide null result");
    }

    @Test public void testGetIndexedChildElements() {
        // Test pass first
        Map<QName, List<Element>> map = ElementSupport.getIndexedChildElements(rootElement);
        
        Assert.assertEquals(map.get(TEST_ELEMENT_QNAME).size(), 3, "getIndexedChildElements for " + TEST_ELEMENT_QNAME);
        map.remove(TEST_ELEMENT_QNAME);
        
        QName qname = new QName(TEST_NS, "Element2", TEST_PREFIX);
        Assert.assertEquals(map.get(qname).size(), 1, "getIndexedChildElements for " + qname);
        map.remove(qname);
        
        qname = new QName(OTHER_NS, "Element1", "otherns");
        Assert.assertEquals(map.get(qname).size(), 2, "getIndexedChildElements for " + qname);
        map.remove(qname);

        qname = new QName(OTHER_NS, "Element2", "otherns");
        Assert.assertEquals(map.get(qname).size(), 1, "getIndexedChildElements for " + qname);
        map.remove(qname);

        qname = new QName(TEST_NS, "Element4", TEST_PREFIX);
        Assert.assertEquals(map.get(qname).size(), 1, "getIndexedChildElements for " + qname);
        map.remove(qname);

        Assert.assertTrue(map.isEmpty(), "getIndexedChildElements found extra elements");
    }

}
