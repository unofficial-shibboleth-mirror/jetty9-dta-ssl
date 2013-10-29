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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.google.common.collect.Maps;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

/**
 * Implementation of an HTTP servlet {@link Filter} which stores the current {@link HttpServletRequest} and
 * {@link HttpServletResponse} being serviced on thread-local storage via the use of holder class
 * {@link HttpServletRequestResponseContext}.
 */
public class CookieBufferingFilter implements Filter {

    /** {@inheritDoc} */
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /** {@inheritDoc} */
    public void destroy() {
    }

    /** {@inheritDoc} */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Request is not an instance of HttpServletRequest");
        }

        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Response is not an instance of HttpServletResponse");
        }

        CookieBufferingHttpServletResponseProxy responseProxy =
                new CookieBufferingHttpServletResponseProxy((HttpServletResponse) response);
        chain.doFilter(request, responseProxy);
        
        // Dump all the cookies set into the real response if not done already.
        responseProxy.dumpCookies();
    }

    /**
     * An implementation of {@link HttpServletResponse} which buffers added cookies to
     * ensure only a single cookie of a given name is eventually set.
     */
    private class CookieBufferingHttpServletResponseProxy extends HttpServletResponseWrapper {

        /** Map of delayed cookie additions. */
        @Nonnull @NonnullElements private Map<String,Cookie> cookieMap;
        
        /**
         * Constructor.
         *
         * @param response the response to delegate to
         */
        public CookieBufferingHttpServletResponseProxy(@Nonnull final HttpServletResponse response) {
            super(response);
            cookieMap = Maps.newHashMap();
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

        /** {@inheritDoc} */
        public ServletOutputStream getOutputStream() throws IOException {
            dumpCookies();
            return super.getOutputStream();
        }

        /** {@inheritDoc} */
        public PrintWriter getWriter() throws IOException {
            dumpCookies();
            return super.getWriter();
        }

        /**
         * Transfer cookies added into the real response.
         */
        protected void dumpCookies() {
            for (Cookie cookie : cookieMap.values()) {
                ((HttpServletResponse) getResponse()).addCookie(cookie);
            }
            cookieMap.clear();
        }
    }
    
}