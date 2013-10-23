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

package net.shibboleth.utilities.java.support.httpclient;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.client.cache.FileResourceFactory;
import org.apache.http.impl.client.cache.ManagedHttpCacheStorage;

/**
 * An {@link HttpClient} builder that supports RFC 2616 caching.
 * <p>
 * Cached content is written to disk. Special care should be taken so that multiple clients do not share a single cache
 * directory unintentionally. This could result senstive data being available in ways it should not be.
 * </p>
 * 
 * <p>
 * When using the single-arg constructor variant to wrap an existing instance of
 * {@link CachingHttpClientBuilder}, there are several caveats of which to be aware:
 * 
 * <ul>
 * 
 * <li>
 * Several important non-caching-specific caveats are enumerated in this class's superclass {@link HttpClientBuilder}.
 * </li>
 * 
 * <li>
 * Instances of the following which are set as the default instance on the Apache builder will be
 * unconditionally overwritten by this builder when {@link #buildClient()} is called:
 * 
 *   <ul>
 *   <li>{@link CacheConfig}</li>
 *   </ul>
 *   
 *   <p>
 *   This is due to the unfortunate fact that the Apache builder does not currently provide accessor methods to
 *   obtain the default instances currently set on the builder.  Therefore, if you need to set any default cache
 *   config parameters which are not exposed by this builder, then you must use the Apache
 *   builder directly and may not use this builder.
 *   </p>
 * </li>
 * 
 * </ul>
 * 
 * </p>
 */
public class FileCachingHttpClientBuilder extends HttpClientBuilder {

    /**
     * Directory in which cached content will be stored. Default:
     * <code>System.getProperty("java.io.tmpdir") + File.separator + "wwwcache"</code>
     */
    private File cacheDir;

    /** The maximum number of cached responses. Default: 100 */
    private int maxCacheEntries;

    /** The maximum response body size, in bytes, that will be eligible for caching. Default: 10485760 (10 megabytes) */
    private long maxCacheEntrySize;
    
    /**
     * Constructor.
     */
    public FileCachingHttpClientBuilder() {
        this(CachingHttpClientBuilder.create());
    }
    
    /**
     * Constructor.
     * 
     * @param builder builder of clients used to fetch data from remote servers
     */
    public FileCachingHttpClientBuilder(@Nonnull final CachingHttpClientBuilder builder) {
        super(builder);
        cacheDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "wwwcache");
        maxCacheEntries = 100;
        maxCacheEntrySize = 10485760;
    }

    /**
     * Gets the directory in which cached content will be stored.
     * 
     * @return directory in which cached content will be stored
     */
    public File getCacheDirectory() {
        return cacheDir;
    }

    /**
     * Sets the directory in which cached content will be stored.
     * 
     * @param directoryPath filesystem path to the directory
     */
    public void setCacheDirectory(@Nonnull @NotEmpty String directoryPath) {
        String trimmedPath =
                Constraint.isNotNull(StringSupport.trimOrNull(directoryPath),
                        "Cache directory path can not be null or empty");
        cacheDir = new File(trimmedPath);
    }

    /**
     * Sets the directory in which cached content will be stored.
     * 
     * @param directory the directory
     */
    public void setCacheDirectory(@Nonnull File directory) {
        cacheDir = Constraint.isNotNull(directory, "Cache directory can not be null");
    }

    /**
     * Gets the maximum number of cached responses.
     * 
     * @return maximum number of cached responses
     */
    public int getMaxCacheEntries() {
        return maxCacheEntries;
    }

    /**
     * Sets the maximum number of cached responses.
     * 
     * @param maxEntries maximum number of cached responses, must be greater than zero
     */
    public void setMaxCacheEntries(int maxEntries) {
        maxCacheEntries =
                (int) Constraint.isGreaterThan(0, maxEntries, "Maximum number of cache entries must be greater than 0");
    }

    /**
     * Gets the maximum response body size, in bytes, that will be eligible for caching.
     * 
     * @return maximum response body size that will be eligible for caching
     */
    public long getMaxCacheEntrySize() {
        return maxCacheEntrySize;
    }

    /**
     * Sets the maximum response body size, in bytes, that will be eligible for caching.
     * 
     * @param size maximum response body size that will be eligible for caching, must be greater than zero
     */
    public void setMaxCacheEntrySize(long size) {
        maxCacheEntrySize = (int) Constraint.isGreaterThan(0, size, "Maximum cache entry size must be greater than 0");
    }

    /** {@inheritDoc} */
    protected void decorateApacheBuilder() throws Exception {
        super.decorateApacheBuilder();
        
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw new IOException("Unable to create cache directory " + cacheDir.getAbsolutePath());
            }
        }

        if (!cacheDir.canRead()) {
            throw new IOException("Cache directory '" + cacheDir.getAbsolutePath() + "' is not readable");
        }

        if (!cacheDir.canWrite()) {
            throw new IOException("Cache directory '" + cacheDir.getAbsolutePath() + "' is not writable");
        }
        
        CachingHttpClientBuilder cachingBuilder = (CachingHttpClientBuilder) getApacheBuilder();

        CacheConfig.Builder cacheConfigBuilder = CacheConfig.custom();
        cacheConfigBuilder.setMaxCacheEntries(maxCacheEntries);
        cacheConfigBuilder.setMaxObjectSize(maxCacheEntrySize);
        cacheConfigBuilder.setHeuristicCachingEnabled(false);
        cacheConfigBuilder.setSharedCache(false);
        CacheConfig cacheConfig = cacheConfigBuilder.build();
        
        cachingBuilder.setCacheConfig(cacheConfig);
        cachingBuilder.setResourceFactory(new FileResourceFactory(cacheDir));
        cachingBuilder.setHttpCacheStorage(new ManagedHttpCacheStorage(cacheConfig));
    }
}