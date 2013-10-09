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

package net.shibboleth.utilities.java.support.net;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

/**
 * An implementation of {@link HttpServletResponse} which buffers added cookies to
 * ensure only a single cookie of a given name is eventually set.
 */
public class CookieBufferingHttpServletResponseProxy extends HttpServletResponseWrapper {

    /** Map of delayed cookie additions. */
    @Nonnull @NonnullElements private Map<String,Cookie> cookieMap;
    
    /**
     * Constructor.
     *
     * @param response the response to delegate to
     */
    public CookieBufferingHttpServletResponseProxy(@Nonnull final HttpServletResponse response) {
        super(response);
    }

    /** {@inheritDoc} */
    public void addCookie(Cookie cookie) {
        // Guarantees any existing cookie by this name is replaced.
        cookieMap.put(cookie.getName(), cookie);
    }

    /**
     * Get the map of cookies that will be set.
     * 
     * @return map of cookies to be set
     */
    @Nonnull @NonnullElements @Live protected Map<String,Cookie> getCookies() {
        return cookieMap;
    }
    
}