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
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A class for building a {@link Schema} from a set of inputs, allowing for
 * manipulation of the underlying factory.
 */
@NotThreadSafe
public class SchemaBuilder {

    /** Language of the schema files. */
    public static enum SchemaLanguage {

        /** W3 XML Schema. */
        XML("xsd"),

        /** OASIS RELAX NG Schema. */
        RELAX("rng");

        /** File extension used for the schema files. */
        @Nonnull private String schemaFileExtension;

        /**
         * Constructor.
         * 
         * @param extension file extension used for the schema files
         */
        private SchemaLanguage(@Nonnull @NotEmpty String extension) {
            schemaFileExtension = Constraint.isNotNull(StringSupport.trimOrNull(extension),
                    "Extension cannot be null or empty");
        }

        /**
         * Gets the file extension used for the schema files.
         * 
         * @return file extension used for the schema files
         */
        @Nonnull @NotEmpty public String getSchemaFileExtension() {
            return schemaFileExtension;
        }
    };

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SchemaBuilder.class);
    
    /** Language of schemas. */
    @Nonnull private SchemaLanguage schemaLang;

    /** Sources of schema material compatible with JAXP. */
    @Nonnull @NonnullElements private Collection<Source> sources;
    
    /** Mechanism for resolving nested resources like included/imported schemas. */
    @Nullable private LSResourceResolver resourceResolver;
    
    /** Custom error handler. */
    @Nullable private ErrorHandler errorHandler;
    
    /** Features to set on factory. */
    @Nonnull private Map<String,Boolean> features;
    
    /** Properties to set on factory. */
    @Nonnull private Map<String,Object> properties;
    
    /** Constructor. */
    public SchemaBuilder() {
        schemaLang = SchemaLanguage.XML;
        sources = Lists.newArrayList();
        features = Maps.newHashMap();
        properties = Maps.newHashMap();
        errorHandler = new LoggingErrorHandler(log);
    }
    
    /**
     * Set the schema language.
     * 
     * @param lang  the schema language
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder setSchemaLanguage(@Nonnull final SchemaLanguage lang) {
        schemaLang = Constraint.isNotNull(lang, "SchemaLanguage cannot be null");
        return this;
    }
    
    /**
     * Set the resource resolver to use for included/imported schemas.
     * 
     * <p>If not set, the default resolver will prevent lookup of any
     * schemas not explicitly added.</p>
     * 
     * @param resolver resource resolver
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder setResourceResolver(@Nullable final LSResourceResolver resolver) {
        resourceResolver = resolver;
        return this;
    }
    
    /**
     * Set a custom error handler to use.
     * 
     * <p>If not set, a default handler will be used that logs errors before throwing exceptions.</p>
     * 
     * @param handler   error handler
     * @return this builder
     */
    @Nonnull public SchemaBuilder setErrorHandler(@Nullable final ErrorHandler handler) {
        errorHandler = handler;
        return this;
    }
    
    /**
     * Set a feature for the {@link SchemaFactory} to be built.
     * 
     * @param name The feature name, which is a non-null fully-qualified URI.
     * @param value The requested value of the feature (true or false).
     *
     * @see {@link SchemaFactory#setFeature(String, boolean)}
     */
    public void setFeature(@Nonnull @NotEmpty final String name, final boolean value) {
        features.put(name, value);
    }

    /**
     * Set the value of a property for the {@link SchemaFactory} to be built.
     * 
     * @param name The property name, which is a non-null fully-qualified URI.
     * @param object The requested value for the property.
     * 
     * @see {@link SchemaFactory#setProperty(String, Object)}
     */
    public void setProperty(@Nonnull @NotEmpty final String name, @Nullable Object object) {
        properties.put(name, object);
    }
    
    /**
     * Clear the schemas to be included.
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder resetSchemas() {
        sources.clear();
        return this;
    }
    
    /**
     * Add schemas from the given schema pathnames.
     * 
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder addSchemas(@Nonnull @NullableElements final String... schemaFilesOrDirectories) {
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema source file paths cannot be null");

        for (String file : schemaFilesOrDirectories) {
            if (file != null) {
                addSchemas(new File(file));
            }
        }

        return this;
    }

    /**
     * Add schemas from the given schema sources.
     * 
     * @param schemaFilesOrDirectories files or directories which contains schema sources
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder addSchemas(@Nonnull @NullableElements final File... schemaFilesOrDirectories) {
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema source files cannot be null");

        final Collection<File> schemaFiles = getSchemaFiles(schemaFilesOrDirectories);
        for (File schemaFile : schemaFiles) {
            if (schemaFile != null) {
                addSchemas(new StreamSource(schemaFile));
            }
        }

        return this;
    }

    /**
     * Add schemas from the given schema input streams.
     * 
     * @param schemaSources schema input streams
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder addSchemas(@Nonnull @NullableElements final InputStream... schemaSources) {
        Constraint.isNotNull(schemaSources, "Schema source input streams cannot be null");

        for (InputStream schemaSource : schemaSources) {
            if (schemaSource != null) {
                addSchemas(new StreamSource(schemaSource));
            }
        }

        return this;
    }
    
    /**
     * Add schemas from the given schema sources.
     * 
     * @param schemaSources schema sources
     * 
     * @return this builder
     */
    @Nonnull public SchemaBuilder addSchemas(@Nonnull @NullableElements final Source... schemaSources) {
        Constraint.isNotNull(schemaSources, "Schema source inputstreams can not be null");

        for (Source schemaSource : schemaSources) {
            if (schemaSource != null) {
                sources.add(schemaSource);
            }
        }

        return this;
    }

    /**
     * Build a schema from the given schema sources.
     * 
     * @return the constructed schema
     * @throws SAXException thrown if there is a problem converting the schema sources into a schema
     */
    @Nonnull public Schema buildSchema() throws SAXException {
        Constraint.isNotEmpty(sources, "No schema sources specified");

        final SchemaFactory schemaFactory;
        if (schemaLang == SchemaLanguage.XML) {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        } else {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        }
        
        for (final Map.Entry<String, Boolean> entry : features.entrySet()) {
            schemaFactory.setFeature(entry.getKey(), entry.getValue());
        }

        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            schemaFactory.setProperty(entry.getKey(), entry.getValue());
        }
        
        schemaFactory.setErrorHandler(errorHandler);
        if (resourceResolver != null) {
            schemaFactory.setResourceResolver(resourceResolver);
        }
        return schemaFactory.newSchema(sources.toArray(new Source[sources.size()]));
    }

    /**
     * Get all of the schema files in the given set of readable files, directories or subdirectories.
     * 
     * @param schemaFilesOrDirectories the sources to pull from
     * 
     * @return a collection of {@link File} objects
     */
    @Nonnull @NonnullElements private Collection<File> getSchemaFiles(
            @Nonnull @NullableElements final File... schemaFilesOrDirectories) {
        Constraint.isNotNull(schemaFilesOrDirectories, "Schema source file paths cannot be null");

        Collection<File> schemas = Lists.newArrayList();
        
        for (File handle : schemaFilesOrDirectories) {
            if (handle == null) {
                continue;
            }

            if (!handle.canRead()) {
                log.debug("Ignoring '{}', no read permission", handle.getAbsolutePath());
            }

            if (handle.isFile() && handle.getName().endsWith(schemaLang.getSchemaFileExtension())) {
                log.debug("Added schema source '{}'", handle.getAbsolutePath());
                schemas.add(handle);
            }

            if (handle.isDirectory()) {
                schemas.addAll(getSchemaFiles(handle.listFiles()));
            }
        }
        
        return schemas;
    }
    
}