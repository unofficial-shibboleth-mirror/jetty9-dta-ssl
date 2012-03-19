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

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** {@link SimpleNamespaceContext} test. */
public class SimpleNamespaceContextTest {

    private final static String PREFIX_A = "a";

    private final static String URI_A = "value:of:a";

    private final static String PREFIX_B = "b";

    private final static String URI_B = "value:of:b";

    /** Standard set of mappings. */
    private NamespaceContext stdContext;

    /** Create standard set of mappings for use by multiple tests. */
    @BeforeClass public void createStandardMappings() {
        Map<String, String> prefixMappings = new HashMap<String, String>();
        prefixMappings.put(PREFIX_A, URI_A);
        prefixMappings.put(PREFIX_B, URI_B);
        stdContext = new SimpleNamespaceContext(prefixMappings);
    }

    /** Test for getNamespaceURI method. */
    @Test public void testGetNamespaceURI() {
        Assert.assertEquals(stdContext.getNamespaceURI(PREFIX_A), URI_A);
        Assert.assertEquals(stdContext.getNamespaceURI(PREFIX_B), URI_B);
        Assert.assertEquals(stdContext.getNamespaceURI(XmlConstants.XML_PREFIX), XmlConstants.XML_NS);
        Assert.assertEquals(stdContext.getNamespaceURI(XmlConstants.XMLNS_PREFIX), XmlConstants.XMLNS_NS);
        Assert.assertEquals(stdContext.getNamespaceURI("c"), XMLConstants.NULL_NS_URI);
    }

    @Test public void testGetPrefix() {
        Assert.assertEquals(stdContext.getPrefix(URI_A), PREFIX_A);
        Assert.assertEquals(stdContext.getPrefix(URI_B), PREFIX_B);
        Assert.assertEquals(stdContext.getPrefix(XmlConstants.XML_NS), XmlConstants.XML_PREFIX);
        Assert.assertEquals(stdContext.getPrefix(XmlConstants.XMLNS_NS), XmlConstants.XMLNS_PREFIX);
        Assert.assertNull(stdContext.getPrefix("value:of:c"));
    }

    @Test public void testGetPrefixes() {
        Assert.assertEquals(stdContext.getPrefixes(URI_A).next(), PREFIX_A);
        Assert.assertEquals(stdContext.getPrefixes(URI_B).next(), PREFIX_B);
        Assert.assertEquals(stdContext.getPrefixes(XmlConstants.XML_NS).next(), XmlConstants.XML_PREFIX);
        Assert.assertEquals(stdContext.getPrefixes(XmlConstants.XMLNS_NS).next(), XmlConstants.XMLNS_PREFIX);
        Assert.assertFalse(stdContext.getPrefixes("value:of:c").hasNext());
    }
}