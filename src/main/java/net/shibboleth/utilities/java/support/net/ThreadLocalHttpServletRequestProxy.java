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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * An implementation of {@link HttpServletRequest} which serves as a proxy for the 
 * current thread-local servlet request obtained from {@link HttpServletRequestResponseContext}.
 */
public class ThreadLocalHttpServletRequestProxy implements HttpServletRequest {

    /** {@inheritDoc} */
    public Object getAttribute(String name) {
        return getCurrent().getAttribute(name);
    }

    /** {@inheritDoc} */
    public Enumeration getAttributeNames() {
        return getCurrent().getAttributeNames();
    }

    /** {@inheritDoc} */
    public String getCharacterEncoding() {
        return getCurrent().getCharacterEncoding();
    }

    /** {@inheritDoc} */
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        getCurrent().setCharacterEncoding(env);
    }

    /** {@inheritDoc} */
    public int getContentLength() {
        return getCurrent().getContentLength();
    }

    /** {@inheritDoc} */
    public String getContentType() {
        return getCurrent().getContentType();
    }

    /** {@inheritDoc} */
    public ServletInputStream getInputStream() throws IOException {
        return getCurrent().getInputStream();
    }

    /** {@inheritDoc} */
    public String getParameter(String name) {
        return getCurrent().getParameter(name);
    }

    /** {@inheritDoc} */
    public Enumeration getParameterNames() {
        return getCurrent().getParameterNames();
    }

    /** {@inheritDoc} */
    public String[] getParameterValues(String name) {
        return getCurrent().getParameterValues(name);
    }

    /** {@inheritDoc} */
    public Map getParameterMap() {
        return getCurrent().getParameterMap();
    }

    /** {@inheritDoc} */
    public String getProtocol() {
        return getCurrent().getProtocol();
    }

    /** {@inheritDoc} */
    public String getScheme() {
        return getCurrent().getScheme();
    }

    /** {@inheritDoc} */
    public String getServerName() {
        return getCurrent().getServerName();
    }

    /** {@inheritDoc} */
    public int getServerPort() {
        return getCurrent().getServerPort();
    }

    /** {@inheritDoc} */
    public BufferedReader getReader() throws IOException {
        return getCurrent().getReader();
    }

    /** {@inheritDoc} */
    public String getRemoteAddr() {
        return getCurrent().getRemoteAddr();
    }

    /** {@inheritDoc} */
    public String getRemoteHost() {
        return getCurrent().getRemoteHost();
    }

    /** {@inheritDoc} */
    public void setAttribute(String name, Object o) {
        getCurrent().setAttribute(name, o);
    }

    /** {@inheritDoc} */
    public void removeAttribute(String name) {
        getCurrent().removeAttribute(name);
    }

    /** {@inheritDoc} */
    public Locale getLocale() {
        return getCurrent().getLocale();
    }

    /** {@inheritDoc} */
    public Enumeration getLocales() {
        return getCurrent().getLocales();
    }

    /** {@inheritDoc} */
    public boolean isSecure() {
        return getCurrent().isSecure();
    }

    /** {@inheritDoc} */
    public RequestDispatcher getRequestDispatcher(String path) {
        return getCurrent().getRequestDispatcher(path);
    }

    /** {@inheritDoc} */
    public String getRealPath(String path) {
        return getCurrent().getRealPath(path);
    }

    /** {@inheritDoc} */
    public int getRemotePort() {
        return getCurrent().getRemotePort();
    }

    /** {@inheritDoc} */
    public String getLocalName() {
        return getCurrent().getLocalName();
    }

    /** {@inheritDoc} */
    public String getLocalAddr() {
        return getCurrent().getLocalAddr();
    }

    /** {@inheritDoc} */
    public int getLocalPort() {
        return getCurrent().getLocalPort();
    }

    /** {@inheritDoc} */
    public String getAuthType() {
        return getCurrent().getAuthType();
    }

    /** {@inheritDoc} */
    public Cookie[] getCookies() {
        return getCurrent().getCookies();
    }

    /** {@inheritDoc} */
    public long getDateHeader(String name) {
        return getCurrent().getDateHeader(name);
    }

    /** {@inheritDoc} */
    public String getHeader(String name) {
        return getCurrent().getHeader(name);
    }

    /** {@inheritDoc} */
    public Enumeration getHeaders(String name) {
        return getCurrent().getHeaders(name);
    }

    /** {@inheritDoc} */
    public Enumeration getHeaderNames() {
        return getCurrent().getHeaderNames();
    }

    /** {@inheritDoc} */
    public int getIntHeader(String name) {
        return getCurrent().getIntHeader(name);
    }

    /** {@inheritDoc} */
    public String getMethod() {
        return getCurrent().getMethod();
    }

    /** {@inheritDoc} */
    public String getPathInfo() {
        return getCurrent().getPathInfo();
    }

    /** {@inheritDoc} */
    public String getPathTranslated() {
        return getCurrent().getPathTranslated();
    }

    /** {@inheritDoc} */
    public String getContextPath() {
        return getCurrent().getContextPath();
    }

    /** {@inheritDoc} */
    public String getQueryString() {
        return getCurrent().getQueryString();
    }

    /** {@inheritDoc} */
    public String getRemoteUser() {
        return getCurrent().getRemoteUser();
    }

    /** {@inheritDoc} */
    public boolean isUserInRole(String role) {
        return getCurrent().isUserInRole(role);
    }

    /** {@inheritDoc} */
    public Principal getUserPrincipal() {
        return getCurrent().getUserPrincipal();
    }

    /** {@inheritDoc} */
    public String getRequestedSessionId() {
        return getCurrent().getRequestedSessionId();
    }

    /** {@inheritDoc} */
    public String getRequestURI() {
        return getCurrent().getRequestURI();
    }

    /** {@inheritDoc} */
    public StringBuffer getRequestURL() {
        return getCurrent().getRequestURL();
    }

    /** {@inheritDoc} */
    public String getServletPath() {
        return getCurrent().getServletPath();
    }

    /** {@inheritDoc} */
    public HttpSession getSession(boolean create) {
        return getCurrent().getSession(create);
    }

    /** {@inheritDoc} */
    public HttpSession getSession() {
        return getCurrent().getSession();
    }

    /** {@inheritDoc} */
    public boolean isRequestedSessionIdValid() {
        return getCurrent().isRequestedSessionIdValid();
    }

    /** {@inheritDoc} */
    public boolean isRequestedSessionIdFromCookie() {
        return getCurrent().isRequestedSessionIdFromCookie();
    }

    /** {@inheritDoc} */
    public boolean isRequestedSessionIdFromURL() {
        return getCurrent().isRequestedSessionIdFromURL();
    }

    /** {@inheritDoc} */
    public boolean isRequestedSessionIdFromUrl() {
        return getCurrent().isRequestedSessionIdFromUrl();
    }
    
    /**
     * Get the current HttpServletRequest from ThreadLocal storage.
     * 
     * @return the current request
     */
    protected HttpServletRequest getCurrent() {
        return Constraint.isNotNull(HttpServletRequestResponseContext.getRequest(), 
                "Current HttpServletRequest has not been loaded via HttpServletRequestResponseContext");
    }

}
