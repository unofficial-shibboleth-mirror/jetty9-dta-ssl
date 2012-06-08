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

package net.shibboleth.utilities.java.support.logging;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * {@link ServletContextListener} that can be used in web applications to load a logback configuration file. This
 * listener supports logback configuration locations specified either as an absolute file path or as a 'classpath:' URL.
 */
public class LogbackConfigurationListener implements ServletContextListener {

    /** Name of context parameter giving location of logback configuration file. */
    public static final String CONFIG_LOCATION_PARAM = "logbackConfiguration";

    /** URL scheme used to for file URLs. */
    public static final String FILE_URL_SCHEME = "file:";

    /** URL scheme used to for classpath URLs. */
    public static final String CLASSPATH_URL_SCHEME = "classpath:";

    /** {@inheritDoc} */
    public void contextInitialized(final ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();

        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (!(loggerFactory instanceof LoggerContext)) {
            servletContext.log("Can not configure logback. " + LoggerFactory.class + " is using " + loggerFactory
                    + " which is not an instance of " + LoggerContext.class);
            return;
        }

        final String configPath = StringSupport.trimOrNull(servletContext.getInitParameter(CONFIG_LOCATION_PARAM));
        if (configPath == null) {
            servletContext.log("Can not configure logback. Location is null." + " Maybe context param \""
                    + CONFIG_LOCATION_PARAM + "\" is not set or is not correct.");
            return;
        }

        final URL url = toUrl(servletContext, configPath);
        if (url == null) {
            servletContext.log("Can not configure logback. Could not find logback"
                    + " config neither as servlet context-, nor as" + " classpath-, nor as url-, nor as file system"
                    + " resource. Config location = \"" + configPath + "\".");
            return;
        }

        servletContext
                .log("Configuring logback. Config location = \"" + configPath + "\", full url = \"" + url + "\".");
        configure(servletContext, url, (LoggerContext) loggerFactory);
    }

    /** {@inheritDoc} */
    public void contextDestroyed(final ServletContextEvent sce) {
        final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();

        if (!(loggerFactory instanceof LoggerContext)) {
            return;
        }

        final LoggerContext loggerContext = (LoggerContext) loggerFactory;
        loggerContext.stop();
    }

    /**
     * Configures logback using the given configuration file.
     * 
     * @param servletContext current servlet context
     * @param configLocation logback configuration file location
     * @param loggerContext logger context to be configured
     */
    protected void configure(final ServletContext servletContext, final URL configLocation,
            final LoggerContext loggerContext) {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.stop();
        try {
            configurator.doConfigure(configLocation);
        } catch (JoranException e) {
            servletContext.log("Failed to configure logback.", e);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }

    /**
     * Converts the given location in to an absolute URL.
     * 
     * @param servletContext current servlet context
     * @param location logback configuration file location
     * 
     * @return the absolute URL representing the logback configuration file
     */
    protected URL toUrl(final ServletContext servletContext, final String location) {
        if (location.startsWith("/")) {
            final File file = new File(location);
            if (!file.exists()) {
                servletContext.log("Logback configuration file " + location + " does not exist");
                return null;
            }
            if (!file.canRead()) {
                servletContext.log("Logback configuration file " + location + " is not readable");
                return null;
            }
            if (!file.isFile()) {
                servletContext.log("Logback configuration file " + location + " is not a file");
                return null;
            }

            try {
                return file.toURI().toURL();
            } catch (MalformedURLException e) {
                servletContext.log("Unable to convert logback File to a URL", e);
                return null;
            }
        }

        if (location.startsWith(CLASSPATH_URL_SCHEME)) {
            return Thread.currentThread().getContextClassLoader()
                    .getResource(location.substring(CLASSPATH_URL_SCHEME.length()));
        }

        servletContext.log("Logback configuration location is neither a file path nor a classpath url");
        return null;
    }
}