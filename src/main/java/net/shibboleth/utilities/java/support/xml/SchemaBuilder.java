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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/** A helper class for building {@link Schema} from a set of input. */
public final class SchemaBuilder {

    /** Language of the schema files. */
    public static enum SchemaLanguage {

        /** W3 XML Schema. */
        XML("xsd"),

        /** OASIS RELAX NG Schema. */
        RELAX("rng");

        /** File extension used for the schema files. */
        private String schemaFileExtension;

        /**
         * Constructor.
         * 
         * @param extension file extension used for the schema files
         */
        private SchemaLanguage(String extension) {
            schemaFileExtension = extension;
        }

        /**
         * Gets the file extension used for the schema files.
         * 
         * @return file extension used for the schema files
         */
        public String getSchemaFileExtension() {
            return schemaFileExtension;
        }
    };

    /** Class logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SchemaBuilder.class);

    /** Constructor. */
    private SchemaBuilder() {
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    @Nonnull public static Schema buildSchema(@Nonnull final SchemaLanguage lang,
            @Nonnull @NotEmpty @NullableElements final String... schemaFilesOrDirectories) throws SAXException {
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema source files paths can not be null");

        ArrayList<File> sourceFiles = new ArrayList<File>();
        for (String file : schemaFilesOrDirectories) {
            if (file != null) {
                sourceFiles.add(new File(file));
            }
        }

        return buildSchema(lang, sourceFiles.toArray(new File[sourceFiles.size()]));
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language, must not be null
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    @Nonnull public static Schema buildSchema(@Nonnull final SchemaLanguage lang,
            @Nonnull @NotEmpty @NullableElements final File... schemaFilesOrDirectories) throws SAXException {
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema source files can not be null");

        final ArrayList<File> schemaFiles = new ArrayList<File>();
        getSchemaFiles(lang, schemaFiles, schemaFilesOrDirectories);

        final ArrayList<Source> schemaSources = new ArrayList<Source>();
        for (File schemaFile : schemaFiles) {
            if (schemaFile != null) {
                schemaSources.add(new StreamSource(schemaFile));
            }
        }

        return buildSchema(lang, schemaSources);
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language
     * @param schemaSources schema source resources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    @Nonnull public static Schema buildSchema(@Nonnull final SchemaLanguage lang,
            @Nonnull @NotEmpty @NullableElements final Resource... schemaSources) throws SAXException {
        Constraint.isNotNull(schemaSources, "Schema source resources can not be null");

        final ArrayList<Source> sourceStreams = new ArrayList<Source>();
        for (Resource schemaSource : schemaSources) {
            try {
                if (schemaSource != null) {
                    sourceStreams.add(new StreamSource(schemaSource.getInputStream(), schemaSource.getLocation()));
                }
            } catch (ResourceException e) {
                throw new SAXException("Unable to read schema resource " + schemaSource.getLocation(), e);
            }
        }

        return buildSchema(lang, sourceStreams);
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language
     * @param schemaSources schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    @Nonnull public static Schema buildSchema(@Nonnull final SchemaLanguage lang,
            @Nonnull @NotEmpty @NullableElements final InputStream... schemaSources) throws SAXException {
        Constraint.isNotNull(schemaSources, "Schema source inputstreams can not be null");

        final ArrayList<StreamSource> sources = new ArrayList<StreamSource>();
        for (InputStream schemaSource : schemaSources) {
            if (schemaSource != null) {
                sources.add(new StreamSource(schemaSource));
            }
        }

        return buildSchema(lang, sources);
    }

    /**
     * Gets all of the schema files in the given set of readable files, directories or subdirectories.
     * 
     * @param lang schema language
     * @param schemaFilesOrDirectories files and directories which may contain schema files
     * @param accumulatedSchemaFiles list that accumulates the schema files
     */
    private static void getSchemaFiles(@Nonnull final SchemaLanguage lang,
            @Nonnull final List<File> accumulatedSchemaFiles, @Nonnull final File... schemaFilesOrDirectories) {
        Constraint.isNotNull(lang, "Schema language identifier can not be null");
        Constraint.isNotNull(accumulatedSchemaFiles, "Accumulated schema file collection can not be null");
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema file or directory can not be null");

        for (File handle : schemaFilesOrDirectories) {
            if (handle == null) {
                continue;
            }

            if (!handle.canRead()) {
                LOG.debug("Ignoring '{}', no read permission", handle.getAbsolutePath());
            }

            if (handle.isFile() && handle.getName().endsWith(lang.getSchemaFileExtension())) {
                LOG.debug("Added schema source '{}'", handle.getAbsolutePath());
                accumulatedSchemaFiles.add(handle);
            }

            if (handle.isDirectory()) {
                getSchemaFiles(lang, accumulatedSchemaFiles, handle.listFiles());
            }
        }
    }

    /**
     * Builds a schema from the given schema sources.
     * 
     * @param lang schema language
     * @param schemaSources schema sources
     * 
     * @return the constructed schema
     * 
     * @throws SAXException thrown if there is a problem converting the schema sources in to a schema
     */
    @Nonnull private static Schema buildSchema(@Nonnull final SchemaLanguage lang,
            @Nonnull @NotEmpty @NullableElements final List<? extends Source> schemaSources) throws SAXException {
        Constraint.isNotNull(lang, "Schema language identifier can not be null");

        Constraint.isNotNull(schemaSources, "Schema source files can not be null");
        ArrayList<Source> sources = new ArrayList<Source>();
        for (Source source : schemaSources) {
            if (source != null) {
                sources.add(source);
            }
        }
        Constraint.isNotEmpty(sources, "No schema source specified");

        final SchemaFactory schemaFactory;
        if (lang == SchemaLanguage.XML) {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        } else {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        }

        schemaFactory.setErrorHandler(new LoggingErrorHandler(LoggerFactory.getLogger(SchemaBuilder.class)));
        return schemaFactory.newSchema(sources.toArray(new Source[sources.size()]));
    }
}