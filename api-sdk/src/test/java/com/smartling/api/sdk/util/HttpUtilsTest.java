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
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpUtilsTest
{
    private HttpUtils httpUtils;
    private HttpProxyUtils httpProxyUtils;

    private HttpRequestBase httpRequest;
    private ProxyConfiguration proxyConfiguration;
    private CloseableHttpResponse httpResponse;
    private HttpEntity httpEntity;
    private StatusLine statusLine;

    private static final String TEST_RESPONSE = "{\"response\":{\"data\":null,\"code\":\"VALIDATION_ERROR\",\"messages\":[\"apiKey parameter is required\"]}}";

    private static final String HOST = "host";
    private static final String PASSWORD = "password";
    private static final String PORT = "5000";
    private static final String USERNAME = "username";

    @Before
    public void setUp() throws IllegalStateException, IOException
    {
        httpUtils = new HttpUtils();
        httpUtils.setHttpProxyUtils(httpProxyUtils = mock(HttpProxyUtils.class));

        httpRequest = mock(HttpRequestBase.class);
        proxyConfiguration = new ProxyConfiguration();
        httpResponse = mock(CloseableHttpResponse.class);
        httpEntity = mock(HttpEntity.class);
        statusLine = mock(StatusLine.class);

        InputStream responseStream = new ByteArrayInputStream(TEST_RESPONSE.getBytes(StandardCharsets.UTF_8));
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(responseStream);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
    }
    
    @Test
    public void testExecuteHttpCall() throws ApiException, ClientProtocolException, IOException
    {
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpProxyUtils.getHttpClient(proxyConfiguration)).thenReturn(httpClient);
        when(httpProxyUtils.getProxyRequestConfig(httpRequest, proxyConfiguration)).thenReturn(null);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);
        
        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());
        verify(httpRequest, never()).setConfig(any(RequestConfig.class));
    }

    @Test
    public void testExecuteHttpCallWithProxy() throws ApiException, ClientProtocolException, IOException
    {
        RequestConfig requestConfig = mock(RequestConfig.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpProxyUtils.getHttpClient(any(ProxyConfiguration.class))).thenReturn(httpClient);
        when(httpProxyUtils.getProxyRequestConfig(eq(httpRequest), any(ProxyConfiguration.class))).thenReturn(requestConfig);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());
        verify(httpRequest).setConfig(requestConfig);
    }

    @Test
    public void testExecuteHttpCallWithProxySystemHttp() throws ApiException, ClientProtocolException, IOException
    {
        RequestConfig requestConfig = mock(RequestConfig.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        System.setProperty(HttpUtils.SCHEME_HTTP + HttpUtils.PROPERTY_SUFFIX_PROXY_HOST, HOST);
        System.setProperty(HttpUtils.SCHEME_HTTP + HttpUtils.PROPERTY_SUFFIX_PROXY_PORT, PORT);
        System.setProperty(HttpUtils.SCHEME_HTTP + HttpUtils.PROPERTY_SUFFIX_PROXY_USERNAME, USERNAME);
        System.setProperty(HttpUtils.SCHEME_HTTP + HttpUtils.PROPERTY_SUFFIX_PROXY_PASSWORD, PASSWORD);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<ProxyConfiguration> proxyConfigurationCaptor = ArgumentCaptor.forClass(ProxyConfiguration.class);
        when(httpProxyUtils.getHttpClient(proxyConfigurationCaptor.capture())).thenReturn(httpClient);
        when(httpProxyUtils.getProxyRequestConfig(eq(httpRequest), any(ProxyConfiguration.class))).thenReturn(requestConfig);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());
        verify(httpRequest).setConfig(requestConfig);

        ProxyConfiguration proxyConfiguration = proxyConfigurationCaptor.getValue();
        assertEquals(HOST, proxyConfiguration.getHost());
        assertEquals(Integer.parseInt(PORT), proxyConfiguration.getPort());
        assertEquals(USERNAME, proxyConfiguration.getUsername());
        assertEquals(PASSWORD, proxyConfiguration.getPassword());
    }

    @Test
    public void testExecuteHttpCallWithProxySystemHttps() throws ApiException, ClientProtocolException, IOException
    {
        RequestConfig requestConfig = mock(RequestConfig.class);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        System.setProperty(HttpUtils.SCHEME_HTTPS + HttpUtils.PROPERTY_SUFFIX_PROXY_HOST, HOST);
        System.setProperty(HttpUtils.SCHEME_HTTPS + HttpUtils.PROPERTY_SUFFIX_PROXY_PORT, PORT);
        System.setProperty(HttpUtils.SCHEME_HTTPS + HttpUtils.PROPERTY_SUFFIX_PROXY_USERNAME, USERNAME);
        System.setProperty(HttpUtils.SCHEME_HTTPS + HttpUtils.PROPERTY_SUFFIX_PROXY_PASSWORD, PASSWORD);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<ProxyConfiguration> proxyConfigurationCaptor = ArgumentCaptor.forClass(ProxyConfiguration.class);
        when(httpProxyUtils.getHttpClient(proxyConfigurationCaptor.capture())).thenReturn(httpClient);
        when(httpProxyUtils.getProxyRequestConfig(eq(httpRequest), any(ProxyConfiguration.class))).thenReturn(requestConfig);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());
        verify(httpRequest).setConfig(requestConfig);

        ProxyConfiguration proxyConfiguration = proxyConfigurationCaptor.getValue();
        assertEquals(HOST, proxyConfiguration.getHost());
        assertEquals(Integer.parseInt(PORT), proxyConfiguration.getPort());
        assertEquals(USERNAME, proxyConfiguration.getUsername());
        assertEquals(PASSWORD, proxyConfiguration.getPassword());
    }
}
