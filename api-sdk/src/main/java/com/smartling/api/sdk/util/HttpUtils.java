/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.util;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.ApiException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Util class for executing http calls
 */
public class HttpUtils
{
    private static final Log logger = LogFactory.getLog(HttpUtils.class);

    private static final String LOG_MESSAGE_ERROR_TEMPLATE = "GENERAL ERROR: %s";

    private HttpUtils()
    {
    }

    /**
     * Method for executing http calls and retrieving string response.
     * @param httpRequest request for execute
     * @param proxyConfiguration proxy configuration, if it is set to {@code NULL} proxy settings will be setup from system properties. Otherwise switched off.
     * @return {@link StringResponse} the contents of the requested file along with the encoding of the file.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    public static StringResponse executeHttpCall(HttpRequestBase httpRequest, ProxyConfiguration proxyConfiguration) throws ApiException
    {
        HttpClient httpClient = null;
        try
        {
            httpClient = new DefaultHttpClient();

            ProxyUtils.setupProxy(httpClient, proxyConfiguration);

            HttpResponse response = httpClient.execute(httpRequest);

            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK)
                return inputStreamToString(response.getEntity().getContent(), null);

            throw new ApiException(inputStreamToString(response.getEntity().getContent(), null).getContents());
        }
        catch (IOException ioe)
        {
            logger.error(String.format(LOG_MESSAGE_ERROR_TEMPLATE, ioe.getMessage()));
            throw new ApiException(ioe);
        }
        finally
        {
            if (null != httpClient)
                httpClient.getConnectionManager().shutdown();
        }
    }

    private static StringResponse inputStreamToString(InputStream inputStream, String encoding) throws IOException
    {
        StringWriter writer = new StringWriter();
        // unless UTF-16 explicitly specified, use default UTF-8 encoding.
        String responseEncoding = (null == encoding || !encoding.toUpperCase().contains(CharEncoding.UTF_16) ? CharEncoding.UTF_8 : CharEncoding.UTF_16);
        IOUtils.copy(inputStream, writer, responseEncoding);
        return new StringResponse(writer.toString(), responseEncoding);
    }

    private static class ProxyUtils
    {
        private static final String SCHEME_HTTPS                   = "https";
        private static final String SCHEME_HTTP                    = "http";
        private static final String PROPERTY_SUFFIX_PROXY_HOST     = ".proxyHost";
        private static final String PROPERTY_SUFFIX_PROXY_PORT     = ".proxyPort";
        private static final String PROPERTY_SUFFIX_PROXY_USERNAME = ".proxyUsername";
        private static final String PROPERTY_SUFFIX_PROXY_PASSWORD = ".proxyPassword";

        public static void setupProxy(HttpClient httpClient, ProxyConfiguration configuration)
        {
            if (null != configuration)
                setProxyToHttpClient(httpClient, configuration.getHost(), configuration.getPort(), configuration.getUsername(), configuration.getPassword());
            else
                setupProxyFromSystemProperties(httpClient);
        }

        public static void setupProxyFromSystemProperties(HttpClient httpClient)
        {
            String protocol = defineSchemeFromSystemProperties();

            if (null != protocol)
                setProxyToHttpClient(httpClient,
                        System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_HOST),
                        Integer.valueOf(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PORT)),
                        System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_USERNAME),
                        System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PASSWORD)
                );
        }

        private static void setProxyToHttpClient(HttpClient httpClient, String proxyHost, Integer proxyPort, String username, String password)
        {
            if (null == proxyHost || null == proxyPort)
                return;

            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password) && httpClient instanceof DefaultHttpClient)
            {
                ((DefaultHttpClient)httpClient).getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(username, password)
                );
            }

            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        private static String defineSchemeFromSystemProperties()
        {
            if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_HOST))
                    && StringUtils.isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_PORT)))
                return SCHEME_HTTPS;

            if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_HOST))
                    && StringUtils.isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_PORT)))
                return SCHEME_HTTP;

            return null;
        }
    }
}
