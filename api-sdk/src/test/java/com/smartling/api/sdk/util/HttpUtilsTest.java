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

import com.smartling.api.sdk.HttpClientConfiguration;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HttpUtilsTest
{
    private HttpUtils httpUtils;
    private HttpClientFactory httpClientFactory;

    private HttpRequestBase httpRequest;
    private ProxyConfiguration proxyConfiguration;
    private HttpClientConfiguration httpClientConfiguration;
    private CloseableHttpResponse httpResponse;
    private StatusLine statusLine;

    private static final String TEST_RESPONSE = "{\"response\":{\"data\":null,\"code\":\"VALIDATION_ERROR\",\"messages\":[\"apiKey parameter is required\"]}}";

    private static final String HOST = "host";
    private static final String PASSWORD = "password";
    private static final String PORT = "5000";
    private static final String USERNAME = "username";
    private static final String USER_AGENT = "test-artifact-id/1.0.0";

    @Before
    public void setUp() throws IllegalStateException, IOException
    {
        httpUtils = new HttpUtils();
        httpUtils.setHttpClientFactory(httpClientFactory = mock(HttpClientFactory.class));

        httpRequest = mock(HttpRequestBase.class);
        proxyConfiguration = new ProxyConfiguration();
        httpClientConfiguration = new HttpClientConfiguration();

        httpResponse = mock(CloseableHttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        statusLine = mock(StatusLine.class);

        InputStream responseStream = new ByteArrayInputStream(TEST_RESPONSE.getBytes(StandardCharsets.UTF_8));
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(responseStream);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
    }
    
    @Test
    public void testExecuteHttpCallWithProxy() throws SmartlingApiException, IOException
    {
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        setSystemProxy(HttpUtils.SCHEME_HTTP, "", "", "", "");
        setSystemProxy(HttpUtils.SCHEME_HTTPS, "", "", "", "");

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClientFactory.getHttpClient(proxyConfiguration, httpClientConfiguration)).thenReturn(httpClient);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration, httpClientConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());
        verify(httpRequest).addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
    }

    @Test
    public void testExecuteHttpCallWithProxySystemHttp() throws SmartlingApiException, IOException
    {
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        setSystemProxy(HttpUtils.SCHEME_HTTP, HOST, PORT, USERNAME, PASSWORD);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<ProxyConfiguration> proxyConfigurationCaptor = ArgumentCaptor.forClass(ProxyConfiguration.class);
        when(httpClientFactory.getHttpClient(proxyConfigurationCaptor.capture(), eq(httpClientConfiguration))).thenReturn(httpClient);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration, httpClientConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());

        ProxyConfiguration proxyConfiguration = proxyConfigurationCaptor.getValue();
        assertEquals(HOST, proxyConfiguration.getHost());
        assertEquals(Integer.parseInt(PORT), proxyConfiguration.getPort());
        assertEquals(USERNAME, proxyConfiguration.getUsername());
        assertEquals(PASSWORD, proxyConfiguration.getPassword());
    }

    @Test
    public void testExecuteHttpCallWithProxySystemHttps() throws SmartlingApiException, IOException
    {
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

        setSystemProxy(HttpUtils.SCHEME_HTTPS, HOST, PORT, USERNAME, PASSWORD);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        ArgumentCaptor<ProxyConfiguration> proxyConfigurationCaptor = ArgumentCaptor.forClass(ProxyConfiguration.class);
        when(httpClientFactory.getHttpClient(proxyConfigurationCaptor.capture(), eq(httpClientConfiguration))).thenReturn(httpClient);
        when(httpClient.execute(httpRequest)).thenReturn(httpResponse);

        StringResponse response = httpUtils.executeHttpCall(httpRequest, proxyConfiguration, httpClientConfiguration);

        assertEquals(TEST_RESPONSE, response.getContents());

        ProxyConfiguration proxyConfiguration = proxyConfigurationCaptor.getValue();
        assertEquals(HOST, proxyConfiguration.getHost());
        assertEquals(Integer.parseInt(PORT), proxyConfiguration.getPort());
        assertEquals(USERNAME, proxyConfiguration.getUsername());
        assertEquals(PASSWORD, proxyConfiguration.getPassword());
    }

    private void setSystemProxy(String protocol, String host, String port, String userName, String password)
    {
        System.setProperty(protocol + HttpUtils.PROPERTY_SUFFIX_PROXY_HOST, host);
        System.setProperty(protocol + HttpUtils.PROPERTY_SUFFIX_PROXY_PORT, port);
        System.setProperty(protocol + HttpUtils.PROPERTY_SUFFIX_PROXY_USERNAME, userName);
        System.setProperty(protocol + HttpUtils.PROPERTY_SUFFIX_PROXY_PASSWORD, password);
    }
}
