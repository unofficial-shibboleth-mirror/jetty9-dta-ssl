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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.client.cache.HeapResourceFactory;

/**
 * An {@link HttpClient} builder that supports RFC 2616 caching.
 * <p>
 * This client will cache information retrieved from the remote server in memory. The backing store does
 * <strong>not</strong> perform any resource management (e.g., removing content that has nominally expired) so, special
 * care must be taken to tune the {@link #maxCacheEntries} and {@link #maxCacheEntrySize} appropriately so that the
 * system's memory is not fully consumed.
 * </p>
 */
public class InMemoryCachingHttpClientBuilder {

    /** Builder of clients used to fetch data from remote servers. */
    private final HttpClientBuilder clientBuilder;

    /** The maximum number of cached responses. Default: 50 */
    private int maxCacheEntries;

    /** The maximum response body size, in bytes, that will be eligible for caching. Default: 1048576 (1 megabyte) */
    private int maxCacheEntrySize;

    /**
     * Constructor.
     * 
     * @param builder builder of clients used to fetch data from remote servers
     */
    public InMemoryCachingHttpClientBuilder(@Nonnull final HttpClientBuilder builder) {
        clientBuilder = Constraint.isNotNull(builder, "HttpClient builder can not be null");
        maxCacheEntries = 50;
        maxCacheEntrySize = 1048576;
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
     */
    public HttpClient buildClient() {
        HttpClient client = clientBuilder.buildClient();

        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setMaxCacheEntries(maxCacheEntries);
        cacheConfig.setMaxObjectSizeBytes(maxCacheEntrySize);
        cacheConfig.setHeuristicCachingEnabled(false);
        cacheConfig.setSharedCache(false);

        return new CachingHttpClient(client, new HeapResourceFactory(), new BasicHttpCacheStorage(cacheConfig),
                cacheConfig);
    }
}