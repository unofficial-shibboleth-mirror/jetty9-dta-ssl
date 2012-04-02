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

package net.shibboleth.utilities.java.support.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/** A resource which reads data from the classpath. */
public class ClasspathResource extends AbstractResource {

    /** URL to the classpath resource. */
    private final URL classpathResource;

    /**
     * Constructor. ClassLoader used to locate the resource is the loader used to load this class. Default system
     * character set is used as the resource character set.
     * 
     * @param resourcePath classpath path to the resource
     */
    public ClasspathResource(final String resourcePath) {
        this(resourcePath, ClasspathResource.class.getClassLoader());
    }

    /**
     * Constructor.
     * 
     * @param resourcePath classpath path to the resource
     * @param classLoader class loader used to locate the resource
     */
    public ClasspathResource(final String resourcePath, final ClassLoader classLoader) {
        String myLocation =
                Constraint.isNotNull(StringSupport.trimOrNull(resourcePath), "Resource path may not be null or empty");
        setLocation(myLocation);

        Constraint.isNotNull(classLoader, "Resource class loader may not be null");
        classpathResource =
                Constraint.isNotNull(classLoader.getResource(myLocation), "Resource " + resourcePath
                        + " does not exist on the classpath");
    }

    /** {@inheritDoc} */
    protected boolean doExists() throws ResourceException {
        return true;
    }

    /** {@inheritDoc} */
    protected InputStream doGetInputStream() throws ResourceException {
        try {
            return classpathResource.openStream();
        } catch (IOException e) {
            throw new ResourceException("Resource " + getLocation() + " can not be read", e);
        }
    }

    /** {@inheritDoc} */
    protected long doGetLastModifiedTime() throws ResourceException {
        return 0;
    }
}