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

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.net.MediaType;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

/** Utilities for working with HTTP Servlet requests and responses. */
@Beta
public final class HttpServletSupport {
    
    /** Function to strip MediaType parameters. */
    private static final Function<MediaType, MediaType> STRIP_PARAMS = new StripMediaTypeParametersFunction();

    /** Constructor. */
    private HttpServletSupport() {
    }

    /**
     * Adds Cache-Control and Pragma headers meant to disable caching.
     * 
     * @param response transport to add headers to
     */
    public static void addNoCacheHeaders(final HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * Sets the character encoding of the transport to UTF-8.
     * 
     * @param response transport to set character encoding type
     */
    public static void setUTF8Encoding(final HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
    }

    /**
     * Sets the MIME content type of the response.
     * 
     * @param response the transport to set content type on
     * @param contentType the content type to set
     */
    public static void setContentType(final HttpServletResponse response, final String contentType) {
        response.setHeader("Content-Type", contentType);
    }

    /**
     * Gets the request URI as returned by {@link HttpServletRequest#getRequestURI()} but without the servlet context
     * path.
     * 
     * @param request request to get the URI from
     * 
     * @return constructed URI
     */
    public static String getRequestPathWithoutContext(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();

        if (request.getPathInfo() == null) {
            return servletPath;
        } else {
            return servletPath + request.getPathInfo();
        }
    }

    /**
     * Gets the URL that was requested to generate this request. This includes the scheme, host, port, path, and query
     * string.
     * 
     * @param request current request
     * 
     * @return URL that was requested to generate this request
     */
    public static URI getFullRequestURI(final HttpServletRequest request) {
        final StringBuffer requestUrl = request.getRequestURL();

        final String encodedQuery = StringSupport.trimOrNull(request.getQueryString());
        if (encodedQuery != null) {
            requestUrl.append("?").append(encodedQuery);
        }

        return URI.create(requestUrl.toString());
    }
    
    /**
     * Validate the Content-Type of the specified request.
     * 
     * <p>
     * 2 strategies are supported for evaluating the request's parsed content type:
     * <ol>
     * <li>
     * If isOneOfStrategy is true, then the {@link MediaType} parsed from the request is compared to each 
     * of the specified valid types via {@link MediaType#is(MediaType)}. If any pass, the type is considered
     * valid.  This allows use of MediaType's support for wildcard and parameter evaluation.
     * </li>
     * <li>
     * If isOneOfStrategy is false, then the {@link MediaType} parsed from the request is stripped 
     * of its parameters, as is each of the valid types.  Then a simple evaluation is done that the 
     * request type is equal to one of the passed types. In this case, only literal types and subtypes 
     * should be passed as valid types; wildcards should not be used.
     * </li>
     * </ol>
     * </p>
     * 
     * @param request the request to be validated
     * @param validTypes the set of valid media types
     * @param noContentTypeIsValid flag whether the case of a missing/empty Content-Type header is considered valid
     * @param isOneOfStrategy flag for the strategy used in the validation (see above for details)
     * @return true if the content type is valid, false if not
     */
    public static boolean validateContentType(final HttpServletRequest request, final Set<MediaType> validTypes, 
            final boolean noContentTypeIsValid, final boolean isOneOfStrategy) {
        
        final String contentType = StringSupport.trimOrNull(request.getContentType());
        if (contentType != null) {
            if (isOneOfStrategy) {
                final MediaType mediaType = MediaType.parse(contentType);
                for (final MediaType validType : validTypes) {
                    if (mediaType.is(validType)) {
                        return true;
                    }
                }
                return false;
            } else {
                final MediaType mediaType = MediaType.parse(contentType).withoutParameters();
                final Set<MediaType> validTypesWithoutParameters = new HashSet<>();
                validTypesWithoutParameters.addAll(Collections2.filter(
                        Collections2.transform(validTypes, STRIP_PARAMS), 
                        Predicates.notNull()));
                return validTypesWithoutParameters.contains(mediaType);
            }
        } else {
            return noContentTypeIsValid;
        }
        
    }
    
}