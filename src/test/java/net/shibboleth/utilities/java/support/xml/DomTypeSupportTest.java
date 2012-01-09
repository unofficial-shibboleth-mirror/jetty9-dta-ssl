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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resource.ClasspathResource;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Tests for {@link DomTypeSupport};
 */
public class DomTypeSupportTest {

    private ParserPool parserPool;
    private Element xsStringXSITypeElement;
    private Element noXSITypeElement;
    
    @BeforeTest public void setup() throws ComponentInitializationException, SAXException, IOException, ResourceException, XMLParserException {
        BasicParserPool pool = new BasicParserPool();
        pool.initialize();
        parserPool = pool;
        
        DocumentBuilder builder = parserPool.getBuilder();
        Resource res = new ClasspathResource("data/net/shibboleth/utilities/java/support/xml/getXSIType.xml");
        res.initialize();
        xsStringXSITypeElement = (Element) builder.parse(res.getInputStream()).getFirstChild();

        res = new ClasspathResource("data/net/shibboleth/utilities/java/support/xml/noXSIType.xml");
        res.initialize();
        noXSITypeElement = (Element) builder.parse(res.getInputStream()).getFirstChild();
        
        if (null != builder) {
            parserPool.returnBuilder(builder);
        }

    }
    
    
    @Test public void testDateTimeToLong() {
        Assert.assertEquals(DomTypeSupport.dateTimeToLong("1970-01-01T00:00:01Z"), 1000, "Epoch plus one second");
        Assert.assertEquals(DomTypeSupport.dateTimeToLong("1969-12-31T23:59:59Z"), -1000, "Epoch minus one second");
        Assert.assertEquals(DomTypeSupport.dateTimeToLong("1970-01-01T00:00:00-05:00"), 5 * 3600 * 1000,
                "Epoch minus 5 hours timezone");
    }

    @Test public void testLongToDateTime() {
        Assert.assertEquals(DomTypeSupport.longToDateTime(1000), "1970-01-01T00:00:01.000Z", "Epoch plus one second");
        Assert.assertEquals(DomTypeSupport.longToDateTime(-1000), "1969-12-31T23:59:59.000Z", "Epoch minus one second");
    }

    @Test public void testDurationToLong() {
        Assert.assertEquals(DomTypeSupport.durationToLong("P0Y0M0DT00H00M01S"), 1000, "One second duration");
        Assert.assertEquals(DomTypeSupport.durationToLong("-P1D"), -1 * 1000 * 24 * 3600, "Back One day duration");
    }

    @Test public void testLongToDuration() {
        Assert.assertEquals(DomTypeSupport.longToDuration(1000), "P0Y0M0DT0H0M1.000S", "One second duration");
        Assert.assertEquals(DomTypeSupport.longToDuration(-1000*24*3600), "-P0Y0M1DT0H0M0.000S", "Back One day duration");
    }
    
    @Test public void testGetXSIType() {
        Assert.assertEquals(DomTypeSupport.getXSIType(xsStringXSITypeElement),
                new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"),
                "XSI type clash");
        Assert.assertNull(DomTypeSupport.getXSIType(noXSITypeElement), "No xsiType expected");
    }

    @Test public void testHasXSIType() {
        Assert.assertTrue(DomTypeSupport.hasXSIType(xsStringXSITypeElement)," Expected xsi:type");
        Assert.assertFalse(DomTypeSupport.hasXSIType(noXSITypeElement), "No xsiType expected");
    }

}
