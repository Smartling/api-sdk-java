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
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientBuilder.class, SSLContext.class})
public class HttpClientFactoryTest
{
    private static final String HOST = "HOST";
    private static final int PORT = 5000;
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private HttpClientFactory httpClientFactory;
    private HttpClientBuilder httpClientBuilder;
    private SSLContext context;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        httpClientFactory = spy(new HttpClientFactory());

        httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        when(httpClientFactory.getHttpClientBuilder()).thenReturn(httpClientBuilder);

        PowerMockito.when(httpClientBuilder.build()).then(RETURNS_MOCKS);

        // create the mock to return by getInstance()
        context = PowerMockito.mock(SSLContext.class);
        // mock the static method getInstance() to return above created mock context
        PowerMockito.mockStatic(SSLContext.class);

        when(SSLContext.getInstance("TLSv1.2")).thenReturn(context);
    }

    @After
    public void validate()
    {
        validateMockitoUsage();
    }

    @Test
    public void testGetHttpClientNoProxy() throws SmartlingApiException
    {
        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        PowerMockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
        assertNotNull(httpClientFactory.getHttpClient(null, null));

        verify(httpClientBuilder, never()).setProxy(any(HttpHost.class));
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
        verify(httpClientBuilder).build();
        verify(httpClientBuilder).setSSLContext(context);
    }

    @Test
    public void testGetHttpClientNoProxyConfig() throws SmartlingApiException {
        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        PowerMockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();

        assertNotNull(httpClientFactory.getHttpClient(proxyConfiguration, null));

        verify(httpClientBuilder, never()).setProxy(any(HttpHost.class));
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
        verify(httpClientBuilder).build();
        verify(httpClientBuilder).setSSLContext(context);
    }

    @Test
    public void testGetHttpClientWithProxyConfig() throws Exception
    {
        ArgumentCaptor<CredentialsProvider> credentialsProviderCaptor = ArgumentCaptor.forClass(CredentialsProvider.class);
        ArgumentCaptor<HttpHost> httpHostArgumentCaptor = ArgumentCaptor.forClass(HttpHost.class);
        PowerMockito.when(httpClientBuilder.setProxy(httpHostArgumentCaptor.capture())).thenReturn(httpClientBuilder);
        PowerMockito.when(httpClientBuilder.setDefaultCredentialsProvider(credentialsProviderCaptor.capture())).thenReturn(httpClientBuilder);

        assertNotNull(httpClientFactory.getHttpClient(fillProxyConfiguration(), null));
        HttpHost httpHost = httpHostArgumentCaptor.getValue();
        Credentials credentials = credentialsProviderCaptor.getValue().getCredentials(new AuthScope(HOST, PORT));

        assertEquals(HOST, httpHost.getHostName());
        assertEquals(PORT, httpHost.getPort());
        assertEquals(USERNAME, credentials.getUserPrincipal().getName());
        assertEquals(PASSWORD, credentials.getPassword());

        verify(httpClientBuilder).build();
    }

    @Test
    public void shouldNotConfigureAuthenticationIfUsernameIsMissing() throws Exception
    {
        ProxyConfiguration proxyConfiguration = fillProxyConfiguration();
        proxyConfiguration.setUsername(null);

        CloseableHttpClient httpClient = httpClientFactory.getHttpClient(proxyConfiguration, null);

        assertNotNull(httpClient);
        verify(httpClientBuilder).setProxy(any(HttpHost.class));
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
    }

    @Test
    public void shouldNotConfigureAuthenticationIfPasswordIsMissing() throws Exception
    {
        ProxyConfiguration proxyConfiguration = fillProxyConfiguration();
        proxyConfiguration.setPassword(null);

        CloseableHttpClient httpClient = httpClientFactory.getHttpClient(proxyConfiguration, null);

        assertNotNull(httpClient);
        verify(httpClientBuilder).setProxy(any(HttpHost.class));
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
    }

    @Test
    public void shouldConfigureHttpTimeouts() throws SmartlingApiException
    {
        ArgumentCaptor<RequestConfig> requestConfigArgumentCaptor = ArgumentCaptor.forClass(RequestConfig.class);
        HttpClientConfiguration httpClientConfiguration = httpClientConfiguration();
        when(httpClientBuilder.setDefaultRequestConfig(any(RequestConfig.class))).thenReturn(httpClientBuilder);

        CloseableHttpClient httpClient = httpClientFactory.getHttpClient(null, httpClientConfiguration);

        assertNotNull(httpClient);
        verify(httpClientBuilder).setDefaultRequestConfig(requestConfigArgumentCaptor.capture());
        verify(httpClientBuilder).build();

        RequestConfig requestConfig = requestConfigArgumentCaptor.getValue();
        assertEquals(httpClientConfiguration.getConnectionRequestTimeout(), requestConfig.getConnectionRequestTimeout());
        assertEquals(httpClientConfiguration.getConnectionTimeout(), requestConfig.getConnectTimeout());
        assertEquals(httpClientConfiguration.getSocketTimeout(), requestConfig.getSocketTimeout());
    }

    @Test
    public void shouldSkipSettingTimeoutsIfHttpClientConfigurationIsNull() throws SmartlingApiException
    {
        CloseableHttpClient httpClient = httpClientFactory.getHttpClient(null, null);

        assertNotNull(httpClient);
        verify(httpClientBuilder, never()).setDefaultRequestConfig(any(RequestConfig.class));
    }

    private static HttpClientConfiguration httpClientConfiguration()
    {
        return new HttpClientConfiguration();
    }

    private static ProxyConfiguration fillProxyConfiguration()
    {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost(HOST);
        proxyConfiguration.setPort(PORT);
        proxyConfiguration.setUsername(USERNAME);
        proxyConfiguration.setPassword(PASSWORD);

        return proxyConfiguration;
    }
}
