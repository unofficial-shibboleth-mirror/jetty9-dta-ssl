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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

//TODO need to deal with expiring cached resources

/** A store which can be used to cache content fetched via a {@link Resource}. */
public interface ResourceCache extends DestructableComponent {

    /**
     * Determines if a cache entry exists for the resource at the given location.
     * 
     * @param location the resource location
     * 
     * @return whether a cache entry exists for a resource at that location, returns false if the given location was
     *         null or empty
     */
    public boolean contains(@Nullable String location);

    /**
     * Gets the cache entry for the resource at the given location.
     * 
     * @param location the resource location
     * 
     * @return the cache entry if one exists or null if it doesn't exist or the given location was null or empty
     */
    @Nullable public CachedResource get(@Nullable String location);

    /**
     * Adds a cache entry.
     * 
     * @param resource the cache entry
     * 
     * @return the cache entry replaced by this entry
     */
    @Nullable public CachedResource put(@Nonnull CachedResource resource);

    /**
     * Removes the given cache entry.
     * 
     * @param location the entry to remove
     * 
     * @return the removed entry or null if no entry existed for the given location
     */
    @Nullable public CachedResource remove(String location);

    /** An entry for a cached {@link Resource}. */
    public static class CachedResource extends AbstractResource implements Serializable {

        /** Serial version UID. */
        private static final long serialVersionUID = -5945597373788593911L;

        /** The instant this entry was created. */
        private final long creationInstant;

        /** The cached content of the resource. */
        private final byte[] content;

        /** Properties associated with this cache entry. */
        private final Map<String, String> properties;

        /**
         * Constructor.
         * 
         * @param resourceLocation the location of the resource
         * @param resourceContent the content of the resource
         * @param resourceProperties properties associated with the resource
         */
        public CachedResource(@Nonnull @NotEmpty final String resourceLocation,
                @Nonnull @NotLive final byte[] resourceContent,
                @Nullable @NullableElements final Map<String, String> resourceProperties) {
            creationInstant = System.currentTimeMillis();

            setLocation(Assert.isNotNull(StringSupport.trimOrNull(resourceLocation),
                    "Resource location can not be null or empty"));

            Assert.isNotNull(resourceContent, "Resource content can not be null");
            content = new byte[resourceContent.length];
            System.arraycopy(resourceContent, 0, content, 0, resourceContent.length);

            if (resourceProperties == null || resourceProperties.isEmpty()) {
                properties = Collections.emptyMap();
                return;
            }

            HashMap<String, String> checkedProperties = new HashMap<String, String>();
            String value;
            for (String key : resourceProperties.keySet()) {
                value = StringSupport.trimOrNull(resourceProperties.get(key));
                if (value != null) {
                    checkedProperties.put(key, value);
                }
            }

            if (checkedProperties.isEmpty()) {
                properties = Collections.emptyMap();
            } else {
                properties = Collections.unmodifiableMap(checkedProperties);
            }
        }

        /** {@inheritDoc} */
        protected boolean doExists() {
            return true;
        }

        /** {@inheritDoc} */
        protected InputStream doGetInputStream() {
            return new ByteArrayInputStream(content);
        }

        /** {@inheritDoc} */
        protected long doGetLastModifiedTime() {
            return creationInstant;
        }

        /**
         * Gets the properties associated with this resource.
         * 
         * @return the properties associated with this resource
         */
        @NotLive @Unmodifiable @Nonnull @NonnullElements public Map<String, String> getProperties() {
            return properties;
        }
    }
}