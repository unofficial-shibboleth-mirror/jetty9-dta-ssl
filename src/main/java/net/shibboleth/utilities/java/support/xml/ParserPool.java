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

import java.io.InputStream;
import java.io.Reader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;

import net.shibboleth.utilities.java.support.component.DestructableComponent;

import org.w3c.dom.Document;

/** A pool of XML parsers. */
public interface ParserPool extends DestructableComponent {

    /**
     * Gets a builder from the pool.
     * 
     * @return a builder from the pool
     * 
     * @throws XMLParserException thrown if the document builder factory is misconfigured
     */
    @Nonnull public DocumentBuilder getBuilder() throws XMLParserException;

    /**
     * Returns a builder to the pool.
     * 
     * @param builder the builder to return
     */
    public void returnBuilder(@Nullable DocumentBuilder builder);

    /**
     * Convenience method for creating a new document with a pooled builder.
     * 
     * @return created document
     * 
     * @throws XMLParserException thrown if there is a problem retrieving a builder
     */
    @Nonnull public Document newDocument() throws XMLParserException;

    /**
     * Convenience method for parsing an XML file using a pooled builder.
     * 
     * @param input XML to parse
     * 
     * @return parsed document
     * 
     * @throws XMLParserException thrown if there is a problem retrieving a builder, the input stream can not be read,
     *             or the XML was invalid
     */
    @Nonnull public Document parse(@Nonnull final InputStream input) throws XMLParserException;

    /**
     * Convenience method for parsing an XML file using a pooled builder.
     * 
     * @param input XML to parse
     * 
     * @return parsed document
     * 
     * @throws XMLParserException thrown if there is a problem retrieving a builder, the input stream can not be read,
     *             or the XML was invalid
     */
    @Nonnull public Document parse(@Nonnull final Reader input) throws XMLParserException;
}