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

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.protocol.HttpContext;

/**
 * Adds a <code>Connection: close</code> to all HTTP/1.1 requests.
 * 
 * <p>
 * This interceptor essentially disables connection keep-alive support and, by virtue of the server closing the
 * connection, prevents a {@link org.apache.http.conn.ClientConnectionManager} from holding open and reusing
 * connections. If you'd like to allow the {@link org.apache.http.conn.ClientConnectionManager} to hold open connections
 * for a while and potentially reuse them consider using the {@link IdleConnectionSweeper} to limit the amount of time
 * the connections are held open.
 * </p>
 */
public class RequestConnectionClose implements HttpRequestInterceptor {

    /** {@inheritDoc} */
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (HttpVersion.HTTP_1_1.equals(request.getProtocolVersion())) {
            request.addHeader(HttpHeaders.CONNECTION, "close");
        }
    }
}