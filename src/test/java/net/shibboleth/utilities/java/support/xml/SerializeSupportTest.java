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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * Tests for {@link NamespaceSupport}
 */
public class SerializeSupportTest {

    private Element parent;

    private BasicParserPool parserPool;

    @BeforeTest public void setup() throws XMLParserException, ComponentInitializationException, SAXException,
            IOException, ResourceException {
        parserPool = new BasicParserPool();
        parserPool.initialize();
        DocumentBuilder builder = parserPool.getBuilder();
        try {
            ClasspathResource resource =
                    new ClasspathResource("data/net/shibboleth/utilities/java/support/xml/serializeSupportTest.xml");
            resource.initialize();
            Document testFile = builder.parse(resource.getInputStream());

            parent = (Element) testFile.getFirstChild();

        } finally {
            parserPool.returnBuilder(builder);
        }
    }

    @Test public void testNodeToString() throws XMLParserException {
        //
        // Serialize then parse and serialize again
        //
        String s = SerializeSupport.nodeToString(parent);

        Document dom = parserPool.parse(new StringReader(s));

        Assert.assertEquals(SerializeSupport.nodeToString(dom.getFirstChild()), s, "Should serialize to same output");
    }

    @Test(dependsOnMethods = {"testNodeToString"}) public void testWriteNode() throws XMLParserException {
        //
        // Serialize then parse and serialize again
        //
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        SerializeSupport.writeNode(parent, output);

        byte data[] = output.toByteArray();

        Document dom = parserPool.parse(new ByteArrayInputStream(data));

        Assert.assertEquals(SerializeSupport.nodeToString(dom.getFirstChild()), SerializeSupport.nodeToString(parent),
                "Should serialize to same output");
    }

    @Test(dependsOnMethods = {"testNodeToString"}) public void testPrettyPrintXML() throws XMLParserException {
        //
        // Serialize then parse and serialize again
        //
        String s = SerializeSupport.prettyPrintXML(parent);

        Document dom = parserPool.parse(new StringReader(s));

        Assert.assertEquals(SerializeSupport.nodeToString(dom.getFirstChild()), SerializeSupport.nodeToString(parent),
                "Should serialize to same output");
    }

    @Test(dependsOnMethods = {"testNodeToString"}) public void testLSOps() throws XMLParserException {

        DOMImplementationLS domLS = SerializeSupport.getDomLsImplementation(parent);
        LSSerializer serializer = SerializeSupport.getLsSerializer(domLS, null);

        final LSOutput serializerOut = domLS.createLSOutput();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        serializerOut.setByteStream(output);

        serializer.write(parent, serializerOut);

        Document dom = parserPool.parse(new ByteArrayInputStream(output.toByteArray()));
        Assert.assertEquals(SerializeSupport.nodeToString(dom.getFirstChild()), SerializeSupport.nodeToString(parent),
                "Should serialize to same output");
    }
}
