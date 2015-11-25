package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.auth.AuthenticationContext;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.Response;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
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
        context.setExpiresIn(2);
        context.setAccessToken("111");
        response.setData(context);
        when(authApiClient.authenticate(any(AuthenticationCommand.class), any(ProxyConfiguration.class), anyString())).thenReturn(response);

        smartlingApiGatewayImpl.generateAuthenticationContext();
        smartlingApiGatewayImpl.expireExecutor.shutdown();
        smartlingApiGatewayImpl.expireExecutor.awaitTermination(30, TimeUnit.MINUTES);
    }

}
