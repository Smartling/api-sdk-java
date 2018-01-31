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
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import sun.security.ssl.SSLContextImpl;

import javax.net.ssl.SSLContext;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientBuilder.class, SSLContext.class})
public class HttpProxyUtilsTest
{
    private static final String HOST = "HOST";
    private static final int PORT = 5000;
    private static final String USERNAME = "userName";
    private static final String PASSWORD = "password";
    private HttpProxyUtils httpProxyUtils;
    private HttpRequestBase httpRequest;
    private HttpClientBuilder httpClientBuilder;
    private SSLContext context;

    @Before
    public void setUp() throws NoSuchAlgorithmException {
        httpProxyUtils = spy(new HttpProxyUtils());
        httpRequest = mock(HttpRequestBase.class);

        httpClientBuilder = PowerMockito.mock(HttpClientBuilder.class);
        when(httpProxyUtils.getHttpClientBuilder()).thenReturn(httpClientBuilder);

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
    public void testGetProxyRequestConfigNoProxyConfig()
    {
        assertNull(httpProxyUtils.getProxyRequestConfig(httpRequest, null));
    }

    @Test
    public void testGetProxyRequestConfigBadProxyConfig()
    {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        assertNull(httpProxyUtils.getProxyRequestConfig(httpRequest, proxyConfiguration));
    }

    @Test
    public void testGetProxyRequestConfigWithProxyConfig()
    {
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHost(HOST);
        proxyConfiguration.setPort(PORT);
        assertNotNull(httpProxyUtils.getProxyRequestConfig(httpRequest, proxyConfiguration));
    }

    @Test
    public void testGetHttpClientNoProxy() throws SmartlingApiException, NoSuchAlgorithmException {
        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        PowerMockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
        assertNotNull(httpProxyUtils.getHttpClient(null));

        verify(httpClientBuilder).build();
        verify(httpClientBuilder).setSSLContext(context);
    }

    @Test
    public void testGetHttpClientNoProxyConfig() throws SmartlingApiException {
        CloseableHttpClient closeableHttpClient = mock(CloseableHttpClient.class);
        PowerMockito.when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
        ProxyConfiguration proxyConfiguration = new ProxyConfiguration();

        assertNotNull(httpProxyUtils.getHttpClient(proxyConfiguration));

        verify(httpClientBuilder).build();
        verify(httpClientBuilder).setSSLContext(context);
    }

    @Test
    public void testGetHttpClientWithProxyConfig() throws Exception
    {
        ArgumentCaptor<CredentialsProvider> credentialsProviderCaptor = ArgumentCaptor.forClass(CredentialsProvider.class);
        PowerMockito.when(httpClientBuilder.setDefaultCredentialsProvider(credentialsProviderCaptor.capture())).thenReturn(httpClientBuilder);

        assertNotNull(httpProxyUtils.getHttpClient(fillProxyConfiguration()));
        Credentials credentials = credentialsProviderCaptor.getValue().getCredentials(new AuthScope(HOST, PORT));
        
        assertEquals(USERNAME, credentials.getUserPrincipal().getName());
        assertEquals(PASSWORD, credentials.getPassword());

        verify(httpClientBuilder).build();
    }

    @Test
    public void shouldNotConfigureAuthenticationIfUsernameIsMissing() throws Exception
    {
        ProxyConfiguration proxyConfiguration = fillProxyConfiguration();
        proxyConfiguration.setUsername(null);

        CloseableHttpClient httpClient = httpProxyUtils.getHttpClient(proxyConfiguration);

        assertNotNull(httpClient);
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
    }

    @Test
    public void shouldNotConfigureAuthenticationIfPasswordIsMissing() throws Exception
    {
        ProxyConfiguration proxyConfiguration = fillProxyConfiguration();
        proxyConfiguration.setPassword(null);

        CloseableHttpClient httpClient = httpProxyUtils.getHttpClient(proxyConfiguration);

        assertNotNull(httpClient);
        verify(httpClientBuilder, never()).setDefaultCredentialsProvider(any(CredentialsProvider.class));
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
