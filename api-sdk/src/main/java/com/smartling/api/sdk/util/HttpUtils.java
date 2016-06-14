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

import com.smartling.api.sdk.LibNameVersionHolder;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Util class for executing http calls
 */
public class HttpUtils
{
    private static final Log logger = LogFactory.getLog(HttpUtils.class);

    private static final String LOG_MESSAGE_ERROR_TEMPLATE = "GENERAL ERROR: %s";
    static final String SCHEME_HTTPS = "https";
    static final String SCHEME_HTTP = "http";
    static final String PROPERTY_SUFFIX_PROXY_HOST = ".proxyHost";
    static final String PROPERTY_SUFFIX_PROXY_PORT = ".proxyPort";
    static final String PROPERTY_SUFFIX_PROXY_USERNAME = ".proxyUsername";
    static final String PROPERTY_SUFFIX_PROXY_PASSWORD = ".proxyPassword";
    public static final String X_SL_REQUEST_ID = "X-SL-RequestId";

    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    private HttpProxyUtils httpProxyUtils;

    public void setHttpProxyUtils(HttpProxyUtils httpProxyUtils)
    {
        this.httpProxyUtils = httpProxyUtils;
    }

    public HttpUtils()
    {
        this.httpProxyUtils = new HttpProxyUtils();
    }

    public static ThreadLocal<String> getRequestId()
    {
        return requestId;
    }

    /**
     * Method for executing http calls and retrieving string response.
     * @param httpRequest request for execute
     * @param proxyConfiguration proxy configuration, if it is set to {@code NULL} proxy settings will be setup from system properties. Otherwise switched off.
     * @return {@link StringResponse} the contents of the requested file along with the encoding of the file.
     * @throws com.smartling.api.sdk.exceptions.SmartlingApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    public StringResponse executeHttpCall(final HttpRequestBase httpRequest, final ProxyConfiguration proxyConfiguration) throws SmartlingApiException
    {
        CloseableHttpClient httpClient = null;
        try
        {
            requestId.remove();

            ProxyConfiguration newProxyConfiguration = mergeSystemProxyConfiguration(proxyConfiguration);

            logProxyConfiguration(newProxyConfiguration);

            httpClient = httpProxyUtils.getHttpClient(newProxyConfiguration);

            RequestConfig proxyRequestConfig = httpProxyUtils.getProxyRequestConfig(httpRequest, newProxyConfiguration);

            if (proxyRequestConfig != null)
            {
                httpRequest.setConfig(proxyRequestConfig);
            }
            addUserAgentHeader(httpRequest);
            final HttpResponse response = httpClient.execute(httpRequest);

            final String charset = EntityUtils.getContentCharSet(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            Header header = response.getFirstHeader(X_SL_REQUEST_ID);
            if (header != null)
            {
                requestId.set(header.getValue());
            }

            return inputStreamToString(response.getEntity().getContent(), charset, statusCode);
        }
        catch (final IOException ioe)
        {
            logger.error(String.format(LOG_MESSAGE_ERROR_TEMPLATE, ioe.getMessage()));
            throw new SmartlingApiException(ioe);
        }
        finally
        {
            try
            {
                if (null != httpClient)
                    httpClient.close();
            }
            catch (final IOException ioe)
            {
                logger.warn(String.format(LOG_MESSAGE_ERROR_TEMPLATE, ioe.getMessage()));
            }
        }
    }

    private void logProxyConfiguration(ProxyConfiguration proxyConfiguration)
    {
        if (proxyConfiguration != null)
        {
            logger.info(String.format("Using proxy configuration for executing http call: '%s'", proxyConfiguration));
        }
        else
        {
            logger.info("Proxy is not setup");
        }
    }

    private StringResponse inputStreamToString(final InputStream inputStream, final String encoding, final int httpCode) throws IOException
    {
        final byte[] contentsRaw = IOUtils.toByteArray(inputStream);
        // unless UTF-16 explicitly specified, use default UTF-8 encoding.
        final String responseEncoding = (null == encoding || !encoding.toUpperCase().contains(CharEncoding.UTF_16) ? CharEncoding.UTF_8 : CharEncoding.UTF_16);
        final String contents = new String(contentsRaw, responseEncoding);
        return new StringResponse(contents, contentsRaw, responseEncoding, httpCode == HttpStatus.SC_OK);
    }

    private ProxyConfiguration mergeSystemProxyConfiguration(final ProxyConfiguration proxyConfiguration)
    {
        String protocol = defineSchemeFromSystemProperties();
        if (protocol != null)
        {
            ProxyConfiguration newProxyConfiguration = new ProxyConfiguration();
            newProxyConfiguration.setHost(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_HOST));
            newProxyConfiguration.setPort(Integer.valueOf(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PORT)));
            newProxyConfiguration.setUsername(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_USERNAME));
            newProxyConfiguration.setPassword(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PASSWORD));

            return newProxyConfiguration;
        }
        return proxyConfiguration;
    }

    private String defineSchemeFromSystemProperties()
    {
        if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_HOST)) && StringUtils.isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_PORT)))
            return SCHEME_HTTPS;

        if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_HOST)) && StringUtils.isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_PORT)))
            return SCHEME_HTTP;

        return null;
    }

    private void addUserAgentHeader(final HttpMessage httpMessage) throws SmartlingApiException
    {
        String userAgentHeaderValue = LibNameVersionHolder.getClientLibName() + "/" + LibNameVersionHolder.getClientLibVersion();
        httpMessage.addHeader(HttpHeaders.USER_AGENT, userAgentHeaderValue);
    }
}
