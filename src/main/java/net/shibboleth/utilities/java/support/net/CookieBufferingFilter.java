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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        
        // TODO: I don't think this is going to work, this would run post-response generation
        // so the headers should already be sent. Should be easy to tell, since no cookies will
        // get set. If so, I think we have to catch all of the potential "body writing" methods
        // in the wrapper and dump the cookies at that point from the wrapper.
        
        // Dump all the cookies set into the real response.
        for (Cookie cookie : responseProxy.getCookies().values()) {
            ((HttpServletResponse) response).addCookie(cookie);
        }
    }

}