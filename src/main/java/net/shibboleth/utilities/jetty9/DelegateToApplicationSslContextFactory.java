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

package net.shibboleth.utilities.jetty9;

import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/** A Jetty SSL context factory that delegates X.509 trust evaluation to the application. */
public class DelegateToApplicationSslContextFactory extends org.eclipse.jetty.util.ssl.SslContextFactory {

    /**
     * Constructor.
     * 
     * Sets {@link #setWantClientAuth(boolean)} to true and {@link #setValidateCerts(boolean)} to false.
     */
    public DelegateToApplicationSslContextFactory() {
        super();
        setWantClientAuth(true);
        setValidateCerts(false);
    }

    /** {@inheritDoc} */
    protected TrustManager[] getTrustManagers(KeyStore trustStore, Collection<? extends CRL> crls) throws Exception {
        X509TrustManager noTrustManager = new X509TrustManager() {

            /** {@inheritDoc} */
            public void checkClientTrusted(X509Certificate[] certs, String auth) {
            }

            /** {@inheritDoc} */
            public void checkServerTrusted(X509Certificate[] certs, String auth) {
            }

            /** {@inheritDoc} */
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
        };

        return new TrustManager[] {noTrustManager};
    }
}