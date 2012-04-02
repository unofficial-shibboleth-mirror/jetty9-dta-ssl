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
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.client.cache.FileResourceFactory;
import org.apache.http.impl.client.cache.ManagedHttpCacheStorage;

/**
 * An {@link HttpClient} builder that supports RFC 2616 caching.
 * <p>
 * Cached content is written to disk. Special care should be taken so that multiple clients do not share a single cache
 * directory unintentionally. This could result senstive data being available in ways it should not be.
 * </p>
 */
public class FileCachingHttpClientBuilder {

    /** Builder of clients used to fetch data from remote servers. */
    private final HttpClientBuilder clientBuilder;

    /**
     * Directory in which cached content will be stored. Default:
     * <code>System.getProperty("java.io.tmpdir") + File.separator + "wwwcache"</code>
     */
    private File cacheDir;

    /** The maximum number of cached responses. Default: 100 */
    private int maxCacheEntries;

    /** The maximum response body size, in bytes, that will be eligible for caching. Default: 10485760 (10 megabytes) */
    private int maxCacheEntrySize;

    /**
     * Constructor.
     * 
     * @param builder builder of clients used to fetch data from remote servers
     */
    public FileCachingHttpClientBuilder(@Nonnull final HttpClientBuilder builder) {
        clientBuilder = Constraint.isNotNull(builder, "HttpClient builder can not be null");
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
    public int getMaxCacheEntrySize() {
        return maxCacheEntrySize;
    }

    /**
     * Sets the maximum response body size, in bytes, that will be eligible for caching.
     * 
     * @param size maximum response body size that will be eligible for caching, must be greater than zero
     */
    public void setMaxCacheEntrySize(int size) {
        maxCacheEntrySize = (int) Constraint.isGreaterThan(0, size, "Maximum cache entry size must be greater than 0");
    }

    /**
     * Builds an HTTP client that performs RFC2616 caching.
     * 
     * @return the constructed HTTP client
     * 
     * @throws IOException if the cache directory does not exist and can not be created or if it can not be read from or
     *             written to
     */
    public HttpClient buildClient() throws IOException {
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

        HttpClient client = clientBuilder.buildClient();

        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setMaxCacheEntries(maxCacheEntries);
        cacheConfig.setMaxObjectSizeBytes(maxCacheEntrySize);
        cacheConfig.setHeuristicCachingEnabled(false);
        cacheConfig.setSharedCache(false);

        return new CachingHttpClient(client, new FileResourceFactory(cacheDir),
                new ManagedHttpCacheStorage(cacheConfig), cacheConfig);
    }
}