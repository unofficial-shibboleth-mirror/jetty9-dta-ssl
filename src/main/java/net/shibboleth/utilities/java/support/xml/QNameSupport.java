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

import java.util.StringTokenizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Set of helper methods for working with DOM QNames. */
public final class QNameSupport {

    /** Constructor. */
    private QNameSupport() {
    }

    /**
     * Constructs a QName from a string (attribute element content) value.
     * 
     * @param qname the QName string
     * @param owningElement parent DOM element of the Node which contains the QName value
     * 
     * @return the QName respresented by the string
     */
    @Nonnull public static QName constructQName(@Nonnull final Element owningElement, @Nonnull final String qname) {
        Assert.isNotNull(owningElement, "Owning element may not be null");
        Assert.isNotNull(qname, "Name may not be null");

        String nsPrefix;
        String name;
        if (qname.indexOf(":") > -1) {
            final StringTokenizer qnameTokens = new StringTokenizer(qname, ":");
            nsPrefix = StringSupport.trim(qnameTokens.nextToken());
            name = qnameTokens.nextToken();
        } else {
            nsPrefix = null;
            name = qname;
        }

        final String nsURI = owningElement.lookupNamespaceURI(nsPrefix);
        return constructQName(nsURI, name, nsPrefix);
    }

    /**
     * Constructs a QName.
     * 
     * @param namespaceURI the namespace of the QName
     * @param localName the local name of the QName
     * @param prefix the prefix of the QName, may be null
     * 
     * @return the QName
     */
    @Nonnull public static QName constructQName(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String prefix) {
        String trimmedLocalName = Assert.isNotNull(StringSupport.trimOrNull(localName), "Local name may not be null");
        String trimmedPrefix = StringSupport.trimOrNull(prefix);

        if (trimmedPrefix == null) {
            return new QName(StringSupport.trimOrNull(namespaceURI), trimmedLocalName);
        } else {
            return new QName(StringSupport.trimOrNull(namespaceURI), trimmedLocalName, trimmedPrefix);
        }
    }

    /**
     * Gets the QName for the given DOM node.
     * 
     * @param domNode the DOM node
     * 
     * @return the QName for the element or null if the element was null
     */
    @Nullable public static QName getNodeQName(@Nullable final Node domNode) {
        if (domNode == null) {
            return null;
        }

        return constructQName(domNode.getNamespaceURI(), domNode.getLocalName(), domNode.getPrefix());
    }

    /**
     * Converts a QName into a string that can be used for attribute values or element content.
     * 
     * @param qname the QName to convert to a string
     * 
     * @return the string value of the QName
     */
    @Nonnull public static String qnameToContentString(@Nonnull final QName qname) {
        Assert.isNotNull(qname, "QName may not be null");

        final StringBuffer buf = new StringBuffer();
        final String s = StringSupport.trimOrNull(qname.getPrefix());
        if (s != null) {
            buf.append(qname.getPrefix());
            buf.append(":");
        }
        buf.append(qname.getLocalPart());

        return buf.toString();
    }
}