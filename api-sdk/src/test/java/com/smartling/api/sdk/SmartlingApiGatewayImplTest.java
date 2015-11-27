package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.auth.AuthenticationContext;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.Response;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SmartlingApiGatewayImplTest
{

    private SmartlingApiGatewayImpl smartlingApiGatewayImpl;

    @Test
    public void testGenerateAuthenticationContext() throws SmartlingApiException, InterruptedException
    {
        smartlingApiGatewayImpl = new SmartlingApiGatewayImpl("", "", "", null);
        AuthApiClient authApiClient = mock(AuthApiClient.class);
        smartlingApiGatewayImpl.authApiClient = authApiClient;
        Response<AuthenticationContext> response = new Response<>();
        AuthenticationContext context = new AuthenticationContext();
        context.setExpiresIn(1);
        context.setAccessToken("111");
        context.setRefreshToken("222");
        response.setData(context);
        when(authApiClient.authenticate(any(AuthenticationCommand.class), any(ProxyConfiguration.class), anyString())).thenReturn(response);
        when(authApiClient.refresh(anyString(), any(ProxyConfiguration.class), anyString())).thenReturn(response);

        smartlingApiGatewayImpl.generateAuthenticationContext();
        verify(authApiClient).authenticate(new AuthenticationCommand("",""), null, "https://api.smartling.com");
        context.setRefreshExpiresIn(1000);
        smartlingApiGatewayImpl.generateAuthenticationContext();
        verify(authApiClient).refresh("222", null, "https://api.smartling.com");
        context.setRefreshExpiresIn(1);
        verify(authApiClient).authenticate(new AuthenticationCommand("",""), null, "https://api.smartling.com");

    }

}
