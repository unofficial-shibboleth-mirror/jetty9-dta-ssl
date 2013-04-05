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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.LazyMap;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;

/** Set of helper functions for serializing/writing DOM nodes. */
public final class SerializeSupport {
    
    /** DOM configuration parameters used by LSSerializer in pretty print format output. */
    private static Map<String, Object> prettyPrintParams;

    /** Constructor. */
    private SerializeSupport() {

    }

    /**
     * Converts a Node into a String using the DOM, level 3, Load/Save serializer.
     * 
     * @param node the node to be written to a string
     * 
     * @return the string representation of the node
     */
    @Nonnull public static String nodeToString(@Nonnull final Node node) {
        Constraint.isNotNull(node, "Node may not be null");

        final ByteArrayOutputStream baout = new ByteArrayOutputStream();
        writeNode(node, baout);
        try {
            return new String(baout.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // all VMs are required to support UTF-8, if it's not something is really wrong
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a Node into a String, using the DOM, level 3, Load/Save serializer. A serializer
     * option of 'format-pretty-print=true' is used to produce the pretty-print formatting.
     * 
     * @param node xml node to print
     * 
     * @return pretty-printed xml
     */
    @Nonnull public static String prettyPrintXML(@Nonnull final Node node) {
        Constraint.isNotNull(node, "Node may not be null");
        
        final ByteArrayOutputStream baout = new ByteArrayOutputStream();
        writeNode(node, baout, prettyPrintParams);
        try {
            return new String(baout.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // all VMs are required to support UTF-8, if it's not something is really wrong
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the output stream to write the XML to
     */
    public static void writeNode(@Nonnull final Node node, @Nonnull final OutputStream output) {
        writeNode(node, output, null);
    }
    
    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the output stream to write the XML to
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer instance, obtained
     *            via {@link LSSerializer#getDomConfig()}. May be null.
     */
    public static void writeNode(@Nonnull final Node node, @Nonnull final OutputStream output,
            @Nullable final Map<String, Object> serializerParams) {
        Constraint.isNotNull(node, "Node may not be null");
        Constraint.isNotNull(output, "Outputstream may not be null");

        final DOMImplementationLS domImplLS = getDomLsImplementation(node); 
        final LSSerializer serializer = getLsSerializer(domImplLS, serializerParams);

        final LSOutput serializerOut = domImplLS.createLSOutput();
        serializerOut.setByteStream(output);

        serializer.write(node, serializerOut);
    }

    /**
     * Obtain a the DOM, level 3, Load/Save serializer {@link LSSerializer} instance from the given
     * {@link DOMImplementationLS} instance.
     * 
     * <p>
     * The serializer instance will be configured with the parameters passed as the <code>serializerParams</code>
     * argument. It will also be configured with an {@link LSSerializerFilter} that shows all nodes to the filter, and
     * accepts all nodes shown.
     * </p>
     * 
     * @param domImplLS the DOM Level 3 Load/Save implementation to use
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer instance, obtained
     *            via {@link LSSerializer#getDomConfig()}. May be null.
     * 
     * @return a new LSSerializer instance
     */
    @Nonnull public static LSSerializer getLsSerializer(@Nonnull final DOMImplementationLS domImplLS,
            @Nullable final Map<String, Object> serializerParams) {
        Constraint.isNotNull(domImplLS, "DOM implementation can not be null");
        final LSSerializer serializer = domImplLS.createLSSerializer();

        serializer.setFilter(new LSSerializerFilter() {

            public short acceptNode(Node arg0) {
                return FILTER_ACCEPT;
            }

            public int getWhatToShow() {
                return SHOW_ALL;
            }
        });

        if (serializerParams != null) {
            final DOMConfiguration serializerDOMConfig = serializer.getDomConfig();
            for (String key : serializerParams.keySet()) {
                serializerDOMConfig.setParameter(key, serializerParams.get(key));
            }
        }

        return serializer;
    }

    /**
     * Gets the DOM, level 3, Load/Store implementation associated with the given node.
     * 
     * @param node the node, never null
     * 
     * @return the Load/Store implementation, never null
     */
    @Nonnull public static DOMImplementationLS getDomLsImplementation(@Nonnull final Node node) {
        Constraint.isNotNull(node, "DOM node can not be null");
        final DOMImplementation domImpl;
        if (node instanceof Document) {
            domImpl = ((Document) node).getImplementation();
        } else {
            domImpl = node.getOwnerDocument().getImplementation();
        }

        return (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
    }
    
    static {
        prettyPrintParams = new LazyMap<String, Object>();
        prettyPrintParams.put("format-pretty-print", Boolean.TRUE);
    }

}