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

/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2007 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */

package net.shibboleth.utilities.java.support.codec;

import net.shibboleth.utilities.java.support.codec.HTMLEncoder;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * HTMLEncoder tests.
 * 
 * Based on org.owasp.esapi.reference.EncoderTest.
 */
public class HTMLEncoderTest {

    @Test public void testEncodeForHTML() throws Exception {

        Assert.assertEquals(HTMLEncoder.encodeForHTML(null), null);

        // test invalid characters are replaced with spaces
        Assert.assertEquals(
                HTMLEncoder.encodeForHTML("a" + (char) 0 + "b" + (char) 4 + "c" + (char) 128 + "d" + (char) 150 + "e"
                        + (char) 159 + "f" + (char) 9 + "g"), "a&#xfffd;b&#xfffd;c&#xfffd;d&#xfffd;e&#xfffd;f&#x9;g");

        Assert.assertEquals(HTMLEncoder.encodeForHTML("<script>"), "&lt;script&gt;");

        Assert.assertEquals(HTMLEncoder.encodeForHTML("&lt;script&gt;"), "&amp;lt&#x3b;script&amp;gt&#x3b;");

        Assert.assertEquals(HTMLEncoder.encodeForHTML("!@$%()=+{}[]"),
                "&#x21;&#x40;&#x24;&#x25;&#x28;&#x29;&#x3d;&#x2b;&#x7b;&#x7d;&#x5b;&#x5d;");

        // Would need to port canonicalize() method.
        // Assert.assertEquals(encoder.encodeForHTML("&#33;&#64;&#36;&#37;&#40;&#41;&#61;&#43;&#123;&#125;&#91;&#93;"),
        // "&#x21;&#x40;&#x24;&#x25;&#x28;&#x29;&#x3d;&#x2b;&#x7b;&#x7d;&#x5b;&#x5d;");

        Assert.assertEquals(HTMLEncoder.encodeForHTML(",.-_ "), ",.-_ ");

        Assert.assertEquals(HTMLEncoder.encodeForHTML("dir&"), "dir&amp;");

        Assert.assertEquals(HTMLEncoder.encodeForHTML("one&two"), "one&amp;two");

        Assert.assertEquals("" + (char) 12345 + (char) 65533 + (char) 1244, "" + (char) 12345 + (char) 65533
                + (char) 1244);

        Assert.assertEquals(HTMLEncoder.encodeForHTML("http://example.org/response"),
                "http&#x3a;&#x2f;&#x2f;example.org&#x2f;response");
    }

    @Test public void testEncodeForHTMLAttribute() {

        Assert.assertEquals(HTMLEncoder.encodeForHTMLAttribute(null), null);

        Assert.assertEquals(HTMLEncoder.encodeForHTMLAttribute("<script>"), "&lt;script&gt;");

        Assert.assertEquals(HTMLEncoder.encodeForHTMLAttribute(",.-_"), ",.-_");

        Assert.assertEquals(HTMLEncoder.encodeForHTMLAttribute(" !@$%()=+{}[]"),
                "&#x20;&#x21;&#x40;&#x24;&#x25;&#x28;&#x29;&#x3d;&#x2b;&#x7b;&#x7d;&#x5b;&#x5d;");

        Assert.assertEquals(HTMLEncoder.encodeForHTMLAttribute("http://example.org/response"),
                "http&#x3a;&#x2f;&#x2f;example.org&#x2f;response");
    }

}
