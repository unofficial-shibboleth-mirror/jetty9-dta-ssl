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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.StrictHostnameVerifier;

/**
 * Support class for using {@link org.apache.http.client.HttpClient} and related components.
 */
public final class HttpClientSupport {
    
    /** Constructor to prevent instantiation. */
    private HttpClientSupport() { }
    
    /**
     * Build an instance of TLS-capable {@link LayeredConnectionSocketFactory} which uses
     * the standard JSSE default {@link SSLContext} and which performs
     * strict hostname verification.
     * 
     * @return a new instance of HttpClient SSL connection socket factory
     */
    public static LayeredConnectionSocketFactory buildStrictTLSSocketFactory() {
        return new TLSSocketFactoryBuilder()
            .setHostnameVerifier(new StrictHostnameVerifier())
            .build();
    }
    
    /**
     * Build a TLS-capable instance of {@link LayeredConnectionSocketFactory} which accepts all peer certificates
     * and performs no hostname verification.
     * 
     * @return a new instance of HttpClient SSL connection socket factory
     */
    public static LayeredConnectionSocketFactory buildNoTrustTLSSocketFactory() {
        return new TLSSocketFactoryBuilder()
            .setTrustManagers(Collections.<TrustManager>singletonList(buildNoTrustX509TrustManager()))
            .setHostnameVerifier(new AllowAllHostnameVerifier())
            .build();
    }
    
    /**
     * Build an instance of {@link SSLConnectionSocketFactory} which uses
     * the standard HttpClient default {@link SSLContext} and which uses
     * a strict hostname verifier {@link SSLConnectionSocketFactory#STRICT_HOSTNAME_VERIFIER}.
     * 
     * @return a new instance of HttpClient SSL connection socket factory
     * 
     * @deprecated use instead {@link #buildStrictTLSSocketFactory()}
     */
    @Deprecated
    public static SSLConnectionSocketFactory buildStrictSSLConnectionSocketFactory() {
        return new SSLConnectionSocketFactory(
                SSLContexts.createDefault(), 
                SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER);
    }
    
     /**
     * Build an instance of {@link SSLConnectionSocketFactory} which accepts all peer certificates
     * and performs no hostname verification.
     * 
     * @return a new instance of HttpClient SSL connection socket factory
     * 
     * @deprecated use instead {@link #buildNoTrustTLSSocketFactory()}
     */
    @Deprecated
    public static SSLConnectionSocketFactory buildNoTrustSSLConnectionSocketFactory() {
        X509TrustManager noTrustManager = buildNoTrustX509TrustManager();

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {noTrustManager}, null);
            return new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("TLS SSLContext type is required to be supported by the JVM but is not", e);
        } catch (KeyManagementException e) {
            throw new RuntimeException("Some how the trust everything trust manager didn't trust everything", e);
        }
        
    }
    
    /**
     * Build an instance of {@link X509TrustManager} which trusts all certificates.
     * 
     * @return a new trust manager instance
     */
    public static X509TrustManager buildNoTrustX509TrustManager() {
        return new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // accept everything
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // accept everything
            }
        };
        
    }

}
