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

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.CharsetUtils;

//TODO retry attempts, keep alive strategy

/**
 * Builder used to construct {@link HttpClient} objects configured with particular settings.
 * 
 * <p>
 * When using the single-arg constructor variant to wrap an existing instance of
 * {@link org.apache.http.impl.client.HttpClientBuilder}, there are several caveats of which to be aware:
 * 
 * <ul>
 * 
 * <li>
 * Instances of the following which are set as the default instance on the Apache builder will be unconditionally
 * overwritten by this builder when {@link #buildClient()} is called:
 * 
 * <ul>
 * <li>{@link RequestConfig}</li>
 * <li>{@link ConnectionConfig}</li>
 * </ul>
 * 
 * <p>
 * This is due to the unfortunate fact that the Apache builder does not currently provide accessor methods to obtain the
 * default instances currently set on the builder. Therefore, if you need to set any default request or connection
 * config parameters which are not exposed by this builder, then you must use the Apache builder directly and may not
 * use this builder.
 * </p>
 * </li>
 * 
 * <li>
 * If this builder's <code>connectionDisregardSslCertificate</code> is set to <code>true</code>, then any value
 * previously set via the Apache builder's
 * {@link org.apache.http.impl.client.HttpClientBuilder#setSSLSocketFactory(org.apache.http.conn.socket.LayeredConnectionSocketFactory)} will be
 * unconditionally overwritten.</li>
 * 
 * <li>
 * If this builder is supplied with a <code>connectionProxyHost</code>, <code>connectionProxyUsername</code> and
 * <code>connectionProxyPassword</code>, then any value previously set via the Apache builder's
 * {@link org.apache.http.impl.client.HttpClientBuilder#setDefaultCredentialsProvider(CredentialsProvider)} will be
 * unconditionally overwritten.</li>
 * 
 * <li>
 * Per the Apache builder's Javadoc, if a non-null instance of {@link org.apache.http.conn.HttpClientConnectionManager}
 * is set on the Apache builder via
 * {@link org.apache.http.impl.client.HttpClientBuilder#setConnectionManager(org.apache.http.conn.HttpClientConnectionManager)}
 * , this supersedes various other properties set on the Apache builder. This includes the following
 * instances/properties on the Apache builder:
 * 
 * <ul>
 * <li><code>SSLSocketFactory</code> ({@link org.apache.http.conn.socket.LayeredConnectionSocketFactory})</li>
 * <li>{@link javax.net.ssl.SSLContext}</li>
 * <li>{@link org.apache.http.conn.ssl.X509HostnameVerifier}</li>
 * <li>{@link org.apache.http.config.SocketConfig}</li>
 * <li>{@link ConnectionConfig}</li>
 * <li><code>maxConnTotal</code></li>
 * <li><code>maxConnPerRoute</code></li>
 * </ul>
 * 
 * <p>
 * Similarly, the following setters on this builder will become ineffective when a non-null connection manger is set on
 * the Apache builder:
 * </p>
 * 
 * <ul>
 * <li>{@link #setConnectionDisregardSslCertificate(boolean)}</li>
 * <li>{@link #setSocketBufferSize(int)}</li>
 * <li>{@link #setHttpContentCharSet(String)}</li>
 * </ul>
 * 
 * <p>
 * Therefore, if you need to explicitly supply a connection manager instance to the Apache builder (for example in order
 * to be able to use {@link IdleConnectionSweeper}), then you must supply these properties or instances directly to the
 * connection manager rather than to this builder or the Apache builder.
 * </p>
 * </li>
 * 
 * <li>
 * Similar to the above issue, setting an explicit <code>SSLSocketFactory</code> on the Apache builder will supersede
 * the following Apache builder properties:
 * 
 * <ul>
 * <li>{@link javax.net.ssl.SSLContext}</li>
 * <li>{@link org.apache.http.conn.ssl.X509HostnameVerifier}</li>
 * </ul>
 * </li>
 * 
 * </ul>
 * 
 * </p>
 * 
 * */
public class HttpClientBuilder {

    /** Local IP address used when establishing connections. Default value: system default local address */
    private InetAddress socketLocalAddress;

    /**
     * Maximum period inactivity between two consecutive data packets in milliseconds. Default value: 5000 (5 seconds)
     */
    private int socketTimeout;

    /** Socket buffer size in bytes. Default size is 8192 bytes. */
    private int socketBufferSize;

    /**
     * Maximum length of time in milliseconds to wait for the connection to be established. Default value: 5000 (5
     * seconds)
     */
    private int connectionTimeout;

    /** Whether the SSL certificates used by the responder should be ignored. Default value: false */
    private boolean connectionDisregardSslCertificate;

    /** Whether to instruct the server to close the connection after it has sent its response. Default value: true */
    private boolean connectionCloseAfterResponse;

    /**
     * Whether to check a connection for staleness before using. This can be an expensive operation. Default value:
     * false
     */
    private boolean connectionStalecheck;

    /** Host name of the HTTP proxy server through which connections will be made. Default value: null. */
    private String connectionProxyHost;

    /** Port number of the HTTP proxy server through which connections will be made. Default value: 8080. */
    private int connectionProxyPort;

    /** Username used to connect to the HTTP proxy server. Default value: null. */
    private String connectionProxyUsername;

    /** Password used to connect to the HTTP proxy server. Default value: null. */
    private String connectionProxyPassword;

    /** Whether to follow HTTP redirects. Default value: true */
    private boolean httpFollowRedirects;

    /** Character set used for HTTP entity content. Default value: UTF-8 */
    private String httpContentCharSet;

    /** The Apache HttpClientBuilder 4.3+ instance over which to layer this builder. */
    private org.apache.http.impl.client.HttpClientBuilder apacheBuilder;

    /** Constructor. */
    public HttpClientBuilder() {
        this(org.apache.http.impl.client.HttpClientBuilder.create());
    }

    /**
     * Constructor.
     * 
     * @param builder the Apache HttpClientBuilder 4.3+ instance over which to layer this builder
     */
    public HttpClientBuilder(@Nonnull org.apache.http.impl.client.HttpClientBuilder builder) {
        Constraint.isNotNull(builder, "Apache HttpClientBuilder may not be null");
        apacheBuilder = builder;
        resetDefaults();
    }

    /** Resets all builder parameters to their defaults. */
    public void resetDefaults() {
        socketLocalAddress = null;
        socketTimeout = 5000;
        socketBufferSize = 8192;
        connectionTimeout = 5000;
        connectionDisregardSslCertificate = false;
        connectionCloseAfterResponse = true;
        connectionStalecheck = false;
        connectionProxyHost = null;
        connectionProxyPort = 8080;
        connectionProxyUsername = null;
        connectionProxyPassword = null;
        httpFollowRedirects = true;
        httpContentCharSet = "UTF-8";
    }

    /**
     * Gets the local IP address used when making requests.
     * 
     * @return local IP address used when making requests
     */
    public InetAddress getSocketLocalAddress() {
        return socketLocalAddress;
    }

    /**
     * Sets the local IP address used when making requests.
     * 
     * @param address local IP address used when making requests
     */
    public void setSocketLocalAddress(final InetAddress address) {
        socketLocalAddress = address;
    }

    /**
     * Sets the local IP address used when making requests.
     * 
     * @param ipOrHost IP address or hostname, never null
     * 
     * @throws UnknownHostException thrown if the given IP or hostname can not be resolved
     */
    public void setSocketLocalAddress(final String ipOrHost) throws UnknownHostException {
        socketLocalAddress = InetAddress.getByName(Constraint.isNotNull(ipOrHost, "IP or hostname may not be null"));
    }

    /**
     * Gets the maximum period inactivity between two consecutive data packets in milliseconds. A value of less than 1
     * indicates no timeout.
     * 
     * @return maximum period inactivity between two consecutive data packets in milliseconds
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Sets the maximum period inactivity between two consecutive data packets in milliseconds. A value of less than 1
     * indicates no timeout.
     * 
     * @param timeout maximum period inactivity between two consecutive data packets in milliseconds
     */
    public void setSocketTimeout(final int timeout) {
        this.socketTimeout = timeout;
    }

    /**
     * Gets the size of the socket buffer, in bytes, used for request/response buffering.
     * 
     * @return size of the socket buffer, in bytes, used for request/response buffering
     */
    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    /**
     * Sets size of the socket buffer, in bytes, used for request/response buffering.
     * 
     * @param size size of the socket buffer, in bytes, used for request/response buffering; must be greater than 0
     */
    public void setSocketBufferSize(final int size) {
        socketBufferSize = (int) Constraint.isGreaterThan(0, size, "Socket buffer size must be greater than 0");
    }

    /**
     * Gets the maximum length of time in milliseconds to wait for the connection to be established. A value of less
     * than 1 indicates no timeout.
     * 
     * @return maximum length of time in milliseconds to wait for the connection to be established
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Sets the maximum length of time in milliseconds to wait for the connection to be established. A value of less
     * than 1 indicates no timeout.
     * 
     * @param timeout maximum length of time in milliseconds to wait for the connection to be established
     */
    public void setConnectionTimeout(final int timeout) {
        connectionTimeout = timeout;
    }

    /**
     * Gets whether the responder's SSL certificate should be ignored.
     * 
     * @return whether the responder's SSL certificate should be ignored
     */
    public boolean isConnectionDisregardSslCertificate() {
        return connectionDisregardSslCertificate;
    }

    /**
     * Sets whether the responder's SSL certificate should be ignored.
     * 
     * @param disregard whether the responder's SSL certificate should be ignored
     */
    public void setConnectionDisregardSslCertificate(final boolean disregard) {
        connectionDisregardSslCertificate = disregard;
    }

    /**
     * Gets whether to instruct the server to close the connection after it has sent its response.
     * 
     * @return whether to instruct the server to close the connection after it has sent its response
     */
    public boolean isConnectionCloseAfterResponse() {
        return connectionCloseAfterResponse;
    }

    /**
     * Sets whether to instruct the server to close the connection after it has sent its response.
     * 
     * @param close whether to instruct the server to close the connection after it has sent its response
     */
    public void setConnectionCloseAfterResponse(boolean close) {
        connectionCloseAfterResponse = close;
    }

    /**
     * Gets whether reused connections are checked if they are closed before being used by the client.
     * 
     * @return whether reused connections are checked if they are closed before being used by the client
     */
    public boolean isConnectionStalecheck() {
        return connectionStalecheck;
    }

    /**
     * Sets whether reused connections are checked if they are closed before being used by the client. Checking can take
     * up to 30ms (per request). If checking is turned off an I/O error occurs if the connection is used request. This
     * should be enabled uncles the code using the client explicitly handles the error case and retries connection as
     * appropriate.
     * 
     * @param check whether reused connections are checked if they are closed before being used by the client
     */
    public void setConnectionStalecheck(final boolean check) {
        connectionStalecheck = check;
    }

    /**
     * Gets the hostname of the default proxy used when making connection. A null indicates no default proxy.
     * 
     * @return hostname of the default proxy used when making connection
     */
    public String getConnectionProxyHost() {
        return connectionProxyHost;
    }

    /**
     * Sets the hostname of the default proxy used when making connection. A null indicates no default proxy.
     * 
     * @param host hostname of the default proxy used when making connection
     */
    public void setConnectionProxyHost(final String host) {
        connectionProxyHost = StringSupport.trimOrNull(host);
    }

    /**
     * Gets the port of the default proxy used when making connection.
     * 
     * @return port of the default proxy used when making connection
     */
    public int getConnectionProxyPort() {
        return connectionProxyPort;
    }

    /**
     * Sets the port of the default proxy used when making connection.
     * 
     * @param port port of the default proxy used when making connection; must be greater than 0 and less than 65536
     */
    public void setConnectionProxyPort(final int port) {
        connectionProxyPort =
                (int) Constraint.numberInRangeExclusive(0, 65536, port,
                        "Proxy port must be between 0 and 65536, exclusive");
    }

    /**
     * Gets the username to use when authenticating to the proxy.
     * 
     * @return username to use when authenticating to the proxy
     */
    public String getConnectionProxyUsername() {
        return connectionProxyUsername;
    }

    /**
     * Sets the username to use when authenticating to the proxy.
     * 
     * @param usename username to use when authenticating to the proxy; may be null
     */
    public void setConnectionProxyUsername(final String usename) {
        connectionProxyUsername = usename;
    }

    /**
     * Gets the password used when authenticating to the proxy.
     * 
     * @return password used when authenticating to the proxy
     */
    public String getConnectionProxyPassword() {
        return connectionProxyPassword;
    }

    /**
     * Sets the password used when authenticating to the proxy.
     * 
     * @param password password used when authenticating to the proxy; may be null
     */
    public void setConnectionProxyPassword(final String password) {
        connectionProxyPassword = password;
    }

    /**
     * Gets whether HTTP redirects will be followed.
     * 
     * @return whether HTTP redirects will be followed
     */
    public boolean isHttpFollowRedirects() {
        return httpFollowRedirects;
    }

    /**
     * Gets whether HTTP redirects will be followed.
     * 
     * @param followRedirects true if redirects are followed, false otherwise
     */
    public void setHttpFollowRedirects(final boolean followRedirects) {
        httpFollowRedirects = followRedirects;
    }

    /**
     * Gets the character set used with the HTTP entity (body).
     * 
     * @return character set used with the HTTP entity (body)
     */
    public String getHttpContentCharSet() {
        return httpContentCharSet;
    }

    /**
     * Sets the character set used with the HTTP entity (body).
     * 
     * @param charSet character set used with the HTTP entity (body)
     */
    public void setHttpContentCharSet(final String charSet) {
        httpContentCharSet = charSet;
    }

    /**
     * Constructs an {@link HttpClient} using the settings of this builder.
     * 
     * @return the constructed client
     * 
     * @throws Exception if there is any problem building the new client instance
     */
    public final HttpClient buildClient() throws Exception {
        decorateApacheBuilder();
        return getApacheBuilder().build();
    }

    /**
     * Decorate the Apache builder as determined by this builder's parameters. Subclasses will likely add additional
     * decoration.
     * 
     * @throws Exception if there is a problem decorating the Apache builder
     */
    protected void decorateApacheBuilder() throws Exception {
        org.apache.http.impl.client.HttpClientBuilder builder = getApacheBuilder();

        if (connectionDisregardSslCertificate) {
            builder.setSSLSocketFactory(HttpClientSupport.buildNoTrustSSLConnectionSocketFactory());
        }

        if (connectionCloseAfterResponse) {
            builder.addInterceptorLast(new RequestConnectionClose());
        }

        // RequestConfig params
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

        if (socketLocalAddress != null) {
            requestConfigBuilder.setLocalAddress(socketLocalAddress);
        }

        if (socketTimeout > 0) {
            requestConfigBuilder.setSocketTimeout(socketTimeout);
        }

        if (connectionTimeout > 0) {
            requestConfigBuilder.setConnectTimeout(connectionTimeout);
        }

        requestConfigBuilder.setStaleConnectionCheckEnabled(connectionStalecheck);

        requestConfigBuilder.setRedirectsEnabled(httpFollowRedirects);

        if (connectionProxyHost != null) {
            final HttpHost proxyHost = new HttpHost(connectionProxyHost, connectionProxyPort);
            requestConfigBuilder.setProxy(proxyHost);

            if (connectionProxyUsername != null && connectionProxyPassword != null) {
                final CredentialsProvider credProvider = new BasicCredentialsProvider();
                credProvider.setCredentials(new AuthScope(connectionProxyHost, connectionProxyPort),
                        new UsernamePasswordCredentials(connectionProxyUsername, connectionProxyPassword));
                builder.setDefaultCredentialsProvider(credProvider);
            }
        }

        // ConnectionConfig params
        ConnectionConfig.Builder connectionConfigBuilder = ConnectionConfig.custom();

        connectionConfigBuilder.setBufferSize(socketBufferSize);

        if (httpContentCharSet != null) {
            connectionConfigBuilder.setCharset(CharsetUtils.get(httpContentCharSet));
        }

        builder.setDefaultRequestConfig(requestConfigBuilder.build());
        builder.setDefaultConnectionConfig(connectionConfigBuilder.build());
    }

    /**
     * Get the Apache {@link org.apache.http.impl.client.HttpClientBuilder} instance over which this builder will be
     * layered. Subclasses may override to return a specialized subclass.
     * 
     * @return the Apache HttpClientBuilder instance to use
     */
    protected org.apache.http.impl.client.HttpClientBuilder getApacheBuilder() {
        return apacheBuilder;
    }

}